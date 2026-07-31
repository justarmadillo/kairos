# Layers

> **In plain words** — a *layer* is a horizontal slice of the app with one job and strict rules about who it may talk to. Top to bottom: **UI** draws, **ViewModels** decide, **repositories** own data, **data sources** store it. Each layer only knows about the one below, so a change in how data is stored cannot ripple into how it is drawn. Read [[Learn/Architecture Patterns|Architecture Patterns]] first if this is new, then [[Learn/Code Tour One Feature|Code Tour One Feature]] to watch one action pass through all of them.

The project uses pragmatic layered boundaries: Compose screens and ViewModels depend on domain models/repository interfaces in `:core`; `:data` implements those interfaces with Room, DataStore, Firestore, and files. There are no dedicated use-case classes.

## Pages

- [[Layers/UI Layer|UI Layer]]
- [[Layers/ViewModels|ViewModels]]
- [[Layers/Domain Layer|Domain Layer]]
- [[Layers/Data Layer|Data Layer]]
- [[Layers/Repositories|Repositories]]
- [[Layers/Data Sources|Data Sources]]
- [[Layers/Networking|Networking]]
- [[Layers/Local Storage|Local Storage]]
- [[Layers/Models|Models]]
- [[Layers/Mappers|Mappers]]

## Source references

- `settings.gradle.kts`
- `core/src/main/java/com/taha/kairos/core/repository/CaseRepository.kt`
- `data/src/main/java/com/taha/kairos/data/repository/CaseRepositoryImpl.kt`
- `features/src/main/java/com/taha/kairos/features/patient/PatientCaseViewModel.kt`
