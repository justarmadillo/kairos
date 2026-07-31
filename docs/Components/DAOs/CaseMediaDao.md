# CaseMediaDao

> **In plain words** — the SQL for attachment *records*, not the files themselves. Note the query that lists every referenced file path: it is what lets backup know which files to include, and lets cleanup identify orphaned files that no record points at any more.

## Purpose

Defines Room access to attachment metadata; attachment bytes remain in app-managed files.

## Responsibilities

- Insert, fetch, observe, list, and delete attachment rows.
- Enumerate every referenced file path for backup and orphan cleanup.
- Clear and assign a case's primary attachment flag.

## Dependencies

- `CaseMediaEntity`, Room, and [[Components/Databases/KairosDatabase]].

## Called By

- [[Components/Repositories/MediaRepository]]
- [[Components/Services/BackupEngine]]
- [[Components/Workers/TrashPurgeWorker]]

## Calls

- SQL against `case_media`.

## Important Methods

- `insert`, `deleteById`, and `getById` perform row operations.
- `observeForCase` orders attachments by creation time.
- `listForCase` supports purge preparation.
- `listAllFilePaths` drives referenced-only backup and orphan detection.
- `clearPrimary` and `setPrimary` are intended to run in one transaction.

## Design Patterns

- Metadata DAO separated from filesystem storage.
- Reactive case-scoped list and command pair for primary selection.

## Common Pitfalls

- The schema has no database-level unique constraint enforcing one primary row per case.
- `setPrimary` returns zero when the media ID does not belong to the supplied case; repository code checks this.
- Deleting a row never deletes the corresponding file by itself.

## Related Pages

- [[Components/Repositories/MediaRepository]]
- [[Components/Managers/MediaFileManager]]
- [[Components/Workers/TrashPurgeWorker]]

## Source References

- `data/src/main/java/com/taha/kairos/data/db/dao/CaseMediaDao.kt`
- `data/src/main/java/com/taha/kairos/data/db/entities/CaseEntities.kt`

