# PatientDao

> **In plain words** — the SQL for patients and their phone numbers. Loading a patient with their phones is marked `@Transaction` because it takes more than one query, and the transaction guarantees both see the same instant — otherwise a phone could be added between the two reads and the result would be inconsistent.

## Purpose

Defines Room operations for patient rows and their phone-child aggregate.

## Responsibilities

- Insert and update patients and phone rows.
- Load `PatientWithPhones` transactionally.
- Search active patients by case-insensitive name substring.
- Manage patient trash, retention purge, and active totals.

## Dependencies

- `PatientEntity`, `PatientPhoneEntity`, and `PatientWithPhones`.
- [[Components/Databases/KairosDatabase]].

## Called By

- [[Components/Repositories/PatientRepository]]
- [[Components/Repositories/DashboardRepository]]
- [[Components/Workers/TrashPurgeWorker]]

## Calls

- Room-generated SQL for `patients` and `patient_phones`.
- Room relation expansion for aggregate queries.

## Important Methods

- `insert`, `update`, `insertPhones`, and `deletePhonesFor` support aggregate replacement.
- `getById` / `observeById` return transactional relations.
- `search(query)` returns at most 50 active patients ordered by name.
- `softDelete` / `restore` update deletion, sync, and timestamp fields.
- `purgeOlderThan` protects patients referenced by non-deleted cases.
- `observeTotalPatients` emits the active count.

## Design Patterns

- Room DAO, aggregate relation query, soft delete, and reactive `Flow`.

## Common Pitfalls

- `insertPhones` uses `REPLACE`; repository code normally deletes the whole prior set first.
- Direct ID queries do not filter `is_deleted`.
- Purge protection considers active cases only; filesystem cleanup is owned by [[Components/Workers/TrashPurgeWorker]].

## Related Pages

- [[Components/Repositories/PatientRepository]]
- [[Components/Mappers/PatientMapper]]
- [[Components/DAOs/CaseDao]]

## Source References

- `data/src/main/java/com/taha/kairos/data/db/dao/PatientDao.kt`
- `data/src/main/java/com/taha/kairos/data/db/entities/PatientEntities.kt`
- `data/src/main/java/com/taha/kairos/data/db/relations/PatientWithPhones.kt`

