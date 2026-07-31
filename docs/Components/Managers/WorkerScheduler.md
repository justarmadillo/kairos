# WorkerScheduler

> **In plain words** — the one place background jobs are registered. Each job is registered under a **unique name**, so asking twice cannot produce two copies. The two policies differ deliberately: the purge job uses *keep* (leave any existing registration alone), while the backup job uses *update* (replace it, because a changed schedule must take effect). Selecting "off" cancels the backup registration entirely.

## Purpose

Translates backup settings and retention policy into unique periodic WorkManager registrations.

## Responsibilities

- Schedule, update, or cancel periodic backup work.
- Schedule the daily trash purge once without duplicating it.
- Apply battery-not-low constraints and stable unique work names.

## Dependencies

- Application `Context`, WorkManager, `BackupSchedule`, [[Components/Workers/ScheduledBackupWorker]], and [[Components/Workers/TrashPurgeWorker]].

## Called By

- `KairosApplication.onCreate`.
- The application also calls `scheduleBackup` whenever `SettingsRepository` emits a changed backup schedule.

## Calls

- `WorkManager.cancelUniqueWork` and `enqueueUniquePeriodicWork`.
- `PeriodicWorkRequestBuilder` with battery-not-low constraints.

## Important Methods

- `scheduleBackup(schedule)` maps daily to 1 day, weekly to 7 days, monthly to 30 days, and off to cancellation; it uses `ExistingPeriodicWorkPolicy.UPDATE`.
- `scheduleTrashPurge()` enqueues daily work with `KEEP`.

## Design Patterns

- Singleton scheduling facade.
- Unique, idempotent background-work registration.
- Settings-driven rescheduling.

## Common Pitfalls

- `MONTHLY` means every 30 days, not a calendar date.
- WorkManager periodic execution is inexact and constraint-dependent.
- The scheduler does not validate a backup folder; the worker handles missing configuration.

## Related Pages

- [[Architecture/Background Work]]
- [[Components/Repositories/SettingsRepository]]
- [[Execution Flows/App Startup]]

## Source References

- `data/src/main/java/com/taha/kairos/data/backup/WorkerScheduler.kt`
- `app/src/main/java/com/taha/kairos/KairosApplication.kt`
