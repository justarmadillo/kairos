# Navigation Graph

> **In plain words** — the map of screens and the paths between them. Each box is a *route* (a screen's text address); `{caseId}` marks a value carried in the address. Everything hangs off the authorization gate, because when locked no screen exists at all. See [[Architecture/Navigation|Navigation]].

```mermaid
flowchart LR
    Auth["Device authorization"] -->|Granted| Dashboard["dashboard"]
    Auth -->|Locked| Locked["locked / export"]

    Dashboard --> Search["search"]
    Dashboard --> CaseDetail["case_detail/{caseId}"]

    Shifts["shifts"] --> ShiftDetail["shift_detail/{shiftId}"]
    Shifts --> PatientCase["patient_case"]
    ShiftDetail --> PatientCase
    ShiftDetail --> CaseDetail

    Consultation["consultation"] --> PatientCase
    Consultation --> CaseDetail

    Cases["cases"] --> CaseFeed["case_feed/{diagnosisId}"]
    Cases --> PatientCase
    CaseFeed --> CaseDetail

    Search --> CaseDetail
    CaseDetail --> PatientCase
    CaseDetail --> CaseFeed
    CaseDetail --> Viewer["image_viewer/{caseId}"]

    Settings["settings"] --> Trash["trash"]

    Bottom["Bottom bar"] --> Dashboard
    Bottom --> Shifts
    Bottom --> Consultation
    Bottom --> Cases
    Bottom --> Settings

    Widget["Quick Capture Widget"] -. "allowlisted extra" .-> PatientCase
    Widget -. "allowlisted extra" .-> Search
```

All non-top-level destinations pop back. `patient_case` adds optional shift, session, or edit-case IDs; `case_feed` adds an encoded diagnosis name; the viewer adds an optional initial index.

See [[Architecture/Navigation|Navigation]] and [[Execution Flows/Navigation Flow|Navigation Flow]].

## Source references

- `app/src/main/java/com/taha/kairos/MainActivity.kt`
- `app/src/main/java/com/taha/kairos/navigation/Destinations.kt`
- `app/src/main/java/com/taha/kairos/navigation/KairosNavHost.kt`
- `app/src/main/java/com/taha/kairos/widget/QuickCaptureWidgetProvider.kt`

