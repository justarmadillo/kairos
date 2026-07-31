# DiagnosisBrowseViewModel

> **In plain words** — the brain behind the diagnosis list. Changing the sort switches to a different database query; typing in the filter box just narrows the list already in memory. That division — the database decides *what to fetch*, memory decides *what to show* — is worth internalising, as it recurs throughout the app. Rename produces an explicit outcome so the screen can show a precise error rather than failing quietly.

## Purpose

Drive diagnosis browsing, local filtering, repository sorting, creation, and rename validation.

## Responsibilities

- Observe diagnoses under the selected sort.
- Filter names locally by query.
- Create or reuse a diagnosis.
- Own the rename dialog state and map domain results to field feedback.

## Dependencies

- [[Components/Repositories/DiagnosisRepository|DiagnosisRepository]]
- `DiagnosisSortMode` and `DiagnosisRenameResult`.

## Called By

`DiagnosisBrowseScreen` collects `ui`; toolbar/search/row/dialog actions call the corresponding methods.

## Calls

- `DiagnosisRepository.observeAll(sort)`.
- `getOrCreate(trimmedName)`.
- `rename(diagnosisId, trimmedName)`.

## Important Methods

- `setQuery(q)` and `setSortMode(mode)`.
- `addDiagnosis(name)` — ignores blank input, creates/reuses, then clears search.
- `startRenaming(diagnosis)` / `updateRenameName(name)` / `dismissRename()`.
- `confirmRename()` — validates, locks dialog editing, then maps every repository result.

## Design Patterns

- `@HiltViewModel` constructor injection.
- `flatMapLatest` for sort-dependent repository Flow.
- Local derived filtering through `combine`.
- Explicit editor sub-state with command/result mapping.
- Cancellation is rethrown during rename; unexpected errors become UI text.

## Common Pitfalls

- Initial sort is hardcoded `ALPHABETICAL`; `AppSettings.diagnosisSortMode` is not consumed.
- Add failure is uncaught and the screen closes its dialog immediately.
- Query filtering is substring-based and occurs after repository sorting.
- While renaming, dismiss and editing are intentionally disabled.

## Related Pages

- [[Features/Diagnosis Browser|Diagnosis Browser]]
- [[Components/ViewModels/CaseFeedViewModel|CaseFeedViewModel]]
- [[Features/Settings and Backup|Settings and Backup]]
- [[Architecture/Error Handling]]

## Source references

- `features/src/main/java/com/taha/kairos/features/cases/DiagnosisBrowseViewModel.kt`
- `features/src/main/java/com/taha/kairos/features/cases/DiagnosisBrowseScreen.kt`
- `core/src/main/java/com/taha/kairos/core/repository/DiagnosisRepository.kt`
- `data/src/main/java/com/taha/kairos/data/repository/DiagnosisRepositoryImpl.kt`
