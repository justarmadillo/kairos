# ViewModels

> **In plain words** — the brain behind each screen. A ViewModel holds what the screen should currently show, listens to the repositories for changes, and turns user actions into data operations. Its defining property is that it **survives screen rotation**: turn the phone sideways and Android rebuilds the visuals but hands back the same ViewModel, so your loaded list and half-typed form are still there. Because Kairos has no separate use-case layer, ViewModels are also where multi-step feature logic lives — for example "save the patient, then the case, then the media". See [[Learn/Architecture Patterns|Architecture Patterns]] and [[Learn/Coroutines And Flow|Coroutines And Flow]].

## Role

Twelve Hilt ViewModels convert repository streams and user events into screen state. They are the application's orchestration layer because there are no dedicated use-case classes.

## Conventions

- Long-lived state is exposed as `StateFlow`.
- Repository `Flow`s are combined, switched with `flatMapLatest`, or converted via `stateIn`.
- Mutations run in `viewModelScope`; selected aggregate operations use [[Components/Repositories/DataSafetyCoordinator|Data Safety Coordinator]].
- Route arguments are read from `SavedStateHandle` in detail/feed ViewModels.
- One-shot outcomes are represented as state fields and explicitly cleared rather than emitted through a separate event bus.

## Inventory

See [[Components/ViewModels/ViewModels Index|ViewModels Index]] for every class. The largest coordinator is [[Components/ViewModels/PatientCaseViewModel|PatientCaseViewModel]], while [[Components/ViewModels/AuthorizationGateViewModel|AuthorizationGateViewModel]] owns the fail-closed launch gate.

## Pitfalls

- `SharingStarted.WhileSubscribed(5_000)` means upstream collection pauses without subscribers.
- Several ViewModels translate exceptions to user messages, but the policy is not uniform; see [[Architecture/Error Handling|Error Handling]].
- `DiagnosisBrowseViewModel` initializes alphabetical sorting instead of observing the persisted diagnosis-sort setting.

## Related pages

- [[Architecture/State Management|State Management]]
- [[Diagrams/ViewModel Interactions|ViewModel Interactions]]
- [[Components/Use Cases|Use Cases]]

## Source references

- `features/src/main/java/com/taha/kairos/features/dashboard/DashboardViewModel.kt`
- `features/src/main/java/com/taha/kairos/features/patient/PatientCaseViewModel.kt`
- `features/src/main/java/com/taha/kairos/features/cases/CaseDetailViewModel.kt`
- `app/src/main/java/com/taha/kairos/authorization/AuthorizationGateViewModel.kt`
