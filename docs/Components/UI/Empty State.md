# Empty State

> **In plain words** — the message shown when a list has nothing in it. Small but important: a blank screen looks broken, while "No cases tagged with Femur fracture" tells the user the app worked and there is simply nothing to show. Note that screens only display it once loading has finished, so it never flashes before data arrives.

## Purpose

Provide consistent empty/search-zero/trashed-list messaging across features.

## Responsibilities

Render an icon, title, optional description, and optional call-to-action in Kairos styling.

## Dependencies

Compose Material 3 and `LocalKairosExtraColors`.

## Called By

Shifts, shift detail, search, consultation, diagnosis browser, case feed, and trash screens.

## Calls

An optional caller action callback.

## Important Methods

`EmptyState(...)`.

## Design Patterns

Stateless presentation component with optional slot-like action behavior.

## Common Pitfalls

Do not use it while data is still loading; callers must distinguish loading from a confirmed empty result.

## Related Pages

- [[Layers/UI Layer|UI Layer]]
- [[Architecture/State Management|State Management]]

## Source references

- `core/src/main/java/com/taha/kairos/core/components/EmptyState.kt`
- `features/src/main/java/com/taha/kairos/features/settings/TrashScreen.kt`
- `features/src/main/java/com/taha/kairos/features/search/SearchScreen.kt`
