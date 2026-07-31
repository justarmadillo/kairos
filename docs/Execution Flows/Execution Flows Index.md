# Execution Flows

> **In plain words** — these pages follow one action through the code, step by step, in the order it actually happens. Other sections describe *structure*; these describe *time*. The diagrams are mostly **sequence diagrams**: each vertical line is one participant, and each arrow is one participant calling another, read strictly top to bottom. If you are new, start with [[Execution Flows/Data Loading|Data Loading]], and read the beginner-paced version in [[Learn/Code Tour One Feature|Code Tour One Feature]] alongside it.

- [[Execution Flows/App Startup|App Startup]]
- [[Execution Flows/Login Flow|Login Flow]]
- [[Execution Flows/Data Loading|Data Loading]]
- [[Execution Flows/API Request Lifecycle|API Request Lifecycle]]
- [[Execution Flows/Navigation Flow|Navigation Flow]]
- [[Execution Flows/State Updates|State Updates]]
- [[Execution Flows/Background Jobs|Background Jobs]]
- [[Execution Flows/Database Operations|Database Operations]]

These traces complement the design-oriented [[Architecture/Architecture Index|Architecture]] pages and the visual [[Diagrams/Diagrams Index|Diagrams]].

## Source references

- `app/src/main/java/com/taha/kairos/KairosApplication.kt`
- `app/src/main/java/com/taha/kairos/MainActivity.kt`
- `data/src/main/java/com/taha/kairos/data/repository/CaseRepositoryImpl.kt`
- `features/src/main/java/com/taha/kairos/features/patient/PatientCaseViewModel.kt`

