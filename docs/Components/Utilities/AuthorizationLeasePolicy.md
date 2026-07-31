# AuthorizationLeasePolicy

> **In plain words** — the rules of offline access, written as a **pure function**: give it a stored lease, a device ID, and a time snapshot, and it returns a verdict — none, fresh, grace, expired, or invalid. It reads nothing, writes nothing, and asks nothing; every input is a parameter. That is exactly why it can be tested exhaustively without a phone, and it is the single best file in the project for a beginner to study. The rules it encodes (monotonic clock, boot count, rollback tolerance) are explained in [[Learn/Security And Privacy Basics|Security And Privacy Basics]].

## Purpose

Evaluates a cached device authorization lease without I/O and classifies it as absent, fresh, grace, expired, or invalid.

## Responsibilities

- Use monotonic elapsed time to measure lease age on the same boot.
- Reject device mismatch, reboot, malformed data, elapsed-time reversal, and material wall-clock rollback.
- Calculate time remaining until the next authorization boundary.

## Dependencies

- `AuthorizationLease`, `AuthorizationTime`, `CachedAuthorization`, and `CachedAuthorizationStatus`.

## Called By

- [[Components/ViewModels/AuthorizationGateViewModel]]
- [[Components/Utilities/CachedAuthorizationGuard]]

## Calls

- Pure structural checks and arithmetic; no Android or network API.

## Important Methods

- `evaluate(lease, currentDeviceId, now)` applies the complete policy.
- `NORMAL_LEASE_DURATION_MS` is 24 hours.
- `OFFLINE_GRACE_DURATION_MS` adds another 24 hours; hard expiry is 48 hours.
- `CLOCK_ROLLBACK_TOLERANCE_MS` is 5 minutes.

## Design Patterns

- Pure policy object and explicit state classification.
- Monotonic-clock security with wall-clock rollback detection.

## Common Pitfalls

- Any boot-count change invalidates offline access even when wall time looks valid.
- Exactly 24 hours starts grace; exactly 48 hours is expired.
- A lease for another device is `NONE`, while malformed matching data is `INVALID`.
- The policy does not contact the server or persist observations.

## Related Pages

- [[Features/Device Authorization]]
- [[Components/Repositories/DeviceAuthorizationRepository]]
- [[Components/Utilities/AuthorizationClock]]

## Source References

- `core/src/main/java/com/taha/kairos/core/authorization/AuthorizationLeasePolicy.kt`
- `core/src/main/java/com/taha/kairos/core/authorization/AuthorizationModels.kt`
- `core/src/test/java/com/taha/kairos/core/authorization/AuthorizationLeasePolicyTest.kt`

