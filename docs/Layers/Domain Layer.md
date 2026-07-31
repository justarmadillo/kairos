# Domain Layer

> **In plain words** — the app's own vocabulary, independent of any technology: what a `Case` is, what a `Patient` is, and what may be asked of them. It contains **no** database code, no screens, and no network calls — just plain Kotlin classes and interfaces. This is the shared middle that both the screens and the storage code agree on, which is exactly how they avoid depending on each other. See [[Learn/Architecture Patterns|Architecture Patterns]].

## Role

The domain surface is the non-Android-specific portion of `:core`: immutable models, repository contracts, authorization policy, and data-safety abstractions. Feature and app code compile against these contracts; `:data` supplies implementations.

## Contents

- Clinical models described in [[Layers/Models|Models]].
- Repository interfaces catalogued in [[Layers/Repositories|Repositories]].
- Pure authorization evaluation in [[Components/Utilities/AuthorizationLeasePolicy|Authorization Lease Policy]].
- The cross-cutting mutation contract [[Components/Repositories/DataSafetyCoordinator|Data Safety Coordinator]].

## Boundary decisions

There is no separate `domain` Gradle module and no `UseCase` class family. ViewModels call repository interfaces directly, so multi-repository orchestration lives in ViewModels or repository implementations. See [[Components/Use Cases|Use Cases]] for the explicit inventory.

Android-aware reusable UI, media engines, and file management also live in `:core`; therefore `:core` is a shared-core module rather than a strictly pure domain module.

## Related pages

- [[Overview/Gradle Modules|Gradle Modules]]
- [[Layers/Data Layer|Data Layer]]
- [[Diagrams/Dependency Graph|Dependency Graph]]

## Source references

- `core/build.gradle.kts`
- `core/src/main/java/com/taha/kairos/core/model/Case.kt`
- `core/src/main/java/com/taha/kairos/core/repository/CaseRepository.kt`
- `core/src/main/java/com/taha/kairos/core/authorization/AuthorizationLeasePolicy.kt`
