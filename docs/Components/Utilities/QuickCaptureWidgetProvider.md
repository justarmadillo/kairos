# QuickCaptureWidgetProvider

> **In plain words** — the home-screen widget. A widget is not a screen; it is a small layout drawn by the launcher, so it cannot run app code or read the database — which is also why it deliberately shows no data. Its two buttons carry a pre-packaged "launch the app like this" instruction (a `PendingIntent`), marked *immutable* so no other app can alter it in flight. The app then validates the requested destination against a fixed list before navigating. See [[Learn/Android App Basics|Android App Basics]].

## Purpose

Provides a static home-screen widget that deep-links to new case capture or global search.

## Responsibilities

- Build widget `RemoteViews` for every widget instance.
- Attach immutable `PendingIntent`s for the two allowed destinations.
- Reuse `MainActivity` as the authorization-gated entry point.

## Dependencies

- Android AppWidget/RemoteViews APIs, `MainActivity`, navigation route constants, and widget XML resources.

## Called By

- Android's app-widget host through the manifest receiver declaration.

## Calls

- `AppWidgetManager.updateAppWidget`.
- `PendingIntent.getActivity` with destination extras.

## Important Methods

- `onUpdate` refreshes all widget IDs.
- `buildViews` wires new-case and search buttons.
- `destinationIntent` targets `MainActivity` with update-current and immutable flags.

## Design Patterns

- Stateless `AppWidgetProvider` and deep-link intent gateway.

## Common Pitfalls

- The widget sends a string extra, but `MainActivity` validates it against a strict allowlist before navigation.
- The widget does not bypass device authorization.
- Request codes must remain distinct so the two PendingIntents do not overwrite each other.

## Related Pages

- [[Architecture/Navigation]]
- [[Features/Patient and Case Capture]]
- [[Features/Global Search]]

## Source References

- `app/src/main/java/com/taha/kairos/widget/QuickCaptureWidgetProvider.kt`
- `app/src/main/java/com/taha/kairos/MainActivity.kt`
- `app/src/main/res/layout/widget_quick_capture.xml`
- `app/src/main/res/xml/widget_quick_capture_info.xml`
