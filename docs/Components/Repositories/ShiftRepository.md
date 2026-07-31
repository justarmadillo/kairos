# ShiftRepository

> **In plain words** — the doorway to shifts. Note that the case *count* shown next to each shift is computed by the database query, not stored on the shift — so it can never drift out of date when a case is linked, unlinked, or trashed. Storing a count would be faster and wrong; computing it is the right trade.

## Purpose

Persists shift records and exposes their active-case counts.

## Responsibilities

- Insert or update shifts.
- Retrieve and observe shifts.
- Expose the reverse-chronological active shift list.
- Soft-delete, restore, and observe trashed shifts.

## Dependencies

- [[Components/DAOs/ShiftDao]], [[Components/Repositories/DataSafetyCoordinator]], and [[Components/Mappers/ShiftMapper]].
- `Shift` and Kotlin `Flow`.

## Called By

- [[Components/ViewModels/ShiftsViewModel]]
- [[Components/ViewModels/ShiftDetailViewModel]]
- [[Components/ViewModels/TrashViewModel]]

## Calls

- `ShiftDao` insert, update, lookup, list, soft-delete, restore, and trash queries.

## Important Methods

- `upsert(shift)` chooses insert or update from `id`.
- `observeAll()` maps `ShiftWithCount` projections.
- `observeById(id)` observes one entity.
- `softDelete`, `restore`, and `observeTrashed` manage trash state.

## Design Patterns

- Repository abstraction, command/query split, reactive `Flow`, and soft delete.
- Global mutation serialization through [[Components/Repositories/DataSafetyCoordinator]].

## Common Pitfalls

- Updating an existing shift maps only domain fields and resets unused sync metadata to entity defaults.
- `getById` and `observeById` map plain entities and therefore report `caseCount = 0`; only `observeAll` calculates the count.
- Deleting a shift does not delete its linked cases.

## Related Pages

- [[Components/DAOs/ShiftDao]]
- [[Features/Shift Management]]
- [[Components/Repositories/CaseRepository]]

## Source References

- `core/src/main/java/com/taha/kairos/core/repository/ShiftRepository.kt`
- `data/src/main/java/com/taha/kairos/data/repository/ShiftRepositoryImpl.kt`
- `data/src/main/java/com/taha/kairos/data/di/DataModule.kt`
