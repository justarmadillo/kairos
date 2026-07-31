# CaseRepository

> **In plain words** — the doorway to cases, and the busiest class in the app. Saving a case is not one write but several: the case row, its diagnosis links (creating diagnoses that do not exist yet), and optionally a link to a shift or session — all inside one transaction so a crash cannot leave half a case. Two things to remember: attachments are **not** saved here (that is [[Components/Repositories/MediaRepository|MediaRepository]]), and media paths handed out are absolute while stored ones are relative. Traced end to end in [[Learn/Code Tour One Feature|Code Tour One Feature]].

## Purpose

Persists and queries the central `Case` aggregate, including diagnoses, patient data, media metadata, and optional shift or consultation links.

## Responsibilities

- Insert or update a case and replace its diagnosis junctions atomically.
- Get or create diagnosis rows during case persistence.
- Link cases to shifts and consultation sessions.
- Query cases by ID, diagnosis, shift, or session.
- Soft-delete, restore, and observe trashed cases.
- Resolve stored relative attachment paths for UI consumers.

## Dependencies

- [[Components/DAOs/CaseDao]], [[Components/DAOs/DiagnosisDao]], and [[Components/Databases/KairosDatabase]].
- [[Components/Managers/MediaFileManager]], [[Components/Repositories/DataSafetyCoordinator]], and [[Components/Mappers/CaseMapper]].

## Called By

- [[Components/ViewModels/PatientCaseViewModel]]
- [[Components/ViewModels/CaseDetailViewModel]]
- [[Components/ViewModels/CaseFeedViewModel]]
- [[Components/ViewModels/ShiftDetailViewModel]]
- [[Components/ViewModels/ConsultationViewModel]]
- [[Components/ViewModels/TrashViewModel]]

## Calls

- `CaseDao` for case rows, junctions, aggregate queries, and trash updates.
- `DiagnosisDao` for case-insensitive lookup-or-insert.
- `KairosDatabase.withTransaction` for each case write.
- `MediaFileManager.resolve` when mapping database paths for presentation.

## Important Methods

- `upsertCase(case, diagnosisNames, linkShiftId, linkSessionId)` writes the case, replaces diagnosis links, and optionally inserts shift/session junctions.
- `getById` / `observeById` load `CaseWithRelations`.
- `observeByDiagnosis`, `observeByShift`, and `observeBySession` expose reactive filtered feeds.
- `unlinkFromShift` / `unlinkFromSession` remove only the selected junction.
- `softDelete`, `restore`, and `observeTrashed` manage trash state.

## Design Patterns

- Repository and aggregate-root patterns.
- Transactional junction replacement and idempotent `INSERT IGNORE` links.
- Reactive Room queries mapped into domain models.
- Logical deletion with deferred physical cleanup.

## Common Pitfalls

- `upsertCase` does not persist attachment rows; [[Components/Repositories/MediaRepository]] is a separate step.
- Diagnosis names are trimmed, but blank names are not explicitly rejected here.
- Returned media paths are absolute, while persisted `case_media.file_path` values are relative.
- The full patient/case/media save is globally serialized but spans several Room transactions; see [[Components/ViewModels/PatientCaseViewModel]].

## Related Pages

- [[Components/Repositories/MediaRepository]]
- [[Components/Repositories/DiagnosisRepository]]
- [[Components/DAOs/CaseDao]]
- [[Execution Flows/Data Loading]]

## Source References

- `core/src/main/java/com/taha/kairos/core/repository/CaseRepository.kt`
- `data/src/main/java/com/taha/kairos/data/repository/CaseRepositoryImpl.kt`
- `data/src/main/java/com/taha/kairos/data/di/DataModule.kt`
