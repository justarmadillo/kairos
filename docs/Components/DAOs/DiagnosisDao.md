# DiagnosisDao

> **In plain words** — the SQL for diagnoses. Its insert deliberately does *not* overwrite an existing name, so two people (or two fast taps) creating "Femur fracture" at once end up with one row rather than a duplicate or a lost record. Lookup is case-insensitive for the same reason.

## Purpose

Defines canonical diagnosis writes, usage-count lists, autocomplete, and orphan cleanup.

## Responsibilities

- Insert diagnoses without overwriting an existing unique name.
- Perform case-insensitive name and ID lookup.
- Rename diagnoses and report affected-row count.
- Emit three sorted lists with active-case counts.
- Prefix-search and delete diagnoses with no case junction.

## Dependencies

- `DiagnosisEntity`, `DiagnosisWithCount`, Room, and [[Components/Databases/KairosDatabase]].

## Called By

- [[Components/Repositories/DiagnosisRepository]]
- [[Components/Repositories/CaseRepository]]
- [[Components/Workers/TrashPurgeWorker]]

## Calls

- SQL over `diagnoses`, `case_diagnoses`, and `cases`.

## Important Methods

- `insert` uses `OnConflictStrategy.IGNORE`.
- `findByNameCi` uses `COLLATE NOCASE`.
- `observeAllAlphabetical`, `observeAllByMostUsed`, and `observeAllByRecent` compute usage counts.
- `searchByPrefix(prefix, limit)` supplies autocomplete.
- `deleteOrphaned()` physically removes unreferenced diagnoses.

## Design Patterns

- Room DAO, conflict-tolerant lookup-or-insert support, reactive projections.

## Common Pitfalls

- The unique entity index does not explicitly specify `NOCASE`; callers must retain repository duplicate checks.
- Usage-count subqueries exclude soft-deleted cases.
- `deleteOrphaned` is destructive and should remain part of coordinated purge work.

## Related Pages

- [[Components/Repositories/DiagnosisRepository]]
- [[Components/Mappers/DiagnosisMapper]]
- [[Features/Diagnosis Browser]]

## Source References

- `data/src/main/java/com/taha/kairos/data/db/dao/DiagnosisDao.kt`
- `data/src/main/java/com/taha/kairos/data/db/entities/DiagnosisEntities.kt`
- `data/src/main/java/com/taha/kairos/data/db/relations/Counts.kt`

