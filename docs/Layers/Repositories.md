# Repositories

> **In plain words** — a repository is the single doorway to one kind of data. Ask `CaseRepository` for cases; it decides whether that means a database query, a file lookup, or both, and you never find out which. Each one comes in two parts: an **interface** in `:core` listing what can be asked (readable in one screen), and an **implementation** in `:data` doing the work. That split is what lets a test hand a ViewModel a fake repository, and what stops screens from depending on the database. See [[Learn/Architecture Patterns|Architecture Patterns]] and [[Learn/Design Patterns Glossary|Design Patterns Glossary]].

## Role

Repository interfaces in `:core` isolate ViewModels from Room, DataStore, Firestore, and filesystem details. Hilt binds their singleton implementations from `:data`.

## Families

- Aggregate persistence: [[Components/Repositories/PatientRepository|Patient]], [[Components/Repositories/CaseRepository|Case]], [[Components/Repositories/MediaRepository|Media]].
- Classification and grouping: [[Components/Repositories/DiagnosisRepository|Diagnosis]], [[Components/Repositories/ShiftRepository|Shift]], [[Components/Repositories/ConsultationRepository|Consultation]].
- Read models: [[Components/Repositories/DashboardRepository|Dashboard]], [[Components/Repositories/SearchRepository|Search]].
- Configuration and safety: [[Components/Repositories/SettingsRepository|Settings]], [[Components/Repositories/BackupRepository|Backup]], [[Components/Repositories/DeviceAuthorizationRepository|Device Authorization]], [[Components/Repositories/DataSafetyCoordinator|Data Safety Coordinator]].

## Conventions

Reactive reads return `Flow`; single lookups and writes are suspending. Implementations map persistence records to domain objects and normally allow storage exceptions to reach their caller. Multi-table writes use Room transactions, while global write/export/restore exclusion uses the data-safety coordinator.

## Related pages

- [[Components/Repositories/Repositories Index|Repositories Index]]
- [[Architecture/Dependency Injection|Dependency Injection]]
- [[Execution Flows/Database Operations|Database Operations]]

## Source references

- `core/src/main/java/com/taha/kairos/core/repository/CaseRepository.kt`
- `core/src/main/java/com/taha/kairos/core/repository/SettingsRepository.kt`
- `core/src/main/java/com/taha/kairos/core/authorization/AuthorizationModels.kt`
- `data/src/main/java/com/taha/kairos/data/di/DataModule.kt`
