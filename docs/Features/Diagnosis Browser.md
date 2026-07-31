# Diagnosis Browser

> **In plain words** — the index of every diagnosis with how many active cases each has. Note the deliberate split in how it works: **sorting** happens in the database (a different query runs when you change the sort), while **filtering** as you type happens in memory on the already-loaded list. Sorting affects what to fetch; filtering only affects what to show, and doing it in memory keeps typing instant. Renaming is a single atomic repository operation because a half-renamed diagnosis would corrupt every case linked to it.

## Purpose

Provide a reusable diagnosis index with active case counts, local filtering, sorting, creation, and safe rename behavior.

## User Flow

The user opens Cases, searches or sorts the diagnosis list, taps a diagnosis to open its [[Features/Case Feed|Case Feed]], creates a standalone diagnosis, renames an existing one, or starts a new case from the floating action button.

## Execution Flow

`DiagnosisBrowseViewModel` switches `DiagnosisRepository.observeAll(sort)` whenever sort changes, then applies a case-insensitive substring filter in memory. Rename trims input, calls the atomic repository operation, and maps each `DiagnosisRenameResult` to dismissal or a field error.

## Important Classes

- `DiagnosisBrowseScreen` and `DiagnosisRow`.
- `DiagnosisBrowseViewModel`, `DiagnosisBrowseUiState`, and `DiagnosisRenameUiState`.
- `Diagnosis`, `DiagnosisSortMode`, and `DiagnosisRenameResult`.

## Related ViewModels

- [[Components/ViewModels/DiagnosisBrowseViewModel|DiagnosisBrowseViewModel]]
- [[Components/ViewModels/CaseFeedViewModel|CaseFeedViewModel]]

## Related Repositories

- [[Components/Repositories/DiagnosisRepository|DiagnosisRepository]]

## API Calls

There is no network API. Calls are `observeAll(sort)`, `getOrCreate(trimmedName)`, and `rename(id, trimmedName)`.

## State Flow

`_sortMode`, `_query`, `_rename`, and the repository Flow are combined into one `DiagnosisBrowseUiState`. Repository sorting happens before local query filtering. The state is shared while subscribed with a five-second stop timeout.

## Navigation

- Top-level route: `cases`.
- Diagnosis row: `case_feed/{diagnosisId}?name={encodedName}`.
- Add-case FAB: `patient_case`.

## Design Decisions

- Rename is case-insensitive and atomic at the repository boundary; saving cannot be dismissed mid-operation.
- Creating a diagnosis closes the dialog immediately and does not surface repository failure.
- The ViewModel starts with `ALPHABETICAL` every time. The persisted “Default diagnosis sort” setting is currently not read here.
- Case counts come from the repository projection, so unused diagnoses remain visible with zero cases.

## Related Pages

- [[Features/Case Feed|Case Feed]]
- [[Features/Patient and Case Capture|Patient and Case Capture]]
- [[Features/Settings and Backup|Settings and Backup]]
- [[Architecture/State Management]]

## Source references

- `features/src/main/java/com/taha/kairos/features/cases/DiagnosisBrowseScreen.kt`
- `features/src/main/java/com/taha/kairos/features/cases/DiagnosisBrowseViewModel.kt`
- `core/src/main/java/com/taha/kairos/core/model/Diagnosis.kt`
- `core/src/main/java/com/taha/kairos/core/repository/DiagnosisRepository.kt`
- `data/src/main/java/com/taha/kairos/data/repository/DiagnosisRepositoryImpl.kt`
- `app/src/main/java/com/taha/kairos/navigation/KairosNavHost.kt`
