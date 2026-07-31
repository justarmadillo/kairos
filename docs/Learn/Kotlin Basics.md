# Kotlin Basics

Every piece of Kotlin syntax that actually appears in Kairos, explained once. Assumes [[Learn/Programming Fundamentals|Programming Fundamentals]].

Kotlin is the language Google recommends for Android. It runs on the Java virtual machine, but it is far more concise and much safer about `null`.

## Package and imports

Every file starts like this:

```kotlin
package com.taha.kairos.core.model

import kotlinx.coroutines.flow.Flow
```

`package` is the file's address, matching its folder path. `import` pulls in a name defined elsewhere so you can use it short. Imports are noise — skip them when reading a file for the first time.

## data class

```kotlin
data class Case(
    val id: Long = 0,
    val patientId: Long,
    val patient: Patient? = null,
    val caseDate: Long,
    val mechanism: String? = null,
    val diagnoses: List<Diagnosis> = emptyList(),
)
```

This is the real `Case` (trimmed). Notes:

- `= 0`, `= null`, `= emptyList()` are **default values**. You may omit those arguments when creating a `Case`.
- `id: Long = 0` is a Kairos convention meaning *not saved yet*. A real saved case always has an id above zero.
- `patient: Patient? = null` — a case may carry its full patient, or just the `patientId`, depending on how it was loaded.

Creating one uses **named arguments**, which is why Kairos code reads almost like English:

```kotlin
val c = Case(patientId = 7, caseDate = now, mechanism = "Fall from height")
```

## interface — a contract with no body

```kotlin
interface CaseRepository {
    suspend fun getById(id: Long): Case?
    fun observeTrashed(): Flow<List<Case>>
}
```

An interface lists *what can be asked for* without saying *how it is done*. Somewhere else, a class fulfils the contract:

```kotlin
class CaseRepositoryImpl(...) : CaseRepository {
    override suspend fun getById(id: Long): Case? { /* real work */ }
}
```

`: CaseRepository` means "implements". `override` marks a function that fulfils one of the contract's requirements. The `Impl` suffix is a naming convention for "the implementation".

This split is the single most important structural idea in Kairos. Screens depend on the *interface* only, so the database can be replaced, faked in a test, or rewritten, without a screen noticing. See [[Learn/Architecture Patterns|Architecture Patterns]].

## Nullability

```kotlin
val patient: Patient? = null    // may be absent
val name: String = "Sara"       // never absent, guaranteed

patient?.name            // safe call → String?, null if patient is null
patient?.name ?: "—"     // elvis operator → fallback value
patient!!.name           // force. Crashes if null. Avoid.
uri?.let { use(it) }     // run the block only if not null
```

`?.let { }` appears in `MainActivity` when a folder picker returns a location: do something *only if* the user actually picked one.

## Lambdas — functions passed as values

```kotlin
IconButton(onClick = onNavigateBack) { ... }
items(state.cases) { case -> CaseCard(case = case) }
```

A **lambda** is a block of code in `{ }` handed to something else to run later. `onClick` receives "what to do when tapped". `items` receives "how to draw one row". Two shorthands you must recognise:

- If a lambda has one parameter and you do not name it, it is called `it`.
- If a lambda is the *last* argument, it can be written outside the parentheses — which is why Compose code looks like nested blocks rather than function calls.

```kotlin
onCaseClick = { onCaseClick(case.id) }      // a lambda taking nothing
onTabSelected = { dest -> navigate(dest) }  // a lambda taking one named parameter
```

A **function type** written `() -> Unit` means "a function that takes nothing and returns nothing". `(Long) -> Unit` takes a `Long`. Kairos screens declare navigation callbacks this way: `onCaseClick: (caseId: Long) -> Unit`.

## suspend

```kotlin
suspend fun getById(id: Long): Case?
```

`suspend` marks a function that may take real time — a database read, a network call — and is therefore allowed to *pause* without freezing the app. It can only be called from other `suspend` functions or from a coroutine. Full explanation: [[Learn/Coroutines And Flow|Coroutines And Flow]].

## enum class — a fixed set of options

```kotlin
enum class MediaType { IMAGE, VIDEO, AUDIO, FILE }
```

A value of this type is one of exactly those four. No typos possible, and `when` can check every case exhaustively. `ThemeMode` (LIGHT/DARK/SYSTEM) works the same way.

## sealed class / sealed interface — options that carry data

Where an enum's options are bare labels, a sealed type's options can each carry different information:

```kotlin
AuthorizationAccessState.InitialChecking     // no extra data
AuthorizationAccessState.Granted             // no extra data
AuthorizationAccessState.Locked(reason, ...) // carries why it locked
```

`MainActivity` branches on exactly this to decide whether to show the splash, the app, or the locked screen. Sealed types are how Kairos models "one of several mutually exclusive situations" safely.

## object and companion object

```kotlin
object DatabaseModule { ... }

companion object {
    const val EXTRA_WIDGET_DESTINATION = "com.taha.kairos.widget.DESTINATION"
}
```

`object` declares a **singleton**: exactly one instance exists for the whole app. `companion object` is a singleton attached to a class, used for constants and helpers that belong to the class rather than to one instance of it. `const val` is a fixed value known at compile time.

## Extension functions

```kotlin
fun Case.toEntity(now: Long): CaseEntity = CaseEntity(...)
```

Read as: "add a `toEntity` function *onto* the `Case` type". Now anywhere you hold a case you can write `case.toEntity(now)`, even though `Case` itself knows nothing about databases. This keeps the domain model clean while still giving convenient syntax. Kairos's [[Layers/Mappers|mappers]] are built entirely from extension functions.

Inside such a function, `this` is the receiver, and you can use its properties directly — that is why `CaseMapper.kt` can write `id = id`.

## Single-expression functions

```kotlin
fun DiagnosisEntity.toDomain(): Diagnosis = Diagnosis(id = id, name = name, caseCount = 0)
```

When a function is just one expression, `= value` replaces `{ return value }`.

## Annotations — the `@` markers

```kotlin
@HiltViewModel
@Entity(tableName = "cases")
@Composable
```

An annotation is a label attached to code that some tool reads. It changes nothing by itself; it instructs a generator or framework. Which tool reads which label:

| Annotation | Read by | Meaning |
|---|---|---|
| `@Composable` | Compose | this function draws UI |
| `@Entity`, `@Dao`, `@Query` | Room | this describes a database table or query |
| `@HiltViewModel`, `@Inject`, `@Module`, `@Provides`, `@Binds` | Hilt | wiring instructions |
| `@HiltAndroidApp`, `@AndroidEntryPoint` | Hilt | entry points where wiring is installed |
| `@HiltWorker` | Hilt + WorkManager | background job that needs wiring |

Annotations are why so much Kairos code appears to have no implementation: a code generator writes the real implementation at build time. See [[Learn/Dependency Injection Explained|Dependency Injection Explained]] and [[Learn/Databases And Room|Databases And Room]].

## String templates and raw strings

```kotlin
"No cases tagged with ${state.diagnosisName}"
```

`${...}` inserts a value into text. Triple-quoted strings hold multi-line text verbatim, which is how SQL is embedded in DAOs:

```kotlin
@Query("""
    SELECT * FROM cases WHERE is_deleted = 1 ORDER BY deleted_at DESC
""")
```

## Generics — the angle brackets

`List<Case>` means "a list *of* cases". `Flow<List<Case>>` means "a live stream *of* lists *of* cases". The brackets always answer "of what?". Read them from the inside out.

## Related pages

- [[Learn/Programming Fundamentals|Programming Fundamentals]]
- [[Learn/Coroutines And Flow|Coroutines And Flow]]
- [[Learn/Jetpack Compose Basics|Jetpack Compose Basics]]
- [[Learn/Code Tour One Feature|Code Tour One Feature]]
