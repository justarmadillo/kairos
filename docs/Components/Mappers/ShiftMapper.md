# ShiftMapper

> **In plain words** — the translator for shifts, in both directions, carrying through the query-computed case count and the deletion time used by the trash screen.

## Purpose

Translates shift entities and count projections into `Shift`, and maps shift writes into `ShiftEntity`.

## Responsibilities

- Preserve computed case counts for list projections.
- Preserve deletion time for trash entities.
- Assign creation time for new shift rows.

## Dependencies

- `Shift`, `ShiftEntity`, and `ShiftWithCount`.

## Called By

- [[Components/Repositories/ShiftRepository]]

## Calls

- No services or I/O; all mappings construct data classes.

## Important Methods

- `ShiftWithCount.toDomain()` carries `caseCount` for active lists.
- `ShiftEntity.toDomain()` carries `deletedAt` and uses `caseCount = 0`.
- `Shift.toEntity(now)` chooses creation time from ID state.

## Design Patterns

- Pure extension mapper with separate projection and entity overloads.

## Common Pitfalls

- Detail/trash mappings from `ShiftEntity` do not calculate a case count.
- Sync and deletion flags are omitted on domain-to-entity mapping and use defaults during update.

## Related Pages

- [[Components/Repositories/ShiftRepository]]
- [[Components/DAOs/ShiftDao]]
- [[Layers/Mappers]]

## Source References

- `data/src/main/java/com/taha/kairos/data/mapper/ShiftMapper.kt`
- `data/src/main/java/com/taha/kairos/data/db/entities/ShiftEntities.kt`

