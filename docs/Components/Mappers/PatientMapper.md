# PatientMapper

> **In plain words** — the translator for patients and their phone numbers. It also normalises the name on the way in (trimming and capitalising), so the same person typed three different ways is stored consistently. Doing it here means every entry point gets the same treatment for free.

## Purpose

Translates patient and phone Room types to and from the `Patient` aggregate.

## Responsibilities

- Map `PatientWithPhones` and `PatientPhoneEntity` to domain models.
- Map `Patient` and `PatientPhone` to persistence entities.
- Normalize patient names and assign write timestamps.

## Dependencies

- `Patient`, `PatientPhone`, `PatientEntity`, `PatientPhoneEntity`, `PatientWithPhones`.
- [[Components/Utilities/PatientNameFormatter]].

## Called By

- [[Components/Repositories/PatientRepository]]
- [[Components/Mappers/CaseMapper]] through the nested patient relation.

## Calls

- `toCapitalizedPatientName` during both read and write mapping.
- Child phone mapping for every aggregate.

## Important Methods

- `PatientWithPhones.toDomain()` preserves IDs, timestamps, deletion time, and child phones.
- `Patient.toEntity(now)` trims/capitalizes the name and chooses `createdAt` from ID state.
- `PatientPhone.toEntity(patientId)` assigns the aggregate's foreign key.

## Design Patterns

- Pure extension-function mapper and aggregate translation.

## Common Pitfalls

- Read mapping can change display casing without rewriting the database.
- A nonzero patient ID with a default `createdAt = 0` preserves zero.
- Entity sync fields are not represented in the domain model and reset on mapped updates.

## Related Pages

- [[Layers/Mappers]]
- [[Components/DAOs/PatientDao]]
- [[Components/Repositories/PatientRepository]]

## Source References

- `data/src/main/java/com/taha/kairos/data/mapper/PatientMapper.kt`
- `core/src/main/java/com/taha/kairos/core/model/Patient.kt`

