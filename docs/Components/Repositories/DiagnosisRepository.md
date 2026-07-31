# DiagnosisRepository

> **In plain words** — the doorway to diagnoses. Its central job is *lookup-or-create, case-insensitively*: typing "femur fracture" must reuse an existing "Femur Fracture" rather than creating a near-duplicate, because duplicates would split one diagnosis's cases across two entries. Renaming returns an explicit result (success, blank name, name already taken) instead of failing silently, so the screen can show a precise error.

## Purpose

Provides canonical diagnosis lookup, creation, renaming, sorting, and autocomplete data.

## Responsibilities

- Perform case-insensitive lookup-or-insert.
- Rename diagnoses with explicit validation outcomes.
- Emit diagnoses ordered alphabetically, by usage, or by creation recency.
- Serve prefix autocomplete and ID lookup.

## Dependencies

- [[Components/DAOs/DiagnosisDao]], [[Components/Databases/KairosDatabase]], [[Components/Repositories/DataSafetyCoordinator]], and [[Components/Mappers/DiagnosisMapper]].
- `Diagnosis`, `DiagnosisSortMode`, and `DiagnosisRenameResult`.

## Called By

- [[Components/ViewModels/PatientCaseViewModel]]
- [[Components/ViewModels/DiagnosisBrowseViewModel]]
- [[Components/Repositories/CaseRepository]] indirectly uses the same DAO for case saves.

## Calls

- `DiagnosisDao.findByNameCi`, `insert`, `rename`, sort queries, prefix search, and lookup.
- `KairosDatabase.withTransaction` for lookup-or-insert and rename.

## Important Methods

- `getOrCreate(name)` trims and atomically resolves or inserts a diagnosis.
- `rename(id, newName)` returns `RENAMED`, `UNCHANGED`, `BLANK_NAME`, `ALREADY_EXISTS`, or `NOT_FOUND`.
- `observeAll(sort)` selects the matching DAO query.
- `searchByPrefix(prefix, limit)` provides autocomplete candidates.

## Design Patterns

- Repository abstraction with explicit result enum for expected rename failures.
- Transactional check-then-write with an `INSERT IGNORE` race fallback.
- Strategy selection through `DiagnosisSortMode`.

## Common Pitfalls

- The entity's unique index uses SQLite's default collation; application code supplies case-insensitive duplicate checks.
- `getOrCreate` permits a trimmed blank string unless the caller validates first.
- Case counts include only non-deleted cases and are computed by correlated subqueries.

## Related Pages

- [[Components/DAOs/DiagnosisDao]]
- [[Components/Repositories/CaseRepository]]
- [[Features/Diagnosis Browser]]

## Source References

- `core/src/main/java/com/taha/kairos/core/repository/DiagnosisRepository.kt`
- `data/src/main/java/com/taha/kairos/data/repository/DiagnosisRepositoryImpl.kt`
- `data/src/main/java/com/taha/kairos/data/di/DataModule.kt`
