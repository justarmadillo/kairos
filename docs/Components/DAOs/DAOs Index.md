# DAOs Index

> **In plain words** — **DAO** means *Data Access Object*: an interface where every function is exactly one database operation, with the SQL written above it as an annotation. Nobody writes the bodies — Room generates them at build time and checks the SQL against the real schema, so a mistyped column fails the build rather than the app. DAOs contain no rules or decisions; that is the repository's job. See [[Learn/Databases And Room|Databases And Room]].

Room generates the implementations of these six interfaces. Repository and worker pages link to the exact queries they consume.

- [[Components/DAOs/PatientDao]]
- [[Components/DAOs/CaseDao]]
- [[Components/DAOs/DiagnosisDao]]
- [[Components/DAOs/CaseMediaDao]]
- [[Components/DAOs/ShiftDao]]
- [[Components/DAOs/ConsultationSessionDao]]

See [[Components/Databases/KairosDatabase]] and [[Execution Flows/Database Operations]].

## Source References

- `data/src/main/java/com/taha/kairos/data/db/dao/`
- `data/src/main/java/com/taha/kairos/data/db/KairosDatabase.kt`
- `data/src/main/java/com/taha/kairos/data/di/DataModule.kt`

