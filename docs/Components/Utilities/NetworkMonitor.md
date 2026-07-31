# NetworkMonitor

> **In plain words** — answers "is there real internet right now?". The word *validated* is the point: Android distinguishes being connected to a network from that network actually reaching the internet. A hotel Wi-Fi login page is connected but useless, and attempting a server check there would waste time and produce a misleading failure.

## Purpose

Exposes whether Android currently reports validated internet access for authorization refresh decisions.

## Responsibilities

- Publish online state as a `StateFlow<Boolean>`.
- Register a default-network callback.
- Treat captive portals and unvalidated connectivity as offline.

## Dependencies

- Android `ConnectivityManager`, `NetworkCapabilities`, application `Context`, and Kotlin StateFlow.
- Hilt binds `ConnectivityNetworkMonitor` to `NetworkMonitor`.

## Called By

- [[Components/ViewModels/AuthorizationGateViewModel]]

## Calls

- `registerDefaultNetworkCallback`, active-network capability lookup, and `NET_CAPABILITY_INTERNET`/`VALIDATED` checks.

## Important Methods

- `isOnline` is the read-only state stream.
- `hasValidatedInternet` requires both internet and validated capabilities.
- Network callback methods recompute state on availability, capability changes, and loss.

## Design Patterns

- Observable platform adapter and dependency-inverted network status service.

## Common Pitfalls

- Online means validated connectivity, not that Firestore authorization will succeed.
- The singleton callback is never unregistered, which is appropriate for application lifetime.
- This is not a general transport or API client.

## Related Pages

- [[Components/APIs/Firebase Authorization API]]
- [[Components/Repositories/DeviceAuthorizationRepository]]
- [[Layers/Networking]]

## Source References

- `app/src/main/java/com/taha/kairos/authorization/NetworkMonitor.kt`
- `app/src/main/java/com/taha/kairos/authorization/AuthorizationModule.kt`

