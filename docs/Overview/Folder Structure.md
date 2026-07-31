# Folder Structure

> **In plain words** — a map of where files live. Read it as: which module (`app`/`core`/`data`/`features`), then which package (the sub-folder), then the file. Knowing this map is what lets you find code without searching blindly — screens are always under `features/`, database code always under `data/db/`, shared models always under `core/model/`. How to use the map when hunting for something: [[Learn/Reading The Codebase|Reading The Codebase]].

```text
Kairos/
├── app/                    # Application, authorization gate, navigation, widget
│   └── src/{main,debug,release}/
├── core/                   # Contracts, domain models, shared UI/theme/media
│   └── src/main/java/com/taha/kairos/core/
│       ├── authorization/  # Lease policy and repository contract
│       ├── components/     # Shared Compose components
│       ├── media/          # Recorder and file manager
│       ├── model/          # Domain models
│       ├── repository/     # Repository interfaces
│       └── theme/          # Design system
├── data/                   # Persistence, implementations, workers, DI
│   ├── schemas/            # Exported Room schemas
│   └── src/main/java/com/taha/kairos/data/
│       ├── authorization/  # Firestore authorization and worker guard
│       ├── backup/         # Backup engine, scheduler, workers
│       ├── db/             # Database, DAOs, entities, relations, migrations
│       ├── di/             # Hilt providers and bindings
│       ├── mapper/         # Entity/domain conversion
│       ├── repository/     # Repository implementations
│       └── settings/       # DataStore wrapper
├── features/               # Screens and ViewModels by feature
│   └── src/main/java/com/taha/kairos/features/
│       ├── cases/
│       ├── consultation/
│       ├── dashboard/
│       ├── patient/
│       ├── search/
│       ├── settings/
│       └── shifts/
├── gradle/                 # Version catalog and wrapper
├── docs/                   # Obsidian knowledge base
└── firestore.rules         # Device-whitelist Firestore rules
```

Tests sit beside their owning module under `src/test`. Build outputs and generated KSP sources are not architectural source and are excluded from this map.

See [[Overview/Gradle Modules|Gradle Modules]], [[Layers/Layers Index|Layers]], [[Features/Features Index|Features]], and [[Components/Components Index|Components]].

## Source references

- `settings.gradle.kts`
- `app/src/main/java/com/taha/kairos/navigation/KairosNavHost.kt`
- `core/src/main/java/com/taha/kairos/core/repository/CaseRepository.kt`
- `data/src/main/java/com/taha/kairos/data/di/DataModule.kt`
- `features/src/main/java/com/taha/kairos/features/dashboard/DashboardViewModel.kt`
