# ShiftDetailViewModel

> **In plain words** — the brain behind one shift's page. The distinction it enforces matters: **unlinking** a case removes only the link row between shift and case, while the case itself remains untouched; deleting the shift likewise does not delete the cases that were grouped under it.

## Purpose

Expose one shift and its linked cases, and handle shift deletion or case unlinking.

## Responsibilities

- Read `shiftId` from navigation state.
- Observe the shift record and linked case list.
- Soft-delete the current shift.
- Remove a case/shift association without deleting the case.

## Dependencies

- `SavedStateHandle`
- [[Components/Repositories/ShiftRepository|ShiftRepository]]
- [[Components/Repositories/CaseRepository|CaseRepository]]

## Called By

`ShiftDetailScreen` collects `ui`, calls `deleteShift()` from the toolbar, and calls `unlinkCase()` on a case-card long press.

## Calls

- `ShiftRepository.observeById(shiftId)` and `softDelete(shiftId)`.
- `CaseRepository.observeByShift(shiftId)` and `unlinkFromShift(caseId, shiftId)`.

## Important Methods

- Initializer — starts two independent repository collectors for a valid ID.
- `deleteShift(onDeleted)` — soft-deletes, then invokes the navigation callback.
- `unlinkCase(caseId)` — deletes only the cross-reference.

## Design Patterns

- Navigation arguments through `SavedStateHandle`.
- `@HiltViewModel` constructor injection.
- Independent reactive sources merged by immutable `MutableStateFlow.update` copies.
- Callback separates completed mutation from navigation.

## Common Pitfalls

- Missing `shiftId` becomes `-1`; state stops loading but exposes no explicit error.
- The two collectors have no coordinated initial-load or error state.
- Delete/unlink exceptions are uncaught.
- Long-press unlink and toolbar delete have no confirmation or undo in detail.

## Related Pages

- [[Features/Shift Management|Shift Management]]
- [[Components/ViewModels/ShiftsViewModel|ShiftsViewModel]]
- [[Components/ViewModels/PatientCaseViewModel|PatientCaseViewModel]]
- [[Architecture/Navigation]]

## Source references

- `features/src/main/java/com/taha/kairos/features/shifts/ShiftDetailViewModel.kt`
- `features/src/main/java/com/taha/kairos/features/shifts/ShiftDetailScreen.kt`
- `core/src/main/java/com/taha/kairos/core/repository/ShiftRepository.kt`
- `core/src/main/java/com/taha/kairos/core/repository/CaseRepository.kt`
