# CaseMapper

> **In plain words** — the translator between a case as stored and a case as used. Going out, it assembles the row plus its patient, diagnoses, and media into one `Case`; going in, it flattens a `Case` back into a row, keeping the original creation time on an update and stamping a new modification time. Storage-only columns simply have no counterpart in `Case`, which is how they stay invisible to the UI. See [[Layers/Mappers|Mappers]].

## Purpose

Translates the complete Room case relation and its child entities into the `Case` domain aggregate, and maps case writes back to `CaseEntity`.

## Responsibilities

- Map `CaseWithRelations`, nested patient data, diagnoses, and media.
- Parse persisted media-type strings.
- Preserve clinical, audit, and soft-delete values on reads.
- Assign creation/update timestamps on writes.

## Dependencies

- `Case`, `Diagnosis`, `MediaItem`, `MediaType` and their Room entity/relation types.
- [[Components/Mappers/PatientMapper]].

## Called By

- [[Components/Repositories/CaseRepository]]
- [[Components/Repositories/MediaRepository]] for `CaseMediaEntity.toDomain`.

## Calls

- Nested patient, diagnosis, and media extension mappings.

## Important Methods

- `CaseWithRelations.toDomain()` constructs the aggregate.
- `DiagnosisEntity.toDomain()` maps a diagnosis without a computed count.
- `CaseMediaEntity.toDomain()` parses `MediaType` and attachment metadata.
- `Case.toEntity(now)` writes case fields and derives timestamps.

## Design Patterns

- Pure extension mapper and aggregate composition.

## Common Pitfalls

- Unknown `media_type` strings silently map to `MediaType.IMAGE`.
- Domain diagnosis rows loaded with a case have `caseCount = 0`.
- `Case.toEntity` omits sync fields, so mapped updates use entity defaults.
- File paths remain relative until [[Components/Repositories/CaseRepository]] resolves them.

## Related Pages

- [[Components/DAOs/CaseDao]]
- [[Components/Repositories/CaseRepository]]
- [[Layers/Models]]

## Source References

- `data/src/main/java/com/taha/kairos/data/mapper/CaseMapper.kt`
- `data/src/main/java/com/taha/kairos/data/db/relations/CaseWithRelations.kt`

