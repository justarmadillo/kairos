# ShiftsViewModel

> **In plain words** — the brain behind the shift list. Worth noting how *undo* works: deleting a shift performs a real soft delete immediately and remembers the ID so the snackbar can restore it. Nothing is held back "just in case" — the delete really happens, and undo is simply the inverse operation.

## Purpose

Own the active shift list, add-shift dialog, and one-item soft-delete undo state.

## Responsibilities

- Continuously collect active shifts.
- Open/close the add dialog.
- Insert a dated/labeled shift and return its ID for navigation.
- Soft-delete a shift and retain it for snackbar undo.
- Restore or clear the most recently deleted shift.

## Dependencies

- [[Components/Repositories/ShiftRepository|ShiftRepository]]
- `viewModelScope` and `MutableStateFlow`.

## Called By

`ShiftsListScreen` collects `ui`, drives `AddShiftDialog`, invokes list deletion, and handles the snackbar result.

## Calls

- `ShiftRepository.observeAll()`.
- `upsert(Shift)`.
- `softDelete(shift.id)` and `restore(shift.id)`.

## Important Methods

- `openAddDialog()` / `closeAddDialog()`.
- `addShift(date, label, onCreated)` — trims an optional label, saves, closes, then returns the ID.
- `softDelete(shift)` — deletes before exposing undo state.
- `undoDelete()` / `clearUndo()`.

## Design Patterns

- `@HiltViewModel` with repository interface injection.
- Manual repository collection into immutable copies of `ShiftsUiState`.
- Callback after successful creation to separate persistence from navigation.
- Soft-delete with compensating restore.

## Common Pitfalls

- Repository exceptions are uncaught and have no UI state.
- Only one deleted shift is retained; a later deletion can replace the earlier undo opportunity.
- `ui` is exposed as `StateFlow` through a custom getter rather than `asStateFlow()`.
- The `LocalDate` and `ZoneId` imports are unused.

## Related Pages

- [[Features/Shift Management|Shift Management]]
- [[Components/ViewModels/ShiftDetailViewModel|ShiftDetailViewModel]]
- [[Features/Trash and Retention|Trash and Retention]]
- [[Architecture/State Management]]

## Source references

- `features/src/main/java/com/taha/kairos/features/shifts/ShiftsViewModel.kt`
- `features/src/main/java/com/taha/kairos/features/shifts/ShiftsListScreen.kt`
- `features/src/main/java/com/taha/kairos/features/shifts/AddShiftDialog.kt`
- `core/src/main/java/com/taha/kairos/core/repository/ShiftRepository.kt`
