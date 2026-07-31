# DataSafetyCoordinator

> **In plain words** — one lock for the whole app. Some operations must never overlap: saving a case while a backup is being written would capture a half-finished record. Anything that spans the database *and* the filesystem takes this lock first, so those operations queue up instead of colliding. It allows *re-entry* — the same coroutine already holding the lock can take it again — which prevents a routine from deadlocking against itself. See [[Learn/Design Patterns Glossary|Design Patterns Glossary]].

## Purpose

Serializes database mutations and filesystem snapshots that must not overlap backup, restore, purge, or multi-repository saves.

## Responsibilities

- Provide one process-wide suspendable data lock.
- Permit nested acquisition by the same coroutine context.
- Protect operations that span Room and the filesystem.

## Dependencies

- Kotlin coroutine `Mutex`, `withLock`, and a private `CoroutineContext` marker.
- Hilt binds `DataSafetyCoordinatorImpl` as a singleton.

## Called By

- Mutating implementations of [[Components/Repositories/PatientRepository]], [[Components/Repositories/CaseRepository]], [[Components/Repositories/DiagnosisRepository]], [[Components/Repositories/MediaRepository]], [[Components/Repositories/ShiftRepository]], and [[Components/Repositories/ConsultationRepository]].
- [[Components/Services/BackupEngine]] and [[Components/Workers/TrashPurgeWorker]].
- [[Components/ViewModels/PatientCaseViewModel]] and [[Components/ViewModels/CaseDetailViewModel]] for aggregate save/export boundaries.

## Calls

- `Mutex.withLock` for the outer acquisition.
- `withContext(DataLockElement())` to mark nested calls as already protected.

## Important Methods

- `withDataLock(block)` executes immediately when `DataLockElement` already exists; otherwise it acquires the singleton mutex and installs that marker.

## Design Patterns

- Re-entrant coroutine-scoped mutex.
- Coarse-grained coordinator around infrastructure operations.
- Dependency-inverted contract in `:core` with implementation in `:data`.

## Common Pitfalls

- Re-entrancy follows coroutine context propagation; launching detached work inside a locked block loses the marker.
- The lock serializes operations but does not combine multiple repository Room transactions into one rollback unit.
- Long exports and restores intentionally block writes and other protected snapshots.

## Related Pages

- [[Architecture/Data Flow]]
- [[Components/Services/BackupEngine]]
- [[Execution Flows/Database Operations]]

## Source References

- `core/src/main/java/com/taha/kairos/core/repository/DataSafetyCoordinator.kt`
- `data/src/main/java/com/taha/kairos/data/backup/DataSafetyCoordinatorImpl.kt`
- `data/src/main/java/com/taha/kairos/data/di/DataModule.kt`
