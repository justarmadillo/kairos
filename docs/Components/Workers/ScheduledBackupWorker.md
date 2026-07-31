# ScheduledBackupWorker

> **In plain words** — the job that makes backups on a schedule, without the app being open. It checks authorization first, exports through the backup engine, records whether it worked (which is what the dashboard's backup warning reads), prunes old backups, and posts a quiet notification. It deliberately never asks to retry immediately: a failing backup that retries in a loop would drain the battery, and the next scheduled run will try again anyway. See [[Architecture/Background Work|Background Work]].

## Purpose

Runs automatic backup export, retention pruning, outcome persistence, and user notification on the configured periodic schedule.

## Responsibilities

- Skip safely when no valid cached device authorization exists.
- Read the current backup destination once per run.
- Export a backup and record its result.
- Prune old Kairos backups after successful export.
- Post a low-priority completion or failure notification when permitted.

## Dependencies

- [[Components/Services/BackupEngine]], [[Components/Repositories/SettingsRepository]], and [[Components/Utilities/CachedAuthorizationGuard]].
- WorkManager, DocumentFile, and Android notifications.

## Called By

- WorkManager unique periodic work registered by [[Components/Managers/WorkerScheduler]].

## Calls

- `CachedAuthorizationGuard.hasCachedAccess`.
- `SettingsRepository.observeSettings().first()` and `recordBackupRun`.
- `BackupEngine.export` and [[Components/Utilities/BackupPruner]].
- Android `NotificationManager`.

## Important Methods

- `doWork()` returns success for a locked-app no-op, failure for missing URI/export failure, and never requests an immediate retry.
- `pruneOldBackups(folderUri)` deletes only names selected by `BackupPruner` and treats pruning as best-effort.
- `notify(success, error)` respects Android 13 notification permission.

## Design Patterns

- Hilt-assisted `CoroutineWorker`.
- Idempotent periodic work with one-shot settings read.
- Authorization guard and best-effort secondary cleanup.

## Common Pitfalls

- A missing folder URI returns `Result.failure`; the next periodic occurrence is the next retry.
- No network constraint is required because backup is local.
- A locked run returns success without exporting or recording a backup result.
- Notification absence does not imply backup failure.

## Related Pages

- [[Components/Managers/WorkerScheduler]]
- [[Components/Repositories/BackupRepository]]
- [[Execution Flows/Background Jobs]]

## Source References

- `data/src/main/java/com/taha/kairos/data/backup/ScheduledBackupWorker.kt`
- `data/src/main/java/com/taha/kairos/data/backup/WorkerScheduler.kt`

