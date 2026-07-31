# AuthorizationClock

> **In plain words** — reads three different notions of "now" at the same moment: the ordinary wall clock (which a user can change), time since the phone last booted (which they cannot), and how many times the phone has rebooted. The authorization rules need all three to tell genuine elapsed time from a tampered clock. Taking them as one snapshot matters — reading them separately could yield an inconsistent combination.

## Purpose

Supplies a coherent three-clock snapshot used to evaluate an authorization lease.

## Responsibilities

- Read wall-clock epoch time.
- Read monotonic elapsed real time.
- Read the Android boot count.
- Return them as one `AuthorizationTime` value.

## Dependencies

- Application `Context`, `System.currentTimeMillis`, `SystemClock.elapsedRealtime`, and `Settings.Global.BOOT_COUNT`.
- Hilt binds `SystemAuthorizationClock` to `AuthorizationClock`.

## Called By

- [[Components/ViewModels/AuthorizationGateViewModel]]

## Calls

- Android system clocks and global settings through a retained `ContentResolver`.

## Important Methods

- `now()` returns `AuthorizationTime(epochTimeMs, elapsedRealtimeMs, bootCount)`.

## Design Patterns

- Injectable clock abstraction for testability.
- Adapter over multiple Android time sources.

## Common Pitfalls

- Missing boot-count access yields `-1`, which [[Components/Utilities/AuthorizationLeasePolicy]] treats as invalid.
- The three values should be treated as one snapshot and not mixed with separately sampled clocks.
- This component does not evaluate policy.

## Related Pages

- [[Components/Utilities/AuthorizationLeasePolicy]]
- [[Components/Repositories/DeviceAuthorizationRepository]]
- [[Architecture/Dependency Injection]]

## Source References

- `app/src/main/java/com/taha/kairos/authorization/AuthorizationClock.kt`
- `app/src/main/java/com/taha/kairos/authorization/AuthorizationModule.kt`

