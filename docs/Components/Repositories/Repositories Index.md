# Repositories Index

> **In plain words** — twelve doorways to data, one per subject area. Each exists twice: an **interface** in `:core` (the list of what can be asked) and an **implementation** in `:data` (the code that does it). Screens only ever see the interface. Read the interface first — it is short and tells you everything the app can do with that kind of record. See [[Layers/Repositories|Repositories]] and [[Learn/Architecture Patterns|Architecture Patterns]].

Repository contracts live in `:core`; Hilt binds them to singleton implementations in `:data`. See [[Layers/Repositories]] for the layer-wide conventions.

## Repository Pages

- [[Components/Repositories/PatientRepository]]
- [[Components/Repositories/CaseRepository]]
- [[Components/Repositories/DiagnosisRepository]]
- [[Components/Repositories/MediaRepository]]
- [[Components/Repositories/ShiftRepository]]
- [[Components/Repositories/ConsultationRepository]]
- [[Components/Repositories/DashboardRepository]]
- [[Components/Repositories/SearchRepository]]
- [[Components/Repositories/SettingsRepository]]
- [[Components/Repositories/BackupRepository]]
- [[Components/Repositories/DeviceAuthorizationRepository]]
- [[Components/Repositories/DataSafetyCoordinator]]

Cross-repository mutation and snapshot coordination is documented in [[Components/Repositories/DataSafetyCoordinator]].

## Source References

- `core/src/main/java/com/taha/kairos/core/repository/`
- `core/src/main/java/com/taha/kairos/core/authorization/AuthorizationModels.kt`
- `data/src/main/java/com/taha/kairos/data/repository/`
- `data/src/main/java/com/taha/kairos/data/di/DataModule.kt`
