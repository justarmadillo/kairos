# ConsultationSessionDao

> **In plain words** — the SQL for consultation sessions, keyed by date. Dates are stored as plain numbers (milliseconds since 1970), which is why "find the session for this day" is a simple numeric lookup and "sessions in this range" is a simple comparison — no date parsing in SQL at all.

## Purpose

Defines Room access to date-keyed consultation sessions and their active-case counts.

## Responsibilities

- Insert, find by date, and get sessions.
- Observe active sessions in a date range with case counts.
- Soft-delete, restore, observe trash, and purge expired sessions.

## Dependencies

- `ConsultationSessionEntity`, `SessionWithCount`, Room, and [[Components/Databases/KairosDatabase]].

## Called By

- [[Components/Repositories/ConsultationRepository]]
- [[Components/Workers/TrashPurgeWorker]]

## Calls

- SQL over `consultation_sessions`, `consultation_cases`, and `cases`.

## Important Methods

- `insert` uses `IGNORE` to support lookup-or-create logic.
- `findByDate` and `getById` return plain entities.
- `observeForRange(start, end)` includes both range endpoints and computes active-case counts.
- `softDelete`, `restore`, `observeTrashed`, and `purgeOlderThan` manage retention.

## Design Patterns

- Room DAO, date-range reactive projection, and soft delete.

## Common Pitfalls

- `date` is indexed but not unique; repository-level coordination prevents normal duplicates.
- The range uses SQL `BETWEEN`, which is inclusive at both ends.
- Direct lookup can return a soft-deleted session.

## Related Pages

- [[Components/Repositories/ConsultationRepository]]
- [[Components/Mappers/ConsultationMapper]]
- [[Features/Consultation Calendar]]

## Source References

- `data/src/main/java/com/taha/kairos/data/db/dao/ConsultationSessionDao.kt`
- `data/src/main/java/com/taha/kairos/data/db/entities/ConsultationEntities.kt`
- `data/src/main/java/com/taha/kairos/data/db/relations/Counts.kt`
