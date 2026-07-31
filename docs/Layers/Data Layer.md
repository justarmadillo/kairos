# Data Layer

> **In plain words** — everything that actually stores or fetches something: the database, its tables and queries, the settings store, the one network call, the backup engine, and the background jobs. It is the only part of the app that knows SQL or file paths exist. Screens never reach in here; they go through the repository contracts, and this layer supplies the implementations. See [[Learn/Databases And Room|Databases And Room]] and [[Learn/Data Storage Choices|Data Storage Choices]].

## Role

`:data` implements domain contracts and owns persistence, the only remote request, backup/restore, background workers, and dependency-injection bindings.

## Structure

- `repository/`: Room/DataStore-backed repository implementations.
- `db/`: [[Components/Databases/KairosDatabase|Room database]], entities, relations, DAOs, and migrations.
- `settings/`: settings [[Components/Databases/PreferencesStore|DataStore]].
- `authorization/`: authorization DataStore, Firestore verification, and cached worker guard.
- `backup/`: backup engine, global data lock, scheduling, retention, and workers.
- `mapper/`: database-to-domain transformations.
- `di/`: Hilt database providers and interface bindings.

## Flow direction

Reads are generally cold Room/DataStore `Flow`s mapped to domain types. Writes are suspending calls; destructive or consistency-sensitive writes serialize through [[Components/Repositories/DataSafetyCoordinator|Data Safety Coordinator]].

## Related pages

- [[Layers/Data Sources|Data Sources]]
- [[Layers/Local Storage|Local Storage]]
- [[Layers/Networking|Networking]]
- [[Diagrams/Repository Interactions|Repository Interactions]]

## Source references

- `data/build.gradle.kts`
- `data/src/main/java/com/taha/kairos/data/di/DataModule.kt`
- `data/src/main/java/com/taha/kairos/data/repository/CaseRepositoryImpl.kt`
- `data/src/main/java/com/taha/kairos/data/backup/BackupEngine.kt`
