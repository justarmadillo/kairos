# Mappers

> **In plain words** — translators. The database shape and the app shape of a case are different on purpose, so something has to convert between them; that something is a mapper, and it is the *only* code that knows both shapes. `toDomain()` converts a database row into what the screens use; `toEntity()` converts back for saving. Keeping translation in one place means database-only fields can never accidentally leak into the UI. See [[Learn/Kotlin Basics|Kotlin Basics]] (extension functions) and [[Learn/Code Tour One Feature|Code Tour One Feature]].

## Role

Extension functions in `:data/mapper` translate Room entities, relations, and count projections into `:core` domain models. Write mappers preserve creation timestamps when updating and assign current timestamps for new records.

## Inventory

- [[Components/Mappers/PatientMapper|Patient Mapper]] — `PatientWithPhones`, `PatientPhoneEntity`, and write entities; trims and capitalizes patient names.
- [[Components/Mappers/CaseMapper|Case Mapper]] — case aggregate, diagnosis, media, and case entity transformations.
- [[Components/Mappers/DiagnosisMapper|Diagnosis Mapper]] — diagnosis count projection.
- [[Components/Mappers/ShiftMapper|Shift Mapper]] — entity/count projection and write entity.
- [[Components/Mappers/ConsultationMapper|Consultation Mapper]] — session entity/count projection.

Media writes are mapped privately inside `MediaRepositoryImpl`; absolute media-path resolution is a repository responsibility, not a mapper responsibility.

## Pitfalls

`CaseMapper` treats an unknown stored media type as `IMAGE`, which avoids a crash but can misclassify corrupted/future values. Mappers do not validate all business invariants; callers and database constraints remain responsible.

## Related pages

- [[Layers/Models|Models]]
- [[Layers/Repositories|Repositories]]
- [[Components/Mappers/Mappers Index|Mappers Index]]

## Source references

- `data/src/main/java/com/taha/kairos/data/mapper/PatientMapper.kt`
- `data/src/main/java/com/taha/kairos/data/mapper/CaseMapper.kt`
- `data/src/main/java/com/taha/kairos/data/mapper/DiagnosisMapper.kt`
- `data/src/main/java/com/taha/kairos/data/mapper/ShiftMapper.kt`
- `data/src/main/java/com/taha/kairos/data/mapper/ConsultationMapper.kt`
