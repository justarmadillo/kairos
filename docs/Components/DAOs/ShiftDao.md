# ShiftDao

> **In plain words** — the SQL for shifts. The case count beside each shift is calculated inside the query (counting rows in the link table, excluding trashed cases) rather than stored anywhere, so it is always correct by construction.

## Purpose

Defines Room access to shifts and computed active-case counts.

## Responsibilities

- Insert, update, get, and observe shifts.
- Emit active shifts in reverse date order with case counts.
- Soft-delete, restore, purge, and count active shifts.

## Dependencies

- `ShiftEntity`, `ShiftWithCount`, Room, and [[Components/Databases/KairosDatabase]].

## Called By

- [[Components/Repositories/ShiftRepository]]
- [[Components/Repositories/DashboardRepository]]
- [[Components/Workers/TrashPurgeWorker]]

## Calls

- SQL over `shifts`, `shift_cases`, and `cases`.

## Important Methods

- `insert`, `update`, `getById`, and `observeById` handle plain entities.
- `observeAll` calculates each shift's non-deleted case count.
- `softDelete`, `restore`, `observeTrashed`, and `purgeOlderThan` implement retention.
- `observeTotalShifts` emits the active count.

## Design Patterns

- Room DAO, correlated count projection, reactive list, and soft delete.

## Common Pitfalls

- Direct ID reads do not filter deleted shifts.
- Purging a shift cascades only its junction rows; linked cases remain.
- Counts ignore soft-deleted cases.

## Related Pages

- [[Components/Repositories/ShiftRepository]]
- [[Components/Mappers/ShiftMapper]]
- [[Components/DAOs/CaseDao]]

## Source References

- `data/src/main/java/com/taha/kairos/data/db/dao/ShiftDao.kt`
- `data/src/main/java/com/taha/kairos/data/db/entities/ShiftEntities.kt`
- `data/src/main/java/com/taha/kairos/data/db/relations/Counts.kt`

