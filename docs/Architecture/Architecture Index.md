# Architecture

> **In plain words** — this section describes how the app *behaves while running*: how data moves, how objects get created, how screens change, how failures are handled, and what happens in the background. It assumes you know the vocabulary; if not, read [[Learn/Architecture Patterns|Architecture Patterns]], [[Learn/Coroutines And Flow|Coroutines And Flow]], and [[Learn/Dependency Injection Explained|Dependency Injection Explained]] first.

- [[Architecture/Data Flow|Data Flow]]
- [[Architecture/Application Lifecycle|Application Lifecycle]]
- [[Architecture/Dependency Injection|Dependency Injection]]
- [[Architecture/Navigation|Navigation]]
- [[Architecture/State Management|State Management]]
- [[Architecture/Error Handling|Error Handling]]
- [[Architecture/Logging|Logging]]
- [[Architecture/Background Work|Background Work]]
- [[Architecture/Configuration|Configuration]]

Visual summaries are collected in [[Diagrams/Diagrams Index|Diagrams]]. Concrete sequences are in [[Execution Flows/Execution Flows Index|Execution Flows]].

## Source references

- `app/src/main/java/com/taha/kairos/KairosApplication.kt`
- `app/src/main/java/com/taha/kairos/MainActivity.kt`
- `data/src/main/java/com/taha/kairos/data/di/DataModule.kt`
- `features/src/main/java/com/taha/kairos/features/patient/PatientCaseViewModel.kt`
