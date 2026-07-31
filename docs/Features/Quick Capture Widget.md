# Quick Capture Widget

> **In plain words** — a small panel on the phone's home screen with two buttons: new case, and search. It deliberately shows **no data** — a home screen is visible to anyone glancing at the phone. Technically it is not a screen but a *broadcast receiver* supplying a static layout plus two `PendingIntent`s (a pre-packaged "launch the app like this" instruction). Because the app's entry activity is exported, incoming requests are checked against a fixed allowlist of destinations before navigating — never trust an instruction arriving from outside. See [[Learn/Android App Basics|Android App Basics]].

## Purpose

Offer one-tap home-screen entry to a new patient/case form or global search without exposing data in the widget itself.

## User Flow

The user adds the Kairos widget, taps “New case” or “Search,” passes through [[Features/Device Authorization|Device Authorization]], and arrives at the requested destination when access is granted.

## Execution Flow

`QuickCaptureWidgetProvider.onUpdate` installs two immutable activity `PendingIntent` values into a static `RemoteViews` layout. Each starts `MainActivity` with an extra. Authorized content consumes the extra once, verifies it against an allowlist, and navigates with `launchSingleTop`.

## Important Classes

- `QuickCaptureWidgetProvider`.
- `MainActivity.AuthorizedAppContent`.
- `RemoteViews`, `PendingIntent`, and widget XML resources.

## Related ViewModels

- [[Components/ViewModels/AuthorizationGateViewModel|AuthorizationGateViewModel]]
- [[Components/ViewModels/PatientCaseViewModel|PatientCaseViewModel]]
- [[Components/ViewModels/SearchViewModel|SearchViewModel]]

## Related Repositories

The widget has no repository dependency. Destination screens use their normal repositories after authorization.

## API Calls

Android AppWidget, `RemoteViews`, `PendingIntent.getActivity`, and activity intent extras are the only platform calls. There is no data or network API call.

## State Flow

The widget is stateless and has no periodic update (`updatePeriodMillis=0`). `rememberSaveable` in `MainActivity` prevents the same widget destination from being consumed again after rotation.

## Navigation

- New case extra: `patient_case`.
- Search extra: `search`.
- `MainActivity` is exported for launcher use, so extras are restricted to this two-value allowlist before navigation.

## Design Decisions

- Static UI avoids background data access and never displays patient information.
- Both actions use `FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TOP` and immutable, updateable pending intents.
- Navigation occurs only inside authorized content, so a widget cannot bypass the device gate.
- The widget receiver itself is not exported.

## Related Pages

- [[Features/Device Authorization|Device Authorization]]
- [[Features/Patient and Case Capture|Patient and Case Capture]]
- [[Features/Global Search|Global Search]]
- [[Architecture/Navigation]]

## Source references

- `app/src/main/java/com/taha/kairos/widget/QuickCaptureWidgetProvider.kt`
- `app/src/main/java/com/taha/kairos/MainActivity.kt`
- `app/src/main/res/layout/widget_quick_capture.xml`
- `app/src/main/res/xml/widget_quick_capture_info.xml`
- `app/src/main/AndroidManifest.xml`
