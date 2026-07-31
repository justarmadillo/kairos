# Technology Stack

> **In plain words** — a list of the ready-made tools the app is built from, so none of it had to be written from scratch. **Kotlin** is the language. **Compose** draws every screen. **Room** stores the records. **DataStore** stores settings. **Hilt** hands objects their dependencies. **WorkManager** runs jobs in the background. **Coil** and **Media3** show images and play audio/video. **Firestore** and **App Check** handle the device permission check. Each row is one problem someone else already solved; the version numbers matter because libraries change behaviour between releases. Beginner explanations: [[Learn/Learn Index|Learn]].

| Concern | Technology | Role |
| --- | --- | --- |
| Language | Kotlin 2.1.0, Java 17 target | Application and build logic |
| UI | Jetpack Compose, Material 3 | Declarative screens and design system |
| Navigation | Navigation Compose 2.8.5 | Single-activity route graph |
| State | Coroutines, Flow, StateFlow, lifecycle-runtime-compose | Reactive state and lifecycle-aware collection |
| DI | Hilt 2.54, Hilt Navigation, Hilt Work | Application graph, ViewModels, workers |
| Database | Room 2.7.0 with KSP | Local relational storage and invalidation Flows |
| Preferences | DataStore Preferences 1.1.1 | App settings and authorization lease |
| Background work | WorkManager 2.10.0 | Scheduled backup and trash purge |
| Remote authorization | Firebase Firestore, Firebase App Check | Device whitelist and attestation |
| Images/video | Coil 3, Media3 1.5.1 | Media rendering and playback |
| Editing | `richeditor-compose` | HTML-backed rich notes |
| Storage access | DocumentFile, FileProvider | Backup destinations, capture, and sharing |
| Permissions | Accompanist Permissions, Activity Result APIs | Camera, microphone, storage, notifications |
| Build | Gradle 8.10.2, AGP 8.7.3, KSP | Android build and code generation |

The source of truth for versions is [[Overview/Dependencies|Dependencies]]. Architecture-level usage is described in [[Layers/UI Layer|UI Layer]], [[Layers/Local Storage|Local Storage]], and [[Layers/Networking|Networking]].

## Source references

- `gradle/libs.versions.toml`
- `gradle/wrapper/gradle-wrapper.properties`
- `app/build.gradle.kts`
- `core/build.gradle.kts`
- `data/build.gradle.kts`
- `features/build.gradle.kts`
