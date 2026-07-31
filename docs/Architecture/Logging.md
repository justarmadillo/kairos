# Logging

> **In plain words** — *logging* is code writing short diagnostic messages while it runs, which a developer can read afterwards to work out what happened. Kairos deliberately has almost none: two `Log` calls in total, no crash reporter, no analytics. That is a privacy-driven choice — an app holding patient data should not accumulate trails — and it has a real cost, stated honestly on this page: when a background backup fails, there is little to inspect beyond the recorded backup status. See [[Architecture/Error Handling|Error Handling]].

Kairos has no centralized logging facade, structured telemetry, crash reporter, or Timber dependency.

## Production Log Calls

- Debug and release `FirebaseAppCheckInitializer.initialize()` call `Log.e("KairosAppCheck", ...)` when Firebase/App Check initialization fails, then continue so authorization can fail closed.
- `MediaFileManager.toRelative()` calls `Log.w("MediaFileManager", ...)` when a file is outside the managed media root and falls back to its basename.

Other failures are expressed through `StateFlow`, result objects, WorkManager results, or silent best-effort cleanup. Firestore error details are classified in the data layer, but the authorization UI presents generic messages.

## Operational Consequences

- Background backup and purge failures have limited diagnostics because workers return failure without logging the exception.
- No persistent event trail connects a UI error to a repository or worker operation.
- Debug App Check tokens are emitted by the Firebase debug provider rather than by application logging code.

See [[Architecture/Error Handling|Error Handling]] and [[Architecture/Background Work|Background Work]].

## Source references

- `app/src/debug/java/com/taha/kairos/FirebaseAppCheckInitializer.kt`
- `app/src/release/java/com/taha/kairos/FirebaseAppCheckInitializer.kt`
- `core/src/main/java/com/taha/kairos/core/media/MediaFileManager.kt`
- `data/src/main/java/com/taha/kairos/data/backup/ScheduledBackupWorker.kt`
- `data/src/main/java/com/taha/kairos/data/backup/TrashPurgeWorker.kt`
