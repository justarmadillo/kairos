# PatientCaseViewModel

> **In plain words** — the biggest ViewModel in the app, behind the data-entry form. Unlike the read-only screens, it holds **staged** state: everything you have typed and every file you have attached but not yet saved. Saving then has to commit a patient, phones, a case, diagnoses, media, and possibly a shift or session link, in order, without leaving anything half-done. Read this page before changing anything about saving — the ordering is deliberate. See [[Learn/Architecture Patterns|Architecture Patterns]].

## Purpose

Own the combined new-patient, existing-patient case, and case-edit workflow, including media staging and resilient aggregate save.

## Responsibilities

- Hold and validate all patient/case form state.
- Load an existing case, patient, diagnoses, and saved media for editing.
- Search/select an existing patient and autocomplete diagnoses.
- Stage, remove, and select primary media.
- Control audio recording and elapsed time.
- Persist patient, case/diagnosis/shift/session links, and attachment edits under the data lock.
- Clean up unsaved files.

## Dependencies

- [[Components/Repositories/PatientRepository|PatientRepository]]
- [[Components/Repositories/CaseRepository|CaseRepository]]
- [[Components/Repositories/DiagnosisRepository|DiagnosisRepository]]
- [[Components/Repositories/MediaRepository|MediaRepository]]
- [[Components/Managers/MediaFileManager|MediaFileManager]]
- [[Components/Services/AudioRecorderEngine|AudioRecorderEngine]]
- [[Components/Repositories/DataSafetyCoordinator|DataSafetyCoordinator]]

## Called By

`PatientCaseScreen`, `NewPatientTab`, and `ExistingPatientTab` collect `state` and bind all form, search, media, recorder, and save callbacks. Shift, consultation, case-detail, and widget navigation all reach this screen.

## Calls

- Patient: `search(query)` and `upsert(patient)`.
- Case: `getById(caseId)` and `upsertCase(case, diagnosisNames, linkShiftId, linkSessionId)`.
- Diagnosis: `searchByPrefix(query, 8)`.
- Media: `applyCaseEdits(caseId, additions, removedIds, existingPrimaryId)`.
- File/recording helpers: new managed file, relative path conversion, start/stop/cancel recording.

## Important Methods

- `loadCase(caseId)` — guarded, retryable form hydration.
- Field methods: `setName`, `setAge`, `setCaseDate`, `setMechanism`, `setNotes`, phone add/remove.
- Diagnosis methods: `setDiagnosisQuery`, `selectDiagnosis`, `removeDiagnosis`.
- Patient methods: `setSearchQuery`, `selectExistingPatient`, `clearSelectedPatient`.
- Media methods: `attachFile`, `removeMedia`, `setPrimaryMedia`, `beginMediaImport`, `endMediaImport`.
- Recorder methods: `startRecording`, `stopRecording`, `cancelRecording`.
- `save(linkShiftId, linkSessionId)` — validation and multi-stage persistence.
- `onCleared()` — cancel recording and delete uncommitted temporary files.

## Design Patterns

- Aggregate editor with one immutable `PatientCaseUiState` exposed as read-only `StateFlow`.
- `@HiltViewModel` repository abstraction and injected media helpers.
- Staged filesystem work plus transactional database media edit.
- Reentrant global data lock prevents backup/restore from observing a partial file/database operation.
- Non-cancellable IO commit and retained persisted IDs provide retry without duplicate aggregates.
- Guard methods make the form immutable during load/failure/save.

## Common Pitfalls

- Editing patient fields updates the shared patient referenced by other cases.
- Patient search switches immediately per keystroke; diagnosis suggestion jobs are not cancelled and can arrive out of order.
- `clearSelectedPatient()` has no current screen action.
- The patient/case/media sequence is not one Room transaction; retry fields compensate for a later media failure.
- Process death can still interrupt the non-cancellable in-process commit and leave temporary artifacts.
- State retains pending objects that reference deleted source files for the short interval between successful save and navigation.
- Save rejects an active recording and an in-progress import.

## Related Pages

- [[Features/Patient and Case Capture|Patient and Case Capture]]
- [[Features/Media Capture and Playback|Media Capture and Playback]]
- [[Components/ViewModels/CaseDetailViewModel|CaseDetailViewModel]]
- [[Execution Flows/Database Operations]]
- [[Architecture/State Management]]

## Source references

- `features/src/main/java/com/taha/kairos/features/patient/PatientCaseViewModel.kt`
- `features/src/main/java/com/taha/kairos/features/patient/PatientCaseScreen.kt`
- `features/src/main/java/com/taha/kairos/features/patient/NewPatientTab.kt`
- `features/src/main/java/com/taha/kairos/features/patient/ExistingPatientTab.kt`
- `core/src/main/java/com/taha/kairos/core/repository/PatientRepository.kt`
- `core/src/main/java/com/taha/kairos/core/repository/CaseRepository.kt`
- `core/src/main/java/com/taha/kairos/core/repository/MediaRepository.kt`
- `data/src/main/java/com/taha/kairos/data/backup/DataSafetyCoordinatorImpl.kt`
