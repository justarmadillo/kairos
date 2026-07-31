# Database Relationships

> **In plain words** — the database tables and how they connect. This is an **ER diagram**; the crow's-foot notation on a line means "many". `||--o{` therefore reads "one on the left, many on the right" — one patient has many cases. Tables whose name joins two others (`CASE_DIAGNOSES`, `SHIFT_CASES`, `CONSULTATION_CASES`) are *junction tables*: each row is one link, which is how "many-to-many" is expressed. "Cascade" means deleting the parent row removes its children automatically. See [[Learn/Databases And Room|Databases And Room]].

```mermaid
erDiagram
    PATIENTS ||--o{ PATIENT_PHONES : "owns (cascade)"
    PATIENTS ||--o{ CASES : "has (cascade)"
    CASES ||--o{ CASE_MEDIA : "attaches (cascade)"
    CASES ||--o{ CASE_DIAGNOSES : "classified by"
    DIAGNOSES ||--o{ CASE_DIAGNOSES : "labels"
    SHIFTS ||--o{ SHIFT_CASES : "groups"
    CASES ||--o{ SHIFT_CASES : "linked to"
    CONSULTATION_SESSIONS ||--o{ CONSULTATION_CASES : "groups"
    CASES ||--o{ CONSULTATION_CASES : "linked to"

    PATIENTS {
        long id PK
        string name
        int age
        boolean is_deleted
    }
    PATIENT_PHONES {
        long id PK
        long patient_id FK
        string number
    }
    CASES {
        long id PK
        long patient_id FK
        long case_date
        string notes_html
        boolean is_deleted
    }
    CASE_MEDIA {
        long id PK
        long case_id FK
        string file_path
        string media_type
        string original_file_name
    }
    DIAGNOSES {
        long id PK
        string name UK
    }
    CASE_DIAGNOSES {
        long case_id PK, FK
        long diagnosis_id PK, FK
    }
    SHIFTS {
        long id PK
        long date
        boolean is_deleted
    }
    SHIFT_CASES {
        long shift_id PK, FK
        long case_id PK, FK
    }
    CONSULTATION_SESSIONS {
        long id PK
        long date
        boolean is_deleted
    }
    CONSULTATION_CASES {
        long session_id PK, FK
        long case_id PK, FK
    }
```

All foreign keys shown use cascade deletion. User-facing deletion is normally soft deletion; hard deletion is deferred to retention cleanup. The current Room schema is version 2.

See [[Layers/Local Storage|Local Storage]], [[Execution Flows/Database Operations|Database Operations]], and [[Components/Databases/KairosDatabase|KairosDatabase]].

## Source references

- `data/src/main/java/com/taha/kairos/data/db/KairosDatabase.kt`
- `data/src/main/java/com/taha/kairos/data/db/entities/PatientEntities.kt`
- `data/src/main/java/com/taha/kairos/data/db/entities/CaseEntities.kt`
- `data/src/main/java/com/taha/kairos/data/db/entities/DiagnosisEntities.kt`
- `data/src/main/java/com/taha/kairos/data/db/entities/ShiftEntities.kt`
- `data/src/main/java/com/taha/kairos/data/db/entities/ConsultationEntities.kt`
