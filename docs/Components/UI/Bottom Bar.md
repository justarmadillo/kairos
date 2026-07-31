# Bottom Bar

> **In plain words** — the five-tab strip at the bottom. It does not navigate; it reports which tab was tapped and the activity performs the move — the same "events up, decisions at the top" rule the whole UI follows. It appears only on the five top-level screens, so detail screens get the full height.

## Purpose

Render the five top-level destinations and report tab selections to the activity-owned navigator.

## Responsibilities

Iterate `TopLevelDestination`, choose active/inactive icons, apply Kairos colors, and suppress reselect callbacks for the current tab.

## Dependencies

Compose Material 3, `TopLevelDestination`, and Kairos theme extras.

## Called By

`MainActivity.AuthorizedAppContent`, only when the current route is top level.

## Calls

`onTabSelected(destination)`; `MainActivity` performs save/restore-state navigation.

## Important Methods

`BottomBar(...)`.

## Design Patterns

Stateless navigation presentation; route ownership remains in [[Architecture/Navigation|Navigation]].

## Common Pitfalls

Nested routes intentionally hide the bar. Adding a top-level route requires updating `TopLevelDestination`, the `NavHost`, and any desired widget/deep-link allowlist separately.

## Related Pages

- [[Components/UI/Kairos Top Bar|Kairos Top Bar]]
- [[Diagrams/Navigation Graph|Navigation Graph]]

## Source references

- `app/src/main/java/com/taha/kairos/ui/BottomBar.kt`
- `app/src/main/java/com/taha/kairos/navigation/Destinations.kt`
- `app/src/main/java/com/taha/kairos/MainActivity.kt`
