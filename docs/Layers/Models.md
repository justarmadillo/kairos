# Models

> **In plain words** — a *model* is a Kotlin class describing one kind of thing the app deals with: a patient, a case, a diagnosis, a shift. They are plain data holders with no behaviour and no Android code, which is why they can be created in one line in a test. Note the deliberate split further down the page: **domain models** (`Case`) are what screens see, while **entities** (`CaseEntity`) are what the database stores, and the entity carries bookkeeping columns like `is_deleted` that the UI must never have to think about. See [[Learn/Programming Fundamentals|Programming Fundamentals]] and [[Learn/Architecture Patterns|Architecture Patterns]].

## Domain models

| Model | Key relationships |
|---|---|
| `Patient`, `PatientPhone` | Patient identity and replace-on-save phone list |
| `Case` | Patient aggregate with diagnoses and media |
| `Diagnosis` | Case classification with computed case count |
| `MediaItem`, `MediaType` | Attachment metadata and IMAGE/VIDEO/AUDIO/FILE type |
| `Shift` | Dated grouping with computed case count |
| `ConsultationSession` | Consultation date with computed case count |
| `AppSettings` | Theme, consultation day, diagnosis sort, backup configuration/status |

Repository-specific read/result models include `RecentCase`, `SearchResult`, `BackupResult`, and `RestoreResult`. Authorization has a separate model family: `AuthorizationTime`, `AuthorizationLease`, `CachedAuthorization`, and `RemoteAuthorizationResult`.

## Persistence distinction

Room entities carry storage fields such as `is_deleted`, remote placeholders, and sync state. Mappers intentionally keep these separate from presentation-friendly aggregates. Remote/sync columns are reserved but no synchronization implementation consumes them.

## Related pages

- [[Layers/Mappers|Mappers]]
- [[Components/Databases/KairosDatabase|Kairos Database]]
- [[Diagrams/Database Relationships|Database Relationships]]

## Source references

- `core/src/main/java/com/taha/kairos/core/model/Patient.kt`
- `core/src/main/java/com/taha/kairos/core/model/Case.kt`
- `core/src/main/java/com/taha/kairos/core/model/MediaItem.kt`
- `core/src/main/java/com/taha/kairos/core/model/AppSettings.kt`
- `core/src/main/java/com/taha/kairos/core/authorization/AuthorizationModels.kt`
- `data/src/main/java/com/taha/kairos/data/db/entities/CaseEntities.kt`
