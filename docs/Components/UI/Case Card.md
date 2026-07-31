# Case Card

> **In plain words** — the small summary tile representing a case, used by the dashboard, search, shifts, sessions, and the diagnosis feed. Because it is one component, a change to how a case is summarised takes effect everywhere at once. It receives a `Case` and an `onClick`, and knows nothing about where it is being shown.

## Purpose

Provide the shared compact representation of a clinical case.

## Responsibilities

Display primary/fallback media, patient name and age, date, mechanism preview, up to three diagnosis chips, and click/optional long-click behavior.

## Dependencies

`Case`, Coil `AsyncImage`, Material 3, and the Kairos theme extras.

## Called By

Case feed, consultation calendar, and shift detail screens.

## Calls

Caller-provided click callbacks; Coil loads `File(media.filePath)`.

## Important Methods

`CaseCard(...)`; private `CaseThumbnail(...)` adds a video overlay when needed.

## Design Patterns

Reusable domain-model view with optional gesture capability.

## Common Pitfalls

The thumbnail assumes repositories have resolved stored relative paths to usable absolute paths. A non-image primary file may still be passed to Coil; selection falls back to the first attachment.

## Related Pages

- [[Components/Repositories/CaseRepository|Case Repository]]
- [[Features/Case Feed|Case Feed]]

## Source references

- `core/src/main/java/com/taha/kairos/core/components/CaseCard.kt`
- `features/src/main/java/com/taha/kairos/features/cases/CaseFeedScreen.kt`
- `features/src/main/java/com/taha/kairos/features/shifts/ShiftDetailScreen.kt`
