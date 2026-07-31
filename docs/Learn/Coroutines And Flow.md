# Coroutines And Flow

How Kairos does slow work without freezing, and how screens stay up to date automatically.

## The problem

An app has one **main thread**, and only that thread may touch the screen. It redraws roughly 60 times a second. If you do something slow on it — read a database, write a file, call a server — the screen freezes and Android may kill the app.

So slow work must happen elsewhere, and its result must come back to the main thread to be displayed. Historically this meant callbacks inside callbacks; Kotlin solves it with coroutines.

## suspend — a function that can pause

```kotlin
suspend fun getById(id: Long): Case?
```

A `suspend` function may pause partway through, release the thread so other work runs, and resume later where it left off. Code you write reads top-to-bottom as if it were blocking, but it never blocks:

```kotlin
suspend fun loadCase(id: Long) {
    val case = caseRepo.getById(id)   // pauses here, thread is free
    ui.value = CaseUiState(case)      // resumes here when the data arrives
}
```

The rule: **a `suspend` function can only be called from another `suspend` function or from inside a coroutine.** The compiler enforces this, which is why `suspend` propagates up through repositories and DAOs. In Kairos, every write (`upsertCase`, `softDelete`, `restore`) is `suspend`.

## Coroutines and scopes

A **coroutine** is one running unit of such work. It is started in a **scope**, which owns it and can cancel it:

```kotlin
viewModelScope.launch {
    caseRepo.softDelete(caseId)
}
```

`viewModelScope` is tied to the ViewModel's life. When the user leaves the screen and the ViewModel is destroyed, anything still running in that scope is cancelled automatically — no leaks, no results delivered to a dead screen. This is **structured concurrency**: work is always owned by something.

`KairosApplication` creates its own long-lived scope for app-wide work:

```kotlin
private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
```

- `SupervisorJob` — if one child coroutine fails, its siblings keep running.
- `Dispatchers.Default` — run on a background thread pool for CPU work.

Common dispatchers: `Main` (UI), `IO` (disk and network), `Default` (CPU-bound). Room and DataStore already move their work off the main thread, so Kairos rarely needs to specify one.

## Flow — a stream of values over time

A `suspend` function returns **one** result. A `Flow` delivers **many, over time**.

```kotlin
fun observeTrashed(): Flow<List<Case>>
```

Read as: "a live stream of *the current list* of trashed cases". Every time the underlying table changes, a fresh list arrives. Nothing polls; nothing refreshes manually.

This is the backbone of Kairos's UI. Room can return a `Flow` from any query, and it re-emits whenever the queried tables change. So:

*delete a case → the row changes → Room re-runs the query → a new list flows to the ViewModel → the ViewModel produces new UI state → Compose redraws the list.*

Nobody wrote "refresh the screen".

## Transforming a Flow

```kotlin
caseRepo
    .observeByDiagnosis(diagnosisId)
    .map { cases -> CaseFeedUiState(cases = cases, isLoading = false) }
```

`map` converts each emission into something else — here, raw cases into a ready-to-render UI state. Other operators you will meet in Kairos:

| Operator | Effect |
|---|---|
| `map` | transform each emission |
| `distinctUntilChanged` | drop repeats of the same value |
| `combine` | merge several flows into one, re-emitting when any changes |
| `collect` | actually consume the stream (a `suspend` call) |
| `stateIn` | convert a cold Flow into a hot `StateFlow` |

`KairosApplication` uses two of them together:

```kotlin
settingsRepository.observeSettings()
    .map { it.backupSchedule }
    .distinctUntilChanged()
    .collect { schedule -> workerScheduler.scheduleBackup(schedule) }
```

Read it as one sentence: *watch the settings; look only at the backup schedule; ignore changes that leave it the same; whenever it really changes, re-register the backup job.*

## StateFlow — a Flow with a current value

A plain `Flow` is **cold**: it does nothing until someone collects it, and each collector triggers separate work. A `StateFlow` is **hot** and always holds a current value, so a screen that subscribes gets something to draw immediately.

The conversion happens with `stateIn`:

```kotlin
val ui: StateFlow<CaseFeedUiState> = caseRepo
    .observeByDiagnosis(diagnosisId)
    .map { ... }
    .stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        CaseFeedUiState(diagnosisName = diagnosisName),
    )
```

The three arguments:

1. **scope** — `viewModelScope`, so it dies with the ViewModel.
2. **sharing policy** — `WhileSubscribed(5_000)`: keep the upstream database query alive while a screen is watching, plus five seconds. That grace period means a screen rotation does not tear down and re-run the query.
3. **initial value** — what the screen shows in the very first frame, before any data arrives.

This exact pattern appears in nearly every Kairos ViewModel. Recognise it once and you can read them all.

## Cancellation

Coroutines are cancellable, and cancellation is cooperative: it takes effect at suspension points. Because scopes cancel their children, leaving a screen stops its queries. You do not manage this by hand — you just make sure work runs in the right scope.

## Mental model summary

| Need | Tool |
|---|---|
| one result, may be slow | `suspend fun` |
| many results over time | `Flow` |
| current value always available, for UI | `StateFlow` |
| start work | `scope.launch { }` |
| consume a stream | `.collect { }` |
| stream → UI state | `.map { }.stateIn(...)` |

## Related pages

- [[Architecture/State Management|State Management]]
- [[Architecture/Data Flow|Data Flow]]
- [[Layers/ViewModels|ViewModels]]
- [[Execution Flows/State Updates|State Updates]]
