# Use Cases

> **In plain words** — this page documents something that is *not* here. In many Android projects there is an extra layer of "use case" classes between ViewModels and repositories, one per action (`SaveCaseUseCase`, `DeleteShiftUseCase`). Kairos deliberately has none: at this size such classes would mostly forward calls, so ViewModels talk to repositories directly and carry the orchestration themselves. Knowing when *not* to add a layer is a real engineering skill; the trade-off is recorded here rather than left implicit. See [[Learn/Architecture Patterns|Architecture Patterns]] and [[Learn/How Kairos Was Built|How Kairos Was Built]].

## Purpose

Records the deliberate absence of standalone use-case/interactor classes so the component inventory does not imply a layer that the source tree does not contain.

## Responsibilities

- Clarify where application actions are currently orchestrated.
- Identify the boundary for introducing use cases later without misclassifying repositories or utilities.

## Dependencies

- [[Layers/Domain Layer]], [[Layers/ViewModels]], and [[Components/Repositories/Repositories Index]].

## Called By

- No production component calls a `UseCase`; no such classes exist.

## Calls

- Feature ViewModels call repository contracts and focused services directly.
- Cross-repository consistency is coordinated by [[Components/Repositories/DataSafetyCoordinator]].

## Important Methods

- None. A repository-wide search finds no `*UseCase` class or interface.

## Design Patterns

- Pragmatic MVVM with repository abstraction rather than a separate interactor layer.

## Common Pitfalls

- Do not call a repository method a use case merely because it performs a business action.
- Multi-step orchestration such as patient/case/media save currently lives in its ViewModel; extracting it later changes ownership and test boundaries.

## Related Pages

- [[Overview/Architecture]]
- [[Layers/Domain Layer]]
- [[Components/ViewModels/ViewModels Index]]

## Source References

- `features/src/main/java/com/taha/kairos/features/`
- `app/src/main/java/com/taha/kairos/authorization/AuthorizationGateViewModel.kt`
- `core/src/main/java/com/taha/kairos/core/repository/`
