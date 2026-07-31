# Project Architecture

> **In plain words** — the whole app on one page. Each box grouping is a Gradle module; arrows point from something to what it depends on. The shape to notice: `:features` and `:data` never point at each other, only at `:core`. That gap is the architecture — screens and storage meet only through shared contracts. See [[Learn/Gradle And Modules|Gradle And Modules]].

```mermaid
flowchart TB
    subgraph AppModule[":app — composition root"]
        Entry["Application / Activity"]
        Gate["Authorization gate"]
        Nav["Navigation / widget"]
    end

    subgraph FeaturesModule[":features — presentation"]
        Screens["Compose screens"]
        VMs["Hilt ViewModels"]
    end

    subgraph CoreModule[":core — shared contracts"]
        Models["Domain models"]
        Contracts["Repository contracts"]
        Shared["Shared UI / theme / media"]
        Policy["Authorization policy"]
    end

    subgraph DataModule[":data — implementation"]
        Implementations["Repository implementations"]
        Room["Room database / DAOs"]
        Prefs["DataStore preferences"]
        Files["Media / backup files"]
        Workers["WorkManager jobs"]
        Firestore["Firestore authorization"]
    end

    Entry --> Gate
    Gate --> Nav
    Nav --> Screens
    Screens --> VMs
    VMs --> Contracts
    VMs --> Models
    Screens --> Shared
    Implementations -. "Hilt bindings" .-> Contracts
    Implementations --> Room
    Implementations --> Prefs
    Implementations --> Files
    Implementations --> Firestore
    Workers --> Implementations
    Gate --> Policy
```

The diagram shows compile-time ownership and runtime calls together. See [[Overview/Architecture|Architecture]], [[Overview/Gradle Modules|Gradle Modules]], and [[Layers/Layers Index|Layers]].

## Source references

- `settings.gradle.kts`
- `app/src/main/java/com/taha/kairos/MainActivity.kt`
- `core/src/main/java/com/taha/kairos/core/repository/CaseRepository.kt`
- `data/src/main/java/com/taha/kairos/data/di/DataModule.kt`
- `features/src/main/java/com/taha/kairos/features/patient/PatientCaseViewModel.kt`

