# CaseDao

> **In plain words** — the SQL for everything to do with cases: insert, update, link to diagnoses/shifts/sessions, load a case with all its related rows, mark deleted, restore, count, and search. Worth opening the source file itself even as a beginner — it is the clearest place in the project to see real SQL, including a `JOIN` through a link table and the `is_deleted = 0` condition that every list query carries. See [[Learn/Databases And Room|Databases And Room]].

## Purpose

Defines Room writes and rich projection queries for cases and their junction tables.

## Responsibilities

- Insert/update cases and manage diagnosis, shift, and consultation junctions.
- Load complete `CaseWithRelations` aggregates.
- Produce diagnosis-, shift-, session-, dashboard-, and search-oriented views.
- Manage case trash, hard purge, and counts.

## Dependencies

- Case and junction entities, `CaseWithRelations`, `RecentCaseRow`, and `SearchCaseRow`.
- [[Components/Databases/KairosDatabase]].

## Called By

- [[Components/Repositories/CaseRepository]]
- [[Components/Repositories/DashboardRepository]]
- [[Components/Repositories/SearchRepository]]
- [[Components/Workers/TrashPurgeWorker]]

## Calls

- SQL over `cases`, `patients`, `patient_phones`, `diagnoses`, `case_diagnoses`, `case_media`, `shift_cases`, and `consultation_cases`.

## Important Methods

- `insert`, `update`, and junction insert/clear/unlink methods.
- `getById` / `observeById` and `observeByDiagnosis|Shift|Session` use Room transactions for relations.
- `softDelete`, `restore`, `observeTrashed`, `listExpiredTrash`, and `hardDelete` implement retention.
- `observeTotalCases`, `countCasesInRange`, and `observeRecentCases` power dashboard metrics.
- `observeSearchCases` builds a broad search projection with escaped `LIKE` input.

## Design Patterns

- Central aggregate DAO plus specialized read projections.
- Many-to-many junction commands, soft delete, and reactive invalidation.

## Common Pitfalls

- `countCasesInRange` filters `created_at`, not `case_date`.
- `observeRecentCases` selects one diagnosis with unordered `LIMIT 1`.
- Search candidate results are limited before Kotlin performs final all-token filtering.
- `hardDelete` cascades metadata but cannot delete attachment files.

## Related Pages

- [[Components/Repositories/CaseRepository]]
- [[Components/DAOs/CaseMediaDao]]
- [[Components/Mappers/CaseMapper]]

## Source References

- `data/src/main/java/com/taha/kairos/data/db/dao/CaseDao.kt`
- `data/src/main/java/com/taha/kairos/data/db/entities/CaseEntities.kt`
- `data/src/main/java/com/taha/kairos/data/db/relations/CaseWithRelations.kt`

