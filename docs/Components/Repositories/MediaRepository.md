# MediaRepository

> **In plain words** — the doorway to attachments, and the place where two worlds must stay in step: rows in the database and files on disk. It also enforces that a case has at most one *primary* image (the one shown on the case card). Because it spans database and filesystem, its operations are the kind that must not overlap a backup — hence the data lock elsewhere in the app. See [[Learn/Data Storage Choices|Data Storage Choices]].

## Purpose

Coordinates attachment metadata in Room with attachment files managed by [[Components/Managers/MediaFileManager]].

## Responsibilities

- Add and delete attachment rows.
- Enforce one selected primary attachment per case during repository operations.
- Apply a complete attachment edit atomically at the database level.
- Expose a reactive attachment list for a case.
- Remove files after successful metadata deletion.

## Dependencies

- [[Components/DAOs/CaseMediaDao]], [[Components/Databases/KairosDatabase]], [[Components/Managers/MediaFileManager]], and [[Components/Repositories/DataSafetyCoordinator]].
- `MediaItem` and mapping functions in [[Components/Mappers/CaseMapper]].

## Called By

- [[Components/ViewModels/PatientCaseViewModel]]
- [[Components/ViewModels/CaseDetailViewModel]]

## Calls

- `CaseMediaDao` insert, lookup, delete, list, clear-primary, and set-primary methods.
- `KairosDatabase.withTransaction` for primary selection and batch edits.
- `MediaFileManager.delete` after database commits.

## Important Methods

- `add(item)` inserts one metadata row.
- `delete(id)` deletes metadata first and then its file.
- `setPrimary(caseId, mediaId)` clears existing flags and verifies that the selected row belongs to the case.
- `applyCaseEdits(caseId, additions, removedIds, existingPrimaryId)` validates ownership and primary invariants, commits metadata changes, then best-effort deletes removed files.
- `observeForCase(caseId)` emits mapped media metadata.

## Design Patterns

- Database-first consistency for non-transactional filesystem cleanup.
- Validate-then-transaction batch command.
- Repository abstraction and reactive query.

## Common Pitfalls

- Callers must copy new files into the media root before `applyCaseEdits`.
- Filesystem changes cannot participate in the Room transaction; interrupted cleanup is retried by [[Components/Workers/TrashPurgeWorker]].
- Direct `add` does not clear another primary row.
- `observeForCase` returns stored relative paths, unlike case reads that resolve absolute paths.

## Related Pages

- [[Components/Repositories/CaseRepository]]
- [[Components/Managers/MediaFileManager]]
- [[Execution Flows/Database Operations]]

## Source References

- `core/src/main/java/com/taha/kairos/core/repository/MediaRepository.kt`
- `data/src/main/java/com/taha/kairos/data/repository/MediaRepositoryImpl.kt`
- `data/src/main/java/com/taha/kairos/data/di/DataModule.kt`
