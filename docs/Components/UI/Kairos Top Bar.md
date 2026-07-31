# Kairos Top Bar

> **In plain words** — the standard header: title on the left, optional actions on the right. Extracting it means every screen's header looks and behaves identically without each one re-implementing it.

## Purpose

Standardize top-level screen titles and optional actions.

## Responsibilities

Render a Material 3 top app bar with the Kairos typography/colors and caller-provided action content.

## Dependencies

Compose Material 3 and the active Kairos theme.

## Called By

Dashboard, shifts, consultation, diagnosis browser, and settings screens.

## Calls

Only action composables/callbacks supplied by the caller.

## Important Methods

`KairosTopBar(...)`.

## Design Patterns

Reusable application chrome with content-slot composition.

## Common Pitfalls

Detail screens that require back navigation use their own bars; this component should not invent navigation ownership.

## Related Pages

- [[Components/UI/Bottom Bar|Bottom Bar]]
- [[Architecture/Navigation|Navigation]]

## Source references

- `core/src/main/java/com/taha/kairos/core/components/KairosTopBar.kt`
- `features/src/main/java/com/taha/kairos/features/dashboard/DashboardScreen.kt`
