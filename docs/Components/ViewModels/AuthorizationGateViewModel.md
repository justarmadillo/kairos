# AuthorizationGateViewModel

> **In plain words** — the gatekeeper. It decides, and keeps deciding, whether the app may be used: evaluate the stored lease, ask the server when needed, set a timer for the moment the lease expires, and re-check when the user returns to the app or a connection appears. It is described as a **state machine** because it is always in exactly one of three states — checking, granted, locked — and every input moves it between them. A lock hides protected content immediately, before any slower re-check runs. See [[Learn/Security And Privacy Basics|Security And Privacy Basics]].

## Purpose

Own the process-local device-authorization state machine that decides whether protected application content may be composed.

## Responsibilities

- Evaluate cached authorization against trusted clock observations.
- Verify due or invalid authorization with the server when validated connectivity exists.
- Grant fresh/offline-grace access, schedule lease boundaries, or fail closed.
- Re-evaluate on activity resume and connectivity recovery.
- Keep emergency backup export available while locked.

## Dependencies

- [[Components/Repositories/DeviceAuthorizationRepository|DeviceAuthorizationRepository]]
- [[Components/Repositories/BackupRepository|BackupRepository]]
- `AuthorizationClock`
- `NetworkMonitor`
- `AuthorizationLeasePolicy`

## Called By

- `MainActivity.DeviceAuthorizationGate()` collects `uiState`.
- `MainActivity.onResume()` calls `onAppResumed()`.
- `AuthorizationLockedScreen` drives `retry()` and the folder picker that leads to `exportData()`.

## Calls

- Authorization repository: `loadLease`, `verifyWithServer`, `saveAuthorized`, `recordObservation`, and `clearLease`.
- Backup repository: `export(folderUri)`.
- Clock: `now()`; connectivity: `isOnline`.
- `AuthorizationLeasePolicy.evaluate()`.

## Important Methods

- `onAppResumed()` — synchronously hides protected content when the hard deadline is unsafe, then requests evaluation.
- `retry()` — requests a serialized evaluation.
- `exportData(folderUri)` — validates input and maps backup results/exceptions to export state.
- `evaluateLease()` — central cached-state branch.
- `verifyRemotely()` — maps authorized, denied, and unavailable server results.
- `grant()`, `grantFreshLease()`, and `scheduleBoundary()` — publish access and arrange re-evaluation.
- `lockForUnexpectedFailure()` — sticky fail-closed path.
- `shouldHideProtectedContentOnResume()` — boot, monotonic deadline, and rollback guard.

## Design Patterns

- `@HiltViewModel` constructor injection.
- Explicit sealed-state machine exposed through read-only `StateFlow`.
- `Mutex` serialization prevents overlapping resume/network/retry checks.
- Timer-driven policy boundary and lifecycle-triggered revalidation.
- Defense in depth: in-memory sticky failure plus durable repository marker.

## Common Pitfalls

- This is not Firebase Authentication or a user login ViewModel.
- A reboot intentionally invalidates cached offline access.
- Cancellation is not specially rethrown in export's broad catch, so a cancelled export can become failure state.
- The UI currently ignores detailed `Locked.reason/message` and detailed export messages.
- `NetworkMonitor` reports only validated internet, not mere connectivity.

## Related Pages

- [[Features/Device Authorization|Device Authorization]]
- [[Execution Flows/Login Flow]]
- [[Architecture/Application Lifecycle]]
- [[Architecture/Error Handling]]

## Source references

- `app/src/main/java/com/taha/kairos/authorization/AuthorizationGateViewModel.kt`
- `app/src/main/java/com/taha/kairos/MainActivity.kt`
- `app/src/main/java/com/taha/kairos/authorization/AuthorizationScreens.kt`
- `core/src/main/java/com/taha/kairos/core/authorization/AuthorizationLeasePolicy.kt`
- `core/src/main/java/com/taha/kairos/core/authorization/AuthorizationModels.kt`
- `app/src/test/java/com/taha/kairos/authorization/AuthorizationGateViewModelTest.kt`
