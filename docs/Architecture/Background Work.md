# Background Work

> **In plain words** — some work must happen even when nobody is looking at the app: making the scheduled backup, and permanently removing records that have sat in the trash long enough. Android will not let an app just run whenever it likes, so these are handed to **WorkManager**, which stores the request and runs it later under the conditions given (here: not when the battery is low), surviving app closure and reboot. Two details worth noting: a *unique name* stops the same job being registered twice, and both jobs first check that the device is still authorized — a locked app quietly does nothing rather than mutating data. See [[Learn/Android App Basics|Android App Basics]].

WorkManager runs two unique periodic jobs. `KairosApplication` registers trash purge at process start and observes settings to register, update, or cancel scheduled backup.

| Job | Unique name | Cadence | Policy | Constraint |
| --- | --- | --- | --- | --- |
| Scheduled backup | `kairos_scheduled_backup` | Daily, weekly, or 30-day monthly | `UPDATE` | Battery not low |
| Trash purge | `kairos_trash_purge` | Daily | `KEEP` | Battery not low |

Both Hilt workers first call `CachedAuthorizationGuard.hasCachedAccess()`. A locked app returns success without backup, pruning, or mutation; only explicit user export bypasses the gate.

`ScheduledBackupWorker` reads settings once, exports through `BackupEngine`, records outcome, prunes generations best-effort, and posts a low-priority notification when permission allows. It never requests an immediate retry.

`TrashPurgeWorker` takes the data-safety lock, removes records soft-deleted for at least 30 days, cleans orphan diagnoses, then deletes known and unreferenced media files. Database deletion precedes file deletion so interruption leaves database references consistent.

See [[Execution Flows/Background Jobs|Background Jobs]], [[Components/Managers/WorkerScheduler|WorkerScheduler]], [[Components/Workers/ScheduledBackupWorker|ScheduledBackupWorker]], and [[Components/Workers/TrashPurgeWorker|TrashPurgeWorker]].

## Source references

- `app/src/main/java/com/taha/kairos/KairosApplication.kt`
- `data/src/main/java/com/taha/kairos/data/backup/WorkerScheduler.kt`
- `data/src/main/java/com/taha/kairos/data/backup/ScheduledBackupWorker.kt`
- `data/src/main/java/com/taha/kairos/data/backup/TrashPurgeWorker.kt`
- `data/src/main/java/com/taha/kairos/data/authorization/CachedAuthorizationGuard.kt`
