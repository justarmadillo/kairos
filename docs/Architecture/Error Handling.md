# Error Handling

> **In plain words** — what the app does when something goes wrong. There is no single global rule; each boundary handles failure in the way that is safe *for it*. The one to remember is **fail closed**: if authorization cannot be confirmed for any reason — server error, storage error, tampered clock — access is denied rather than granted. A security check that opens on error is not a check. Elsewhere the style is gentler: backup and restore return a result object describing what happened, ViewModels turn errors into a message on screen, and best-effort cleanup deliberately ignores failures rather than breaking a save. See [[Learn/Security And Privacy Basics|Security And Privacy Basics]].

Error handling is local to each boundary; there is no project-wide error type or global exception handler.

## Boundary Policies

| Boundary | Policy |
| --- | --- |
| Device authorization | Fail closed, clear an unsafe lease, require a positive server response |
| Firestore | Classify failures as grace-eligible or non-graceable; preserve coroutine cancellation |
| Backup/restore | Convert failures to `BackupResult`/`RestoreResult` with an error string |
| Feature actions | Commonly catch at the ViewModel and expose UI message state; some simple writes propagate into the launched coroutine |
| Room writes | Let repository/transaction exceptions reach the calling ViewModel |
| Workers | Catch broad exceptions and return `Result.failure()`; no immediate retry |
| Cleanup | Best effort; many file/pruning failures are intentionally swallowed |

Authorization denial and non-graceable failures persist a `requires_server_check` marker, preventing stale access after process death. A valid grace lease survives retryable server unavailability only until its hard 48-hour boundary.

Long-running feature operations generally rethrow `CancellationException` before handling other exceptions. Cross-resource operations use [[Components/Repositories/DataSafetyCoordinator|DataSafetyCoordinator]] so backup/restore cannot overlap protected writes.

## UI Caveats

- `AuthorizationLockedScreen` does not render the stored lock reason/message.
- The authorization export UI replaces detailed result text with generic success/failure labels.
- Worker failures are not logged, so their cause is usually visible only through recorded backup status or WorkManager state.

See [[Architecture/Logging|Logging]], [[Execution Flows/API Request Lifecycle|API Request Lifecycle]], and [[Components/ViewModels/AuthorizationGateViewModel|AuthorizationGateViewModel]].

## Source references

- `app/src/main/java/com/taha/kairos/authorization/AuthorizationGateViewModel.kt`
- `app/src/main/java/com/taha/kairos/authorization/AuthorizationScreens.kt`
- `data/src/main/java/com/taha/kairos/data/authorization/FirebaseDeviceAuthorizationRepository.kt`
- `data/src/main/java/com/taha/kairos/data/backup/BackupEngine.kt`
- `data/src/main/java/com/taha/kairos/data/backup/ScheduledBackupWorker.kt`
- `features/src/main/java/com/taha/kairos/features/patient/PatientCaseViewModel.kt`
