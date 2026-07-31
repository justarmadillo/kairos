# BackupRepository

> **In plain words** — the contract for "make a backup", "restore a backup", and "compact the database". Callers get back a **result object** describing what happened rather than an exception, because backup failure is an expected outcome to display, not a crash. All the messy detail — ZIP files, folder permissions, copying media — stays behind this interface in [[Components/Services/BackupEngine|BackupEngine]]. That hiding of a subsystem behind one simple entry point is called a *facade*.

## Purpose

Defines the app-facing contract for complete backup export, restore, and SQLite maintenance. [[Components/Services/BackupEngine]] contains the implementation details.

## Responsibilities

- Return structured success or failure for export and restore.
- Keep callers independent of SAF, ZIP, Room, and filesystem mechanics.
- Expose database vacuuming as an explicit maintenance operation.

## Dependencies

- Contract-owned `BackupResult` and `RestoreResult` data classes.
- Hilt binds [[Components/Services/BackupEngine]] as the singleton implementation.

## Called By

- [[Components/ViewModels/SettingsViewModel]]
- [[Components/ViewModels/AuthorizationGateViewModel]] for locked-state emergency export.

## Calls

- Through Hilt, delegates to `BackupEngine.export`, `restore`, or `vacuumDatabase`.

## Important Methods

- `export(folderUri)` returns a timestamped `BackupResult`.
- `restore(zipUri)` returns `RestoreResult` and requires an app restart after success.
- `vacuumDatabase()` reclaims and defragments SQLite space.

## Design Patterns

- Port-and-adapter boundary around a complex infrastructure service.
- Result objects for expected operational failures.

## Common Pitfalls

- The URI strings must reference accessible SAF locations with persisted permission.
- Emergency export is intentionally available while device authorization is locked; automatic backup is not.
- `vacuumDatabase` throws rather than returning a result object.

## Related Pages

- [[Components/Services/BackupEngine]]
- [[Components/Workers/ScheduledBackupWorker]]
- [[Execution Flows/Background Jobs]]

## Source References

- `core/src/main/java/com/taha/kairos/core/repository/BackupRepository.kt`
- `data/src/main/java/com/taha/kairos/data/backup/BackupEngine.kt`
- `data/src/main/java/com/taha/kairos/data/di/DataModule.kt`

