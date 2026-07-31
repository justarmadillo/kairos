# Diagnosis Autocomplete

> **In plain words** — the field where diagnoses are typed and appear as removable chips, with suggestions from diagnoses already in the database. Suggesting existing names is not a convenience feature but a data-quality one: it is what keeps one condition from being stored under three slightly different spellings and splitting its cases apart.

## Purpose

Edit a case's diagnosis-name list with chips, free text, and repository-backed suggestions.

## Responsibilities

Render selected diagnoses, query input, removable chips, suggestion rows, and an “add new” path.

## Dependencies

Compose Material 3, `Diagnosis` suggestions, and caller-managed query/selection state.

## Called By

`NewPatientTab`.

## Calls

`onQueryChange`, `onSelect`, and `onRemove`; searching and deduplication are handled by [[Components/ViewModels/PatientCaseViewModel|PatientCaseViewModel]].

## Important Methods

`DiagnosisAutocomplete(...)` and private `DiagnosisSuggestionRow(...)`.

## Design Patterns

Fully state-hoisted form component.

## Common Pitfalls

The component does not debounce input or enforce uniqueness. Selecting arbitrary typed text is valid and later causes repository get-or-create during case save.

## Related Pages

- [[Components/Repositories/DiagnosisRepository|Diagnosis Repository]]
- [[Features/Diagnosis Browser|Diagnosis Browser]]

## Source references

- `core/src/main/java/com/taha/kairos/core/components/DiagnosisAutocomplete.kt`
- `features/src/main/java/com/taha/kairos/features/patient/NewPatientTab.kt`
