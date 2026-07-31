# Glossary

Every term this wiki uses, alphabetical, in one line each. Follow the link for depth.

## A

**AAB (Android App Bundle)** — publishing format uploaded to Google Play; Play builds per-device APKs from it.
**Activity** — a system-managed app window. Kairos has one: `MainActivity`. → [[Learn/Android App Basics|Android App Basics]]
**Annotation** — an `@Label` attached to code that a tool reads (Room, Hilt, Compose). → [[Learn/Kotlin Basics|Kotlin Basics]]
**APK** — Android Package, the installable app file.
**App Check** — Firebase feature attesting a request came from a genuine app install.
**API level** — the Android version number a build targets or requires (`minSdk 26`, `targetSdk 35`).
**Argument (navigation)** — a value carried in a route, e.g. `case_feed/{diagnosisId}`.
**Atomic** — happens completely or not at all. → transaction

## B

**Backing field / property** — the stored value behind a `val` or `var`.
**BOM (Bill of Materials)** — a coordinated version set for a family of libraries, e.g. the Compose BOM.
**Boot count** — how many times the device has rebooted; used to invalidate offline authorization leases. → [[Learn/Security And Privacy Basics|Security And Privacy Basics]]
**Build type** — `debug` or `release`, two configurations of the same code.

## C

**Callback** — a function passed into something so it can call you back later, e.g. `onCaseClick`.
**Cold flow** — a `Flow` that does nothing until collected. Opposite of hot.
**Compile time** — when the build runs; before anything executes. → [[Learn/What Is An App|What Is An App]]
**Composable** — a `@Composable` function that describes UI. → [[Learn/Jetpack Compose Basics|Jetpack Compose Basics]]
**Compose (Jetpack Compose)** — the declarative UI toolkit Kairos uses for every screen.
**Context** — the Android object giving access to system services, files, and resources.
**Coroutine** — a unit of work that can pause and resume without blocking a thread. → [[Learn/Coroutines And Flow|Coroutines And Flow]]
**Cross-reference table** — see junction table.

## D

**DAO (Data Access Object)** — an interface where each function is one database operation. → [[Components/DAOs/DAOs Index|DAOs]]
**data class** — a Kotlin class that exists to hold values.
**DataStore** — Android's modern key/value settings storage, exposed as a `Flow`. → [[Learn/Data Storage Choices|Data Storage Choices]]
**Declarative UI** — describing what the screen should look like for a given state, rather than mutating widgets.
**Dependency injection (DI)** — classes declare what they need; a container supplies it. → [[Learn/Dependency Injection Explained|Dependency Injection Explained]]
**Device ID** — the derived identifier used to whitelist a phone. Not a user account.
**Dispatcher** — which threads a coroutine runs on (`Main`, `IO`, `Default`).
**Domain model** — the app's own shape for a concept (`Case`), independent of storage.
**dp** — density-independent pixel, a screen-size-independent unit. `sp` is its text equivalent.

## E

**Elvis operator (`?:`)** — "use this instead if the left side is null".
**Entity** — a Kotlin class annotated `@Entity`, representing one database table.
**Enum** — a fixed set of named options (`ThemeMode.LIGHT`).
**Extension function** — a function added onto an existing type from outside it.

## F

**Fail closed** — when a security check errors, deny access. Kairos's rule.
**FileProvider** — the safe way to share a file with another app via a temporary URI.
**Firestore** — Google's cloud database. In Kairos it stores *only* the device whitelist.
**Flow** — a stream of values over time. → [[Learn/Coroutines And Flow|Coroutines And Flow]]
**Foreign key** — a column pointing at another table's primary key.

## G

**Generics** — the `<T>` brackets answering "of what?": `List<Case>`.
**Gradle** — the build tool that compiles, generates, and packages the app. → [[Learn/Gradle And Modules|Gradle And Modules]]
**Grace (authorization)** — the 24–48 hour window where an offline lease still works but wants refreshing.

## H

**Hard delete** — permanently removing a row. Kairos does this only via the purge worker.
**Hilt** — the dependency injection framework built on Dagger.
**Hoisting (state)** — moving state up to the caller so a component stays controlled and reusable.
**Hot flow** — a stream that runs regardless of collectors and holds a current value (`StateFlow`).

## I

**Idempotent** — running it twice does the same as running it once.
**Immutable** — cannot be changed after creation. Kairos prefers `val` and copies.
**Index (database)** — a structure making lookups on a column fast.
**Interface** — a contract listing what can be asked for, with no implementation.
**Injection** — being handed a dependency rather than constructing it.

## J

**JOIN** — an SQL operation stitching rows from two tables together.
**Junction table** — a table whose rows are links, expressing many-to-many (`case_diagnoses`).

## K

**Kotlin** — the language Kairos is written in.
**KSP (Kotlin Symbol Processing)** — the build-time code generator Room and Hilt use.

## L

**Lambda** — a block of code passed as a value, written in `{ }`.
**LaunchedEffect** — Compose's way to run a coroutine once when something appears.
**LazyColumn** — a scrolling list that only builds visible rows.
**Lease (authorization)** — a stored record that this device was verified, valid for a bounded time.
**Lifecycle** — the sequence of created/started/resumed/paused/destroyed states Android puts components through.

## M

**Main thread** — the single thread allowed to touch the UI; must never be blocked.
**Manifest** — `AndroidManifest.xml`, declaring the app's components and permissions.
**Mapper** — code translating between entity and domain shapes. → [[Layers/Mappers|Mappers]]
**Migration** — instructions for upgrading an existing database file to a new schema version.
**Minification** — stripping and renaming unused code in release builds.
**Module (Gradle)** — an independently compiled folder: `:app`, `:core`, `:data`, `:features`.
**Module (Hilt)** — a class of `@Provides`/`@Binds` recipes.
**Monotonic clock** — `elapsedRealtime`, a time source the user cannot roll back.
**MVVM** — Model–View–ViewModel. → [[Learn/Architecture Patterns|Architecture Patterns]]

## N

**NavHost / NavController** — the navigation graph and the object that moves between routes.
**Null** — the deliberate absence of a value; `?` marks a type that permits it.

## O

**Offline-first** — the device holds the authoritative data; the network is optional.
**Observer** — something subscribed to a stream of updates.

## P

**Package** — a namespace matching the folder path, e.g. `com.taha.kairos.core.model`.
**Permission** — a capability declared in the manifest and, for sensitive ones, granted by the user at runtime.
**Play Integrity** — Google's device/app attestation, used by App Check in release builds.
**Primary key** — the column uniquely identifying a row; `id` in every Kairos table.
**Pure function** — output depends only on inputs, no side effects. Cheap to test.

## R

**Recomposition** — Compose re-running a composable because state it read changed.
**Relation (`@Relation`)** — Room's mechanism for loading related rows alongside a parent.
**remember / rememberSaveable** — keep a value across recompositions / across rotation.
**Repository** — the object owning one slice of data and hiding where it lives. → [[Layers/Repositories|Repositories]]
**Room** — the library mapping Kotlin classes to SQLite. → [[Learn/Databases And Room|Databases And Room]]
**Route** — a text address for a screen, e.g. `case_feed/42?name=...`.
**Runtime** — while the app is actually executing on a device.

## S

**SAF (Storage Access Framework)** — Android's user-driven folder/file picker, used for export and backup.
**Scope (coroutine)** — the owner that can cancel a set of coroutines (`viewModelScope`).
**Schema** — the structure of the database: tables, columns, keys.
**Signing** — cryptographically stamping a release so Android can verify updates come from the same author.
**Singleton** — exactly one instance for the whole app.
**Soft delete** — marking a row deleted instead of removing it. → [[Features/Trash and Retention|Trash and Retention]]
**SQL** — the language for querying a relational database.
**SQLite** — the database engine built into Android; Kairos's file is `kairos.db`.
**Stack trace** — the list of calls leading to a crash; read top-down for the first line of your own code.
**StateFlow** — a hot flow holding a current value, the standard shape of UI state.
**suspend** — marks a function that may pause; callable only from a coroutine.

## T

**targetSdk / compileSdk / minSdk** — Android version the app targets, compiles against, and minimally requires.
**Thread** — an independent line of execution.
**Transaction** — a group of database writes that succeed or fail together.
**Type** — the promise about what kind of value something is.

## U

**UI state** — one data class describing everything a screen needs to render.
**Unidirectional data flow** — state down, events up. → [[Architecture/State Management|State Management]]
**Upsert** — insert if new, update if existing.
**URI** — an address for a resource; used for picked folders and shared files.

## V

**val / var** — read-only vs reassignable.
**Version catalog** — `gradle/libs.versions.toml`, the single list of library versions.
**ViewModel** — the state holder that survives screen rotation. → [[Layers/ViewModels|ViewModels]]

## W

**Widget (home screen)** — the quick-capture shortcut, implemented as a broadcast receiver. → [[Features/Quick Capture Widget|Quick Capture Widget]]
**WorkManager** — Android's scheduler for deferrable background work that must survive app death and reboot.
**Worker** — one scheduled job (`ScheduledBackupWorker`, `TrashPurgeWorker`).

## Related pages

- [[Learn/Learn Index|Learn Index]]
- [[Learn/Design Patterns Glossary|Design Patterns Glossary]]
