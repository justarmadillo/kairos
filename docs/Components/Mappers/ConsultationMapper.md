# ConsultationMapper

> **In plain words** — the translator for consultation sessions, carrying through the case count used by the calendar and the deletion time used by the trash screen.

## Purpose

Translates consultation session projections and entities into `ConsultationSession`.

## Responsibilities

- Preserve computed active-case counts for calendar range results.
- Preserve deletion time for trash rows.

## Dependencies

- `ConsultationSession`, `ConsultationSessionEntity`, and `SessionWithCount`.

## Called By

- [[Components/Repositories/ConsultationRepository]]

## Calls

- No services or I/O; both functions construct domain data classes.

## Important Methods

- `SessionWithCount.toDomain()` maps ID, date, and computed count.
- `ConsultationSessionEntity.toDomain()` maps ID/date/deletion time with `caseCount = 0`.

## Design Patterns

- Pure extension mapper with projection-specific overloads.

## Common Pitfalls

- Direct ID and trash mappings do not calculate case counts.
- Entity audit/sync fields other than deletion time are intentionally absent from the domain model.

## Related Pages

- [[Components/Repositories/ConsultationRepository]]
- [[Components/DAOs/ConsultationSessionDao]]
- [[Layers/Mappers]]

## Source References

- `data/src/main/java/com/taha/kairos/data/mapper/ConsultationMapper.kt`
- `data/src/main/java/com/taha/kairos/data/db/relations/Counts.kt`
