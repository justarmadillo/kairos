# Mappers Index

> **In plain words** — five small files of translation functions. `toDomain()` turns a database row into the shape screens use; `toEntity()` turns it back for saving. They are written as *extension functions*, which is why the code reads `case.toEntity(now)` even though `Case` itself knows nothing about databases. See [[Layers/Mappers|Mappers]] and [[Learn/Kotlin Basics|Kotlin Basics]].

Top-level Kotlin extension functions translate Room entities and relation projections into `:core` models, and translate mutable domain objects back into entities.

- [[Components/Mappers/PatientMapper]]
- [[Components/Mappers/CaseMapper]]
- [[Components/Mappers/DiagnosisMapper]]
- [[Components/Mappers/ShiftMapper]]
- [[Components/Mappers/ConsultationMapper]]

## Source References

- `data/src/main/java/com/taha/kairos/data/mapper/PatientMapper.kt`
- `data/src/main/java/com/taha/kairos/data/mapper/CaseMapper.kt`
- `data/src/main/java/com/taha/kairos/data/mapper/DiagnosisMapper.kt`
- `data/src/main/java/com/taha/kairos/data/mapper/ShiftMapper.kt`
- `data/src/main/java/com/taha/kairos/data/mapper/ConsultationMapper.kt`
- `core/src/main/java/com/taha/kairos/core/model/Case.kt`
