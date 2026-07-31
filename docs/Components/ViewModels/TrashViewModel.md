# TrashViewModel

> **In plain words** — the brain behind the trash screen. It merges four separate streams — trashed patients, cases, shifts, and sessions — into one list, and sends each restore request back to the repository that owns that type. Restored items disappear from the list on their own, because the underlying queries no longer match them.

## Purpose

Provide one reactive recovery view over every soft-deletable record type.

## Responsibilities

- Observe trashed patients, cases, shifts, and consultation sessions.
- Combine those sources into grouped `TrashUiState`.
- Restore individual records through their owning repositories.

## Dependencies

- [[Components/Repositories/PatientRepository|PatientRepository]]
- [[Components/Repositories/CaseRepository|CaseRepository]]
- [[Components/Repositories/ShiftRepository|ShiftRepository]]
- [[Components/Repositories/ConsultationRepository|ConsultationRepository]]

## Called By

`TrashScreen` collects `ui` and dispatches each row's Restore action to the matching method.

## Calls

- Four `observeTrashed()` flows.
- `restore(id)` on the patient, case, shift, or consultation repository.

## Important Methods

- Combined `ui` property — joins all four sources and clears loading on first combined emission.
- `restorePatient(id)`, `restoreCase(id)`, `restoreShift(id)`, and `restoreSession(id)`.

## Design Patterns

- `@HiltViewModel` with multiple repository contracts.
- Fan-in Flow composition through `combine`.
- Immutable grouped state shared with `SharingStarted.WhileSubscribed(5_000)`.
- Thin command methods that preserve repository ownership boundaries.

## Common Pitfalls

- Restore operations have no progress, error, or duplicate-click guard.
- Permanent purge is not owned by this ViewModel; it belongs to `TrashPurgeWorker`.
- A consultation-session group is supported even though current consultation UI exposes no session delete action.
- Restoring related items independently can yield unintuitive visibility until all desired parents/children are restored.

## Related Pages

- [[Features/Trash and Retention|Trash and Retention]]
- [[Features/Settings and Backup|Settings and Backup]]
- [[Components/Workers/TrashPurgeWorker|TrashPurgeWorker]]
- [[Architecture/Background Work]]

## Source references

- `features/src/main/java/com/taha/kairos/features/settings/TrashViewModel.kt`
- `features/src/main/java/com/taha/kairos/features/settings/TrashScreen.kt`
- `core/src/main/java/com/taha/kairos/core/repository/PatientRepository.kt`
- `core/src/main/java/com/taha/kairos/core/repository/CaseRepository.kt`
- `core/src/main/java/com/taha/kairos/core/repository/ShiftRepository.kt`
- `core/src/main/java/com/taha/kairos/core/repository/ConsultationRepository.kt`
