# Dependencies

> **In plain words** — a *dependency* is someone else's library that this app uses. All of them are listed with their versions in one file (`gradle/libs.versions.toml`, the *version catalog*), so upgrading a library means editing one line instead of hunting through four build files. The **scope** words matter: `implementation` means "I use it privately", `api` means "I use it and pass it on to whoever depends on me", `ksp` means "this is a code generator, not a library". Note what is *absent*: no HTTP client, because the app has no clinical backend to talk to. See [[Learn/Gradle And Modules|Gradle And Modules]].

Dependency coordinates and versions are centralized in `gradle/libs.versions.toml`; module build files decide visibility and scope.

## Version Catalog

| Group | Versions |
| --- | --- |
| Android/Kotlin | AGP 8.7.3, Kotlin 2.1.0, KSP 2.1.0-1.0.29 |
| Compose | BOM 2024.12.01, Material 3 1.3.1, Activity Compose 1.9.3, Navigation 2.8.5, Lifecycle 2.8.7 |
| Architecture | Hilt 2.54, Hilt extensions 1.2.0, Room 2.7.0, DataStore 1.1.1, WorkManager 2.10.0 |
| Media/UI | Coil 3.0.4, Media3 1.5.1, rich editor 1.0.0-rc11, Accompanist 0.37.0 |
| Firebase | Firebase BOM 34.16.0, Google Services plugin 4.5.0 |
| Tests | JUnit 4.13.2, AndroidX JUnit 1.2.1, Espresso 3.6.1, coroutines-test 1.9.0 |

## Scope Decisions

- `:core` exports Compose, lifecycle UI, Coil, Media3, editor, permission, and DocumentFile APIs because shared public components use them.
- `:data` keeps Room, DataStore, Firestore, and worker runtime implementation-only.
- `:features` keeps UI/navigation libraries implementation-only and sees shared contracts through `:core`.
- `:app` owns Firebase App Check and the Google Services plugin; Firestore itself is confined to `:data`.
- Hilt and KSP are applied wherever generated injection code is required.

No Retrofit or OkHttp dependency exists; the only remote request uses the Firestore SDK directly. See [[Execution Flows/API Request Lifecycle|API Request Lifecycle]] and [[Layers/Networking|Networking]].

## Source references

- `gradle/libs.versions.toml`
- `app/build.gradle.kts`
- `core/build.gradle.kts`
- `data/build.gradle.kts`
- `features/build.gradle.kts`
