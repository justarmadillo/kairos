# DashboardRepository

> **In plain words** — a read-only repository that exists purely to feed the home screen: totals, the five most recent cases, and counts within a date range. It is a good example of a **read model** — data shaped for one screen's needs rather than for storage — which is why it returns compact projections instead of whole cases.

## Purpose

Provides dashboard-oriented counts, recent-case projections, and period metrics without exposing DAO rows to the UI layer.

## Responsibilities

- Observe totals for active patients, cases, and shifts.
- Observe the five most recently created active cases.
- Count active cases created within a half-open time range.
- Map dashboard rows into `RecentCase` domain projections.

## Dependencies

- [[Components/DAOs/PatientDao]], [[Components/DAOs/CaseDao]], and [[Components/DAOs/ShiftDao]].
- [[Components/Utilities/PatientNameFormatter]] for displayed patient names.

## Called By

- [[Components/ViewModels/DashboardViewModel]]

## Calls

- DAO total-count Flows.
- `CaseDao.observeRecentCases` and `CaseDao.countCasesInRange`.

## Important Methods

- `observeTotalPatients`, `observeTotalCases`, and `observeTotalShifts` forward reactive counts.
- `observeRecentCases()` maps `RecentCaseRow` values and capitalizes names.
- `countCasesInRange(startMs, endMs)` uses `created_at >= startMs AND created_at < endMs`.

## Design Patterns

- Read-model repository specialized for a screen.
- Projection queries instead of loading full aggregates.
- Reactive composition in the consuming ViewModel.

## Common Pitfalls

- Period counts use case creation time, not the clinical `case_date`.
- The recent diagnosis is an unspecified single linked diagnosis because the SQL subquery has `LIMIT 1` without ordering.
- Soft-deleted records are excluded.

## Related Pages

- [[Features/Dashboard]]
- [[Components/DAOs/CaseDao]]
- [[Layers/Data Layer]]

## Source References

- `core/src/main/java/com/taha/kairos/core/repository/DashboardRepository.kt`
- `data/src/main/java/com/taha/kairos/data/repository/DashboardRepositoryImpl.kt`
- `data/src/main/java/com/taha/kairos/data/di/DataModule.kt`

