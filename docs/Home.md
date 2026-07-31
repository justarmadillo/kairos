# Kairos Engineering Wiki

Kairos is an offline-first Android application for recording patients, clinical cases, diagnoses, shifts, consultation sessions, and case media.

**New to programming?** Start at [[Learn/Learn Index|Learn]]. That section explains every concept the rest of this wiki assumes — Kotlin, Android, databases, coroutines, dependency injection — using this app's real code as the examples.

**Already a developer?** Start with [[Overview/Project Overview|Project Overview]], then follow the section indexes below.

## Wiki map

- [[Learn/Learn Index|Learn]] — beginner track: programming, Kotlin, Android, Room, Compose, DI, a full code tour, and a glossary.
- [[Overview/Overview Index|Overview]] — product scope, architecture, modules, build, and dependencies.
- [[Architecture/Architecture Index|Architecture]] — runtime behavior, DI, navigation, state, failures, configuration, and background work.
- [[Layers/Layers Index|Layers]] — UI, ViewModels, domain boundaries, data sources, persistence, models, and mappers.
- [[Features/Features Index|Features]] — one end-to-end page for each user-facing capability.
- [[Components/Components Index|Components]] — ViewModels, repositories, services, managers, APIs, databases, DAOs, workers, mappers, and utilities.
- [[Execution Flows/Execution Flows Index|Execution Flows]] — traced startup, authorization, loading, navigation, state, job, and database paths.
- [[Diagrams/Diagrams Index|Diagrams]] — architecture, dependency, navigation, API, database, repository, feature, and ViewModel graphs.

## Fast paths

- Absolute beginner: [[Learn/How To Read This Wiki|How To Read This Wiki]] → [[Learn/What Is An App|What Is An App]] → [[Learn/Programming Fundamentals|Programming Fundamentals]] → [[Learn/Code Tour One Feature|Code Tour One Feature]].
- New contributor: [[Overview/Folder Structure|Folder Structure]] → [[Overview/Gradle Modules|Gradle Modules]] → [[Architecture/Data Flow|Data Flow]].
- UI change: [[Layers/UI Layer|UI Layer]] → [[Layers/ViewModels|ViewModels]] → [[Architecture/State Management|State Management]].
- Persistence change: [[Layers/Local Storage|Local Storage]] → [[Components/Databases/KairosDatabase|Kairos Database]] → [[Execution Flows/Database Operations|Database Operations]].
- Authorization issue: [[Features/Device Authorization|Device Authorization]] → [[Execution Flows/Login Flow|Login Flow]] → [[Diagrams/API Flow|API Flow]].
- Backup issue: [[Features/Settings and Backup|Settings and Backup]] → [[Components/Services/BackupEngine|Backup Engine]] → [[Execution Flows/Background Jobs|Background Jobs]].
- Understanding the project as a whole: [[Learn/How Kairos Was Built|How Kairos Was Built]].

## What Kairos is, in one paragraph

An Android app that stores clinical records **on the phone itself**. Patients, cases, diagnoses, shifts, consultation sessions, notes, photos, audio, and files live in a local Room database; media bytes live in private app storage. Nothing clinical is uploaded anywhere. The only network call is a check asking a server whether this specific device is authorized to run the app; if that check fails for long enough the app locks itself, while still allowing a full data export. Everything else — search, dashboard statistics, PDF and ZIP export, backup, trash retention — runs offline.

## Documentation conventions

Behavior documented here follows current source code. Repository-relative paths under **Source references** are the evidence trail; wiki links point to the owning concept rather than repeating its details. Pages aimed at readers without a programming background carry an **In plain words** block near the top; deeper background for those readers lives in [[Learn/Learn Index|Learn]]. Terminology is defined once in [[Learn/Glossary|Glossary]].

## Source references

- `settings.gradle.kts`
- `app/src/main/java/com/taha/kairos/KairosApplication.kt`
- `app/src/main/java/com/taha/kairos/navigation/KairosNavHost.kt`
- `README.md`
