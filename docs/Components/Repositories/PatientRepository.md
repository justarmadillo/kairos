# PatientRepository

> **In plain words** — the doorway to patients. A patient can have several phone numbers, stored in their own table, and saving a patient **replaces** that whole set rather than trying to merge it — simpler and impossible to get half-right, because it happens in one transaction. Deleting is a soft delete, so a patient can be restored from trash.

## Purpose

Defines persistence for a patient and the patient's phone-number aggregate. The page covers the `PatientRepository` contract and `PatientRepositoryImpl` binding.

## Responsibilities

- Create or update patients and replace their phone rows transactionally.
- Provide one-shot and reactive patient lookup.
- Search active patients by name.
- Soft-delete, restore, and observe trashed patients.

## Dependencies

- `Patient`, `PatientPhone`, and Kotlin `Flow`.
- [[Components/DAOs/PatientDao]], [[Components/Databases/KairosDatabase]], [[Components/Repositories/DataSafetyCoordinator]], and [[Components/Mappers/PatientMapper]].

## Called By

- [[Components/ViewModels/PatientCaseViewModel]]
- [[Components/ViewModels/TrashViewModel]]

## Calls

- `PatientDao` insert, update, phone replacement, lookup, search, soft-delete, restore, and trash queries.
- `KairosDatabase.withTransaction` for aggregate writes.

## Important Methods

- `upsert(patient)` inserts when `id == 0`, otherwise updates; phone rows are deleted and reinserted in the same Room transaction.
- `getById` / `observeById` return a mapped `PatientWithPhones` aggregate.
- `search(query)` emits up to 50 active name matches.
- `softDelete`, `restore`, and `observeTrashed` implement trash behavior.

## Design Patterns

- Repository abstraction in `:core` with a Hilt-bound `:data` implementation.
- Aggregate replacement for child phone rows.
- Room transaction plus reactive `Flow` reads.
- Soft delete rather than immediate physical deletion.

## Common Pitfalls

- Updating a patient replaces every phone row; callers must submit the complete intended phone list.
- Mapper normalization trims and capitalizes names, so stored text may differ from raw input.
- Read methods do not hide a patient solely because it is soft-deleted when addressed directly by ID.

## Related Pages

- [[Components/Repositories/CaseRepository]]
- [[Layers/Repositories]]
- [[Execution Flows/Database Operations]]

## Source References

- `core/src/main/java/com/taha/kairos/core/repository/PatientRepository.kt`
- `data/src/main/java/com/taha/kairos/data/repository/PatientRepositoryImpl.kt`
- `data/src/main/java/com/taha/kairos/data/di/DataModule.kt`
