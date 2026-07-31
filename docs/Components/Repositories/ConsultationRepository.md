# ConsultationRepository

> **In plain words** — the doorway to consultation sessions, which are simply "the consultation held on this date". Sessions are created lazily, only when a case is about to be attached. The subtle rule worth noticing: if a session for that date already exists **in the trash**, it is restored rather than duplicated — otherwise you would end up with two sessions for one day.

## Purpose

Persists date-keyed consultation sessions and exposes sessions with active-case counts.

## Responsibilities

- Get or create the session for an epoch-millisecond day boundary.
- Restore a same-date session when it already exists in trash.
- Query sessions by ID or date range.
- Soft-delete, restore, and observe trashed sessions.

## Dependencies

- [[Components/DAOs/ConsultationSessionDao]], [[Components/Databases/KairosDatabase]], [[Components/Repositories/DataSafetyCoordinator]], and [[Components/Mappers/ConsultationMapper]].

## Called By

- [[Components/ViewModels/ConsultationViewModel]]
- [[Components/ViewModels/TrashViewModel]]

## Calls

- `ConsultationSessionDao` lookup, insert, range, soft-delete, restore, and trash methods.
- `KairosDatabase.withTransaction` for date-based lookup-or-create.

## Important Methods

- `getOrCreateForDate(dateMillis)` restores an existing soft-deleted row or inserts and race-safe refetches one.
- `getById(id)` returns a one-shot domain session.
- `observeForDateRange(startMillis, endMillis)` maps count projections.
- `softDelete`, `restore`, and `observeTrashed` manage trash state.

## Design Patterns

- Date-keyed repository with idempotent lookup-or-create.
- Transactional race fallback around `INSERT IGNORE`.
- Reactive projection and soft-delete patterns.

## Common Pitfalls

- Callers must normalize `dateMillis` to the intended local day boundary; the repository does not do it.
- The schema has a non-unique date index, so application transaction logic—not a unique constraint—provides normal deduplication.
- `getById` maps a plain entity and reports `caseCount = 0`.

## Related Pages

- [[Components/DAOs/ConsultationSessionDao]]
- [[Features/Consultation Calendar]]
- [[Components/Repositories/CaseRepository]]

## Source References

- `core/src/main/java/com/taha/kairos/core/repository/ConsultationRepository.kt`
- `data/src/main/java/com/taha/kairos/data/repository/ConsultationRepositoryImpl.kt`
- `data/src/main/java/com/taha/kairos/data/di/DataModule.kt`
