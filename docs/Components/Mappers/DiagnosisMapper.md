# DiagnosisMapper

> **In plain words** — the smallest translator in the app: a diagnosis row plus its computed case count becomes a `Diagnosis`. Note that the count is calculated by the database query and merely carried through here — mappers convert shapes, they never compute facts.

## Purpose

Maps a diagnosis count projection into the domain `Diagnosis` model.

## Responsibilities

- Preserve diagnosis ID and name.
- Carry the DAO-computed active-case count into the domain layer.

## Dependencies

- `Diagnosis` and `DiagnosisWithCount`.

## Called By

- [[Components/Repositories/DiagnosisRepository]]

## Calls

- No services or I/O; it constructs `Diagnosis` directly.

## Important Methods

- `DiagnosisWithCount.toDomain()` maps all projection fields one-to-one.

## Design Patterns

- Pure extension-function projection mapper.

## Common Pitfalls

- Count correctness depends entirely on the selected [[Components/DAOs/DiagnosisDao]] query.
- Plain `DiagnosisEntity` mapping lives in [[Components/Mappers/CaseMapper]] and sets count to zero.

## Related Pages

- [[Layers/Mappers]]
- [[Components/Repositories/DiagnosisRepository]]
- [[Components/DAOs/DiagnosisDao]]

## Source References

- `data/src/main/java/com/taha/kairos/data/mapper/DiagnosisMapper.kt`
- `data/src/main/java/com/taha/kairos/data/db/relations/Counts.kt`

