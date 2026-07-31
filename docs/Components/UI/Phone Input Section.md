# Phone Input Section

> **In plain words** — the add/remove list of a patient's phone numbers. "Replace on save" means the whole set is rewritten when the patient is saved rather than each number being tracked and updated individually — far simpler, and impossible to leave in a half-updated state.

## Purpose

Edit the replace-on-save phone list for a patient.

## Responsibilities

Show existing numbers as removable chips and maintain a local draft for adding a number.

## Dependencies

`PatientPhone` and Compose Material 3.

## Called By

`NewPatientTab`.

## Calls

`onAdd(number, null)` and `onRemove(phone)`.

## Important Methods

`PhoneInputSection(...)`.

## Design Patterns

State hoisting for persisted values with composable-local ephemeral draft state.

## Common Pitfalls

Labels are not editable here even though the model supports them. Validation is limited to non-blank input; number normalization belongs to a higher layer if added later.

## Related Pages

- [[Components/Repositories/PatientRepository|Patient Repository]]
- [[Features/Patient and Case Capture|Patient and Case Capture]]

## Source references

- `core/src/main/java/com/taha/kairos/core/components/PhoneInputRow.kt`
- `core/src/main/java/com/taha/kairos/core/model/Patient.kt`
- `features/src/main/java/com/taha/kairos/features/patient/NewPatientTab.kt`
