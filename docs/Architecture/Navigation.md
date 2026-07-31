# Navigation

> **In plain words** — moving between screens. The app has only one real Android window; everything else is Compose swapping content inside it. Each screen has a **route**, a text address like `case_detail/42`, and moving to a screen means navigating to its address. Screens themselves do not navigate — they call a callback such as `onCaseClick(id)`, and `KairosNavHost` (the single place that knows the whole map) decides where that leads. Only **IDs** travel in a route, never whole records; the destination reloads the record itself, so it always shows current data. See [[Learn/Jetpack Compose Basics|Jetpack Compose Basics]] and [[Learn/Code Tour One Feature|Code Tour One Feature]].

Kairos is a single-activity Compose application. `MainActivity` owns one `NavHostController`; `KairosNavHost` declares all routes and passes navigation callbacks into feature screens.

## Top-Level Destinations

`dashboard`, `shifts`, `consultation`, `cases`, and `settings` are represented by `TopLevelDestination`. The bottom bar appears only when the current route exactly matches one of them. Tab changes save and restore state and use `launchSingleTop`.

## Detail and Workflow Routes

- `search`
- `shift_detail/{shiftId}`
- `case_feed/{diagnosisId}?name={diagnosisName}`
- `case_detail/{caseId}`
- `image_viewer/{caseId}?index={index}`
- `trash`
- `patient_case?shiftId={shiftId}&sessionId={sessionId}&caseId={caseId}`

IDs are passed rather than full models. Destination ViewModels use `SavedStateHandle`, or `KairosNavHost` parses optional patient-case parameters and passes them into the screen. Diagnosis names are URI-encoded. All enter/exit transitions are disabled.

## External Entry

[[Features/Quick Capture Widget|Quick Capture Widget]] launches the exported activity with an allowlisted destination extra. `MainActivity` accepts only `patient_case` or `search`, then handles it once after authorization succeeds.

See [[Diagrams/Navigation Graph|Navigation Graph]] and [[Execution Flows/Navigation Flow|Navigation Flow]].

## Source references

- `app/src/main/java/com/taha/kairos/MainActivity.kt`
- `app/src/main/java/com/taha/kairos/navigation/Destinations.kt`
- `app/src/main/java/com/taha/kairos/navigation/KairosNavHost.kt`
- `app/src/main/java/com/taha/kairos/ui/BottomBar.kt`
- `app/src/main/java/com/taha/kairos/widget/QuickCaptureWidgetProvider.kt`
