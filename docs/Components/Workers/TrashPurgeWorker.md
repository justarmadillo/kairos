# TrashPurgeWorker

> **In plain words** — the job that makes deletion final, once a record has sat in the trash for 30 days. It runs daily, takes the global data lock so it cannot collide with a save or a backup, removes the rows, cleans up diagnoses left with no cases, and only *then* deletes the media files. That order is deliberate: if it is interrupted, the worst outcome is an unused file on disk, never a record pointing at a file that no longer exists.

## Purpose

Permanently removes soft-deleted records older than 30 days and reconciles attachment files with surviving database references.

## Responsibilities

- Skip all mutation without a valid cached authorization lease.
- Capture attachment paths before cascading case deletion.
- Hard-delete expired cases and purge eligible patients, shifts, and sessions.
- Delete orphan diagnoses and unreferenced attachment files.
- Serialize the entire operation against save, export, backup, and restore work.

## Dependencies

- All six DAO pages in [[Components/DAOs/DAOs Index]].
- [[Components/Managers/MediaFileManager]], [[Components/Repositories/DataSafetyCoordinator]], and [[Components/Utilities/CachedAuthorizationGuard]].
- WorkManager and `Dispatchers.IO`.

## Called By

- WorkManager unique daily work registered by [[Components/Managers/WorkerScheduler]].

## Calls

- `CaseDao.listExpiredTrash` / `hardDelete` and `CaseMediaDao` path queries.
- DAO purge methods and `DiagnosisDao.deleteOrphaned`.
- `MediaFileManager.delete`, `deleteCaseDir`, and `rootDir` traversal.

## Important Methods

- `doWork()` applies the 30-day threshold under the global data lock.
- `purgeUnreferencedMediaFiles()` compares disk files with all `case_media.file_path` values and removes empty directories.

## Design Patterns

- Hilt-assisted `CoroutineWorker`.
- Database-first deletion followed by idempotent filesystem reconciliation.
- Fail-closed authorization and coarse-grained mutual exclusion.

## Common Pitfalls

- `cases/0/` is deliberately ignored as a legacy pending-media prefix.
- File deletion is best-effort; later runs retry through the orphan scan.
- Returning failure does not roll back already committed DAO deletes.
- Patient purge rules and foreign-key cascades can remove additional trashed cases; the final orphan scan cleans their files.

## Related Pages

- [[Architecture/Background Work]]
- [[Components/Repositories/CaseRepository]]
- [[Components/Managers/MediaFileManager]]

## Source References

- `data/src/main/java/com/taha/kairos/data/backup/TrashPurgeWorker.kt`
- `data/src/main/java/com/taha/kairos/data/db/dao/`
