# DeviceAuthorizationRepository

> **In plain words** — the doorway to "may this phone run the app?". It owns the device ID, stores and clears the offline lease, and performs the server check. Two deliberate details: the Firestore read is forced to come **from the server**, never from Firestore's own local cache (a cached "yes" would defeat the whole mechanism), and a denial writes a durable marker so relaunching the app cannot restore stale access. See [[Learn/Security And Privacy Basics|Security And Privacy Basics]].

## Purpose

Defines local lease persistence and forced-server verification for per-device access control; `FirebaseDeviceAuthorizationRepository` is the bound implementation.

## Responsibilities

- Derive and expose the installation's stable display device ID.
- Load, save, clear, and advance the local authorization lease.
- Persist a fail-closed marker after denial or unsafe failure.
- Verify the current device against Firestore without using its local cache.

## Dependencies

- Authorization models in `AuthorizationModels.kt`.
- [[Components/Databases/Device Authorization DataStore]].
- [[Components/APIs/Firebase Authorization API]].
- Android secure settings and fallback SharedPreferences for identity input.

## Called By

- [[Components/ViewModels/AuthorizationGateViewModel]]
- [[Components/Utilities/CachedAuthorizationGuard]]

## Calls

- Device authorization DataStore reads and edits.
- `FirebaseFirestore` server-source document lookup.
- Android ID or an installation UUID fallback, then SHA-256 formatting.

## Important Methods

- `loadLease()` rejects mismatched devices and the durable server-check marker.
- `saveAuthorized(at)` replaces the trusted lease baseline.
- `clearLease()` clears timestamps and writes `requires_server_check = true`.
- `recordObservation(at)` advances the greatest observed wall-clock time.
- `verifyWithServer()` maps authorized, denied, timeout, and Firestore errors into `RemoteAuthorizationResult`.

## Design Patterns

- Repository boundary combining a local cache with a remote authority.
- Fail-closed security policy and explicit offline-grace eligibility.
- Forced-source remote read to avoid authorizing from Firestore cache.

## Common Pitfalls

- A restored lease from another device cannot authorize the current installation.
- Firestore permission/configuration failures are intentionally not treated like ordinary offline failures.
- Clearing the lease must retain the server-check marker across process death.
- The repository does not evaluate lease age; [[Components/Utilities/AuthorizationLeasePolicy]] does.

## Related Pages

- [[Features/Device Authorization]]
- [[Components/Utilities/AuthorizationLeasePolicy]]
- [[Components/Utilities/NetworkMonitor]]

## Source References

- `core/src/main/java/com/taha/kairos/core/authorization/AuthorizationModels.kt`
- `data/src/main/java/com/taha/kairos/data/authorization/FirebaseDeviceAuthorizationRepository.kt`
- `data/src/main/java/com/taha/kairos/data/di/DataModule.kt`
