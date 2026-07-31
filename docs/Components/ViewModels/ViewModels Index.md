# ViewModels

> **In plain words** — twelve screen brains, one per screen. Each holds what its screen should display, subscribes to the repositories it needs, and turns taps into data operations. They survive screen rotation, and they never call each other — shared updates travel through the database instead. See [[Layers/ViewModels|ViewModels]] and [[Learn/Architecture Patterns|Architecture Patterns]].

ViewModels expose lifecycle-aware UI state and translate user actions into repository or platform operations.

- [[Components/ViewModels/AuthorizationGateViewModel|AuthorizationGateViewModel]]
- [[Components/ViewModels/DashboardViewModel|DashboardViewModel]]
- [[Components/ViewModels/SearchViewModel|SearchViewModel]]
- [[Components/ViewModels/ShiftsViewModel|ShiftsViewModel]]
- [[Components/ViewModels/ShiftDetailViewModel|ShiftDetailViewModel]]
- [[Components/ViewModels/ConsultationViewModel|ConsultationViewModel]]
- [[Components/ViewModels/DiagnosisBrowseViewModel|DiagnosisBrowseViewModel]]
- [[Components/ViewModels/CaseFeedViewModel|CaseFeedViewModel]]
- [[Components/ViewModels/CaseDetailViewModel|CaseDetailViewModel]]
- [[Components/ViewModels/PatientCaseViewModel|PatientCaseViewModel]]
- [[Components/ViewModels/SettingsViewModel|SettingsViewModel]]
- [[Components/ViewModels/TrashViewModel|TrashViewModel]]

## Related pages

- [[Architecture/State Management]]
- [[Architecture/Dependency Injection]]
- [[Layers/ViewModels]]
- [[Features/Features Index]]

## Source references

- `app/src/main/java/com/taha/kairos/authorization/AuthorizationGateViewModel.kt`
- `features/src/main/java/com/taha/kairos/features/`
