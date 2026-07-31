# CaseFeedViewModel

> **In plain words** — the simplest ViewModel in the app, and therefore the best one to read first: take the diagnosis ID that navigation supplied, subscribe to the cases carrying it, and reshape each update into screen state. About ten meaningful lines. It is dissected line by line in [[Learn/Code Tour One Feature|Code Tour One Feature]].

## Purpose

Expose the active cases associated with the diagnosis selected by navigation.

## Responsibilities

- Read diagnosis ID and display name from `SavedStateHandle`.
- Observe cases for a valid diagnosis.
- Provide loading, title, and list state to the feed.

## Dependencies

- `SavedStateHandle`
- [[Components/Repositories/CaseRepository|CaseRepository]]

## Called By

`CaseFeedScreen` obtains it from the `case_feed` navigation entry and collects `ui`.

## Calls

`CaseRepository.observeByDiagnosis(diagnosisId)`.

## Important Methods

The ViewModel has no public command methods. Its initializer constructs `ui` as either a fixed invalid-argument Flow or the mapped repository Flow.

## Design Patterns

- Navigation arguments through `SavedStateHandle`.
- `@HiltViewModel` and repository contract injection.
- Read-only projection Flow converted to `StateFlow` with `SharingStarted.WhileSubscribed(5_000)`.
- Immutable `CaseFeedUiState`.

## Common Pitfalls

- Missing ID uses `-1` and silently produces an empty state.
- The diagnosis name is a route snapshot, not an observed diagnosis; rename does not update an open title.
- There is no error state or retry command.
- In the invalid-ID branch, the emitted fixed state drops the initially captured diagnosis name.

## Related Pages

- [[Features/Case Feed|Case Feed]]
- [[Features/Diagnosis Browser|Diagnosis Browser]]
- [[Components/ViewModels/CaseDetailViewModel|CaseDetailViewModel]]
- [[Architecture/Navigation]]

## Source references

- `features/src/main/java/com/taha/kairos/features/cases/CaseFeedViewModel.kt`
- `features/src/main/java/com/taha/kairos/features/cases/CaseFeedScreen.kt`
- `core/src/main/java/com/taha/kairos/core/repository/CaseRepository.kt`
- `app/src/main/java/com/taha/kairos/navigation/KairosNavHost.kt`
