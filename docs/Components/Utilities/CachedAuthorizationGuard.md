# CachedAuthorizationGuard

> **In plain words** — the authorization check used by background jobs. They cannot show a locked screen or ask the user anything, so they call this first and simply do nothing if access is not currently valid. Without it, a revoked device would keep quietly writing backups and purging records in the background.

## Purpose

Provides a local, fail-closed authorization check for background jobs that can read snapshots or mutate user data.

## Responsibilities

- Sample wall, elapsed, and boot clocks.
- Load the current cached lease.
- Allow work only for `FRESH` or `GRACE` policy states.
- Convert any unexpected error into denied access.

## Dependencies

- Application `Context`, [[Components/Repositories/DeviceAuthorizationRepository]], and [[Components/Utilities/AuthorizationLeasePolicy]].

## Called By

- [[Components/Workers/ScheduledBackupWorker]]
- [[Components/Workers/TrashPurgeWorker]]

## Calls

- Android clocks and boot-count setting.
- `DeviceAuthorizationRepository.loadLease` and `deviceId`.
- `AuthorizationLeasePolicy.evaluate`.

## Important Methods

- `hasCachedAccess()` returns `true` only for fresh or grace status and catches all exceptions as `false`.

## Design Patterns

- Fail-closed guard and local-only policy adapter.

## Common Pitfalls

- The guard never contacts Firestore and never renews a lease.
- Locked workers return a successful no-op; callers should not interpret that as completed business work.
- Manual emergency export deliberately bypasses this guard.

## Related Pages

- [[Features/Device Authorization]]
- [[Components/APIs/Firebase Authorization API]]
- [[Architecture/Background Work]]

## Source References

- `data/src/main/java/com/taha/kairos/data/authorization/CachedAuthorizationGuard.kt`
- `core/src/main/java/com/taha/kairos/core/authorization/AuthorizationLeasePolicy.kt`

