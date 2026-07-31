# Managers Index

> **In plain words** — two small classes that own one shared resource each. `MediaFileManager` is the single authority on where attachment files live and how relative database paths become real file paths. `WorkerScheduler` is the single place background jobs are registered. Centralising each resource means there is exactly one answer to "how is this done", instead of five slightly different ones.

- [[Components/Managers/MediaFileManager]] — attachment paths, files, and FileProvider URIs.
- [[Components/Managers/WorkerScheduler]] — unique periodic WorkManager registration.

## Source References

- `core/src/main/java/com/taha/kairos/core/media/MediaFileManager.kt`
- `data/src/main/java/com/taha/kairos/data/backup/WorkerScheduler.kt`

