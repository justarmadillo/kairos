# PatientNameFormatter

> **In plain words** — capitalises patient names, and a good lesson in why "simple" string handling rarely is. It must capitalise after hyphens and both kinds of apostrophe (`O'Connor`, `D'Angelo`, `Jean-Luc`), preserve existing capitals (`McDONALD` must not become `Mcdonald`), and handle accented and combining characters. Each of those is a real test case in `PatientNameTest`. See [[Learn/Testing Basics|Testing Basics]].

## Purpose

Capitalizes the first letter of each patient-name word while preserving every remaining character as entered.

## Responsibilities

- Iterate safely over Unicode code points.
- Treat punctuation and whitespace as the start of a new word.
- Keep combining marks attached without starting a word.
- Apply locale-aware uppercase only to word-initial letters.

## Dependencies

- Java `Character`, Kotlin `StringBuilder`, and `Locale`.

## Called By

- [[Components/Mappers/PatientMapper]]
- [[Components/Repositories/DashboardRepository]] and [[Components/Repositories/SearchRepository]].
- Patient case UI state updates.

## Calls

- Unicode code-point and character-type APIs.

## Important Methods

- `String.toCapitalizedPatientName(locale)` performs the transformation.

## Design Patterns

- Pure extension function with injectable locale.

## Common Pitfalls

- Remaining letters are deliberately not lowercased; mixed user casing is preserved.
- Digits continue the current word.
- Locale-sensitive uppercasing may expand a character to multiple code points.

## Related Pages

- [[Layers/Models]]
- [[Components/Repositories/PatientRepository]]
- [[Components/Mappers/PatientMapper]]

## Source References

- `core/src/main/java/com/taha/kairos/core/model/PatientName.kt`
- `core/src/test/java/com/taha/kairos/core/model/PatientNameTest.kt`

