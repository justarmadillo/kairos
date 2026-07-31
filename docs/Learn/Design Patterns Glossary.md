# Design Patterns Glossary

A **design pattern** is a named, reusable solution to a recurring problem. Naming them lets engineers say in two words what would otherwise take a paragraph. Every pattern below is actually used in Kairos, with the file that uses it.

## Repository

**Problem:** screens need data, but should not know whether it comes from SQLite, a file, or a server.
**Solution:** an object that owns one slice of data and exposes intention-revealing operations.

```kotlin
interface CaseRepository {
    suspend fun getById(id: Long): Case?
    fun observeTrashed(): Flow<List<Case>>
}
```

*In Kairos:* twelve of them, interfaces in `:core`, implementations in `:data`. See [[Layers/Repositories|Repositories]].

## DAO — Data Access Object

**Problem:** database access scattered through the codebase is unauditable.
**Solution:** one interface per table area, each function exactly one database operation, no business rules.

*In Kairos:* `CaseDao`, `PatientDao`, `DiagnosisDao`, `CaseMediaDao`, `ShiftDao`, `ConsultationSessionDao`. Room generates the implementations. See [[Components/DAOs/DAOs Index|DAOs]].

The distinction from Repository matters: a DAO knows SQL and nothing else; a repository knows *the rules* and coordinates DAOs, files, and locks.

## Mapper

**Problem:** the same concept needs different shapes in different layers, and converting them ad hoc spreads knowledge everywhere.
**Solution:** one place that converts between shapes.

```kotlin
fun CaseWithRelations.toDomain(): Case
fun Case.toEntity(now: Long): CaseEntity
```

*In Kairos:* five mapper files of extension functions. They are the only code that knows both `CaseEntity` and `Case`. See [[Layers/Mappers|Mappers]].

## Singleton

**Problem:** some things must exist exactly once — the database, the settings store.
**Solution:** one instance, shared.

*In Kairos:* `@Singleton` on the Hilt provider for `KairosDatabase`, on every repository implementation, and on `PreferencesStore`. Kotlin's `object` keyword is the language-level version, used for `DatabaseModule` and `Migrations`.

## Observer / reactive streams

**Problem:** many parts of the UI must react to the same changing data, and polling is wasteful and stale.
**Solution:** publish a stream; interested parties subscribe and are pushed updates.

*In Kairos:* Room `Flow` queries → repository → `StateFlow` in a ViewModel → `collectAsStateWithLifecycle` in a composable. It is the backbone of the whole app. See [[Learn/Coroutines And Flow|Coroutines And Flow]].

## Dependency injection / inversion of control

**Problem:** classes that build their own dependencies are untestable and duplicate setup.
**Solution:** declare needs in the constructor; a container supplies them.

*In Kairos:* Hilt. See [[Learn/Dependency Injection Explained|Dependency Injection Explained]].

## Unidirectional data flow

**Problem:** when both the screen and the model can mutate state, they drift apart.
**Solution:** state flows down, events flow up, one owner of truth.

*In Kairos:* every screen/ViewModel pair. See [[Architecture/State Management|State Management]].

## State hoisting

**Problem:** a component holding its own state cannot be controlled, reused, or previewed.
**Solution:** move the state to the caller; the component receives values and emits events.

*In Kairos:* `CaseCard(case = case, onClick = ...)`, and every screen taking `onNavigateBack`/`onCaseClick` callbacks instead of a `NavController`.

## Gate / guard

**Problem:** protected content must not be composed at all until a condition holds.
**Solution:** a wrapper that decides between "checking", "allowed", and "blocked", and only builds the real content in the allowed branch.

*In Kairos:* `DeviceAuthorizationGate` in `MainActivity`, plus `CachedAuthorizationGuard` protecting background mutations. See [[Features/Device Authorization|Device Authorization]].

## Soft delete (logical deletion)

**Problem:** users delete things by accident, and hard deletion is irreversible.
**Solution:** mark the row deleted, hide it from normal queries, purge it later.

*In Kairos:* `is_deleted` / `deleted_at` columns, trash screen, `TrashPurgeWorker`. See [[Features/Trash and Retention|Trash and Retention]].

## Unit of work / transaction

**Problem:** a logical operation spanning several writes can be interrupted halfway.
**Solution:** group the writes so they commit together or not at all.

*In Kairos:* `db.withTransaction { }` in `upsertCase`. See [[Learn/Databases And Room|Databases And Room]].

## Lock / mutual exclusion

**Problem:** two things writing at once — a save and a backup — can produce a torn, inconsistent snapshot.
**Solution:** serialise them behind one lock.

*In Kairos:* `DataSafetyCoordinator.withDataLock { }` wrapping case writes and backup runs. See [[Components/Repositories/DataSafetyCoordinator|DataSafetyCoordinator]].

## Idempotency

**Problem:** an operation that runs twice must not do damage twice.
**Solution:** make repeating it a no-op.

*In Kairos:* `@Insert(onConflict = OnConflictStrategy.IGNORE)` on junction links — linking the same case to the same shift twice changes nothing. Also WorkManager's `KEEP` policy so re-registering the purge job never duplicates it.

## Get-or-create (upsert)

**Problem:** you need an entity that may or may not already exist, without duplicates.
**Solution:** look it up; create only if absent; handle the race where someone else created it first.

*In Kairos:* `getOrCreateDiagnosis` — case-insensitive lookup, insert, and a second lookup if the insert was rejected.

## Facade

**Problem:** a subsystem has many moving parts, but callers want one simple entry point.
**Solution:** a single class hiding the parts.

*In Kairos:* `BackupEngine` behind the `BackupRepository` interface — callers ask for a backup; zipping, media copying, verification, and pruning stay inside.

## Strategy (via enum + `when`)

**Problem:** behaviour must vary by a user choice.
**Solution:** name the options and branch once.

*In Kairos:* `ThemeMode`, `BackupSchedule`, `DiagnosisSortMode` — each a small enum the code branches on exactly once.

## Slot / composition over inheritance

**Problem:** a reusable component needs caller-supplied content.
**Solution:** accept content as a parameter rather than requiring subclassing.

*In Kairos:* `Scaffold(topBar = { }, bottomBar = { }) { }` and the gate's `authorizedContent: @Composable () -> Unit`.

## Related pages

- [[Learn/Architecture Patterns|Architecture Patterns]]
- [[Learn/Glossary|Glossary]]
- [[Components/Components Index|Components]]
