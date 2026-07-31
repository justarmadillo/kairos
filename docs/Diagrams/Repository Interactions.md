# Repository Interactions

> **In plain words** — which ViewModels use which repositories, and which storage each repository touches. Read it to answer "if I change this repository, what breaks?" — every ViewModel with an arrow into it. Note that several ViewModels share the same repository; that is fine, because a repository is a singleton with no per-screen state. See [[Layers/Repositories|Repositories]].

```mermaid
flowchart LR
    subgraph Presentation["ViewModels"]
        DashVM["Dashboard"]
        SearchVM["Search"]
        CaseVM["Case / capture"]
        OrganizeVM["Shift / consultation / diagnosis"]
        SettingsVM["Settings"]
        TrashVM["Trash"]
        AuthVM["Authorization"]
    end

    subgraph Contracts[":core repository contracts"]
        DashboardRepo["DashboardRepository"]
        PatientRepo["PatientRepository"]
        CaseRepo["CaseRepository"]
        MediaRepo["MediaRepository"]
        OrganizeRepo["Shift / Consultation / Diagnosis repositories"]
        SettingsRepo["SettingsRepository"]
        BackupRepo["BackupRepository"]
        SearchRepo["SearchRepository"]
        AuthRepo["DeviceAuthorizationRepository"]
    end

    DashVM --> DashboardRepo
    DashVM --> SettingsRepo
    SearchVM --> SearchRepo
    CaseVM --> PatientRepo
    CaseVM --> CaseRepo
    CaseVM --> MediaRepo
    CaseVM --> OrganizeRepo
    OrganizeVM --> OrganizeRepo
    OrganizeVM --> CaseRepo
    OrganizeVM --> SettingsRepo
    SettingsVM --> SettingsRepo
    SettingsVM --> BackupRepo
    TrashVM --> PatientRepo
    TrashVM --> CaseRepo
    TrashVM --> OrganizeRepo
    AuthVM --> AuthRepo
    AuthVM --> BackupRepo

    DashboardRepo --> Room["Room DAOs"]
    PatientRepo --> Room
    CaseRepo --> Room
    MediaRepo --> Room
    OrganizeRepo --> Room
    SearchRepo --> Room
    SettingsRepo --> DataStore["kairos_prefs DataStore"]
    AuthRepo --> AuthStore["device_authorization DataStore"]
    AuthRepo --> Firestore["Firestore"]
    MediaRepo --> Files["Media files"]
    BackupRepo --> Room
    BackupRepo --> Files
    BackupRepo --> SAF["Storage Access Framework"]
```

Hilt binds each contract to its data implementation. Room-backed writes and backup-sensitive file operations coordinate through `DataSafetyCoordinator`.

See [[Layers/Repositories|Repositories]], [[Architecture/Data Flow|Data Flow]], and [[Components/Repositories/Repositories Index|Repository components]].

## Source references

- `data/src/main/java/com/taha/kairos/data/di/DataModule.kt`
- `features/src/main/java/com/taha/kairos/features/patient/PatientCaseViewModel.kt`
- `features/src/main/java/com/taha/kairos/features/settings/SettingsViewModel.kt`
- `app/src/main/java/com/taha/kairos/authorization/AuthorizationGateViewModel.kt`
- `data/src/main/java/com/taha/kairos/data/repository/CaseRepositoryImpl.kt`
- `data/src/main/java/com/taha/kairos/data/backup/BackupEngine.kt`
