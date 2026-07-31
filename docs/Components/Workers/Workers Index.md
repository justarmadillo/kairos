# Workers Index

> **In plain words** — a *worker* is a job that runs without anyone looking at the app: here, the scheduled backup and the trash purge. Android decides when to run them, and they survive the app being closed or the phone rebooting. Both check device authorization first and do nothing if the app is locked. See [[Architecture/Background Work|Background Work]] and [[Learn/Android App Basics|Android App Basics]].

WorkManager runs two Hilt-injected periodic jobs. Both fail closed through [[Components/Utilities/CachedAuthorizationGuard]].

- [[Components/Workers/ScheduledBackupWorker]]
- [[Components/Workers/TrashPurgeWorker]]

Scheduling is owned by [[Components/Managers/WorkerScheduler]].

## Source References

- `data/src/main/java/com/taha/kairos/data/backup/ScheduledBackupWorker.kt`
- `data/src/main/java/com/taha/kairos/data/backup/TrashPurgeWorker.kt`
- `data/src/main/java/com/taha/kairos/data/backup/WorkerScheduler.kt`

