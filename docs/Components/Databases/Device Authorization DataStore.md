# Device Authorization DataStore

> **In plain words** — a small private store holding proof that this device was approved: which device, when (by two different clocks), during which boot, and the latest time ever seen. Those extra fields exist to defeat clock tampering. It is kept separate from user settings so that restoring a backup can never carry someone else's approval onto this phone — and a lease whose device ID does not match is rejected outright. See [[Learn/Security And Privacy Basics|Security And Privacy Basics]].

## Purpose

Stores the last server-confirmed device authorization lease and a durable fail-closed marker in the private `device_authorization` Preferences DataStore.

## Responsibilities

- Persist the lease's device ID, wall time, elapsed real time, boot count, and greatest observed wall time.
- Reject data restored from a different device.
- Require a new server check after denial or unsafe failure.

## Dependencies

- AndroidX DataStore Preferences.
- `AuthorizationLease` and `AuthorizationTime`.
- [[Components/Repositories/DeviceAuthorizationRepository]].

## Called By

- `FirebaseDeviceAuthorizationRepository` exclusively.

## Calls

- `deviceAuthorizationDataStore.data.first()` for one-shot reads.
- `DataStore.edit`, `Preferences.clear`, key writes, and key removal.

## Important Methods

- The enclosing repository's `loadLease` reconstructs a lease only when `device_id` matches and `requires_server_check` is absent.
- `saveAuthorized` clears stale cross-device data and removes the fail-closed marker.
- `clearLease` writes `requires_server_check = true` after clearing timestamps.
- `recordObservation` monotonically advances `latest_observed_epoch_ms`.

## Design Patterns

- Private security cache with explicit invalidation marker.
- Monotonic trusted-observation update and device binding.

## Common Pitfalls

- This DataStore is intentionally not part of application backup; a restore must not transfer authorization.
- Removing only timestamps is insufficient—`requires_server_check` preserves denial across process death.
- Lease validity and duration are evaluated by [[Components/Utilities/AuthorizationLeasePolicy]], not by DataStore.

## Related Pages

- [[Components/Repositories/DeviceAuthorizationRepository]]
- [[Components/Utilities/CachedAuthorizationGuard]]
- [[Features/Device Authorization]]

## Source References

- `data/src/main/java/com/taha/kairos/data/authorization/FirebaseDeviceAuthorizationRepository.kt`
- `core/src/main/java/com/taha/kairos/core/authorization/AuthorizationModels.kt`
