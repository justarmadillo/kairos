# Feature Relationships

> **In plain words** — which features lead into which, from the user's point of view rather than the code's. Useful for seeing that almost everything eventually flows into case capture or case detail, and that device authorization sits in front of all of it. See [[Features/Features Index|Features]].

```mermaid
graph TD
    Authorization["Device Authorization"] --> App["Protected application"]
    Widget["Quick Capture Widget"] --> Capture["Patient and Case Capture"]
    Widget --> Search["Global Search"]

    App --> Dashboard
    App --> Search
    App --> Shifts["Shift Management"]
    App --> Consultation["Consultation Calendar"]
    App --> Diagnoses["Diagnosis Browser"]
    App --> Settings["Settings and Backup"]

    Dashboard --> Detail["Case Detail and Sharing"]
    Search --> Detail
    Shifts --> Detail
    Consultation --> Detail
    Diagnoses --> Feed["Case Feed"]
    Feed --> Detail

    Shifts --> Capture
    Consultation --> Capture
    Diagnoses --> Capture
    Detail -->|Edit| Capture
    Detail --> Media["Media Capture and Playback"]
    Capture --> Media

    Settings --> Trash["Trash and Retention"]
    Settings --> Dashboard
```

Arrows represent navigation or an observable product dependency. For example, backup status configured in Settings is surfaced by Dashboard, while all case-discovery paths converge on Case Detail.

See [[Features/Features Index|Features]] and [[Diagrams/Navigation Graph|Navigation Graph]].

## Source references

- `app/src/main/java/com/taha/kairos/navigation/KairosNavHost.kt`
- `app/src/main/java/com/taha/kairos/widget/QuickCaptureWidgetProvider.kt`
- `features/src/main/java/com/taha/kairos/features/dashboard/DashboardViewModel.kt`
- `features/src/main/java/com/taha/kairos/features/patient/PatientCaseViewModel.kt`
- `features/src/main/java/com/taha/kairos/features/settings/SettingsViewModel.kt`

