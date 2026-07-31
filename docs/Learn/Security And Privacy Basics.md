# Security And Privacy Basics

Kairos holds patient data. This page explains, for a beginner, what protects it and why each mechanism exists.

## The threat model

What could actually go wrong:

1. The phone is lost, stolen, sold, or handed to a colleague who should no longer have access.
2. The app is copied and installed on a device that was never meant to run it.
3. The phone breaks and takes the only copy of the records with it.
4. Data leaks outward — to a cloud sync, an automatic OS backup, or another app on the device.
5. Two processes write at once and corrupt the database.

Kairos has a specific answer to each, and every answer is visible in the code.

## 1 & 2 — Device authorization

There is no username and no password. Instead the app asks a server: *is this specific device allowed?*

Each install derives a **device ID**, and a Firestore document `authorized_devices/{deviceId}` decides. Access is granted only when the document exists and its `authorized` field is exactly `true`. Anything else — missing document, `false`, malformed — is a refusal.

Why this design fits the use case: a password can be shared, phished, or written on a whiteboard. A device whitelist means the administrator can revoke a lost phone remotely and instantly, and there is no credential for anyone to steal.

### The lease, and why it is clever

Requiring a live server check on every launch would make the app useless offline — unacceptable in a hospital basement. So a successful check writes a **lease**:

```kotlin
const val NORMAL_LEASE_DURATION_MS = 24 hours
const val OFFLINE_GRACE_DURATION_MS = 24 hours
const val MAX_OFFLINE_DURATION_MS   = 48 hours   // normal + grace
const val CLOCK_ROLLBACK_TOLERANCE_MS = 5 minutes
```

- **Fresh** (under 24h) — open the app immediately, no network needed.
- **Grace** (24–48h) — still open, but try to refresh when a validated connection appears.
- **Expired** (over 48h) — a successful server check is required.

Now the attack this invites: *if the app trusts a stored timestamp, a user can simply set the phone's clock back a year and keep an expired lease alive forever.* Kairos closes that hole with three independent checks in `AuthorizationLeasePolicy`:

**Monotonic clock.** Age is measured with `elapsedRealtime` — milliseconds since boot, which the user cannot edit — not with the wall clock.

**Boot count.** `elapsedRealtime` resets to zero on reboot, so it is only comparable within one boot. The lease records the boot count it was created in; a different boot count invalidates the offline lease and forces a fresh server check.

**Rollback detection.** The lease also stores the latest wall-clock time ever observed. If the current wall clock is more than five minutes *behind* that, the lease is rejected as tampered.

Everything else is defensive: negative timestamps, blank device IDs, or an impossible ordering are all `INVALID`. Notice what `AuthorizationLeasePolicy` is — a pure function with no I/O, taking a lease, a device ID, and a time snapshot, returning a verdict. That is precisely why it can be unit-tested exhaustively without a phone.

### Fail closed

The governing principle: **when in doubt, deny.** Storage error, clock error, unexpected exception — all lock the app. A security check that fails open is not a security check.

Two details reinforce it: a durable `requires_server_check` marker survives restarts, so clearing a lease cannot be undone by relaunching; and a sticky in-memory hard failure prevents a denied session from silently reopening.

### Play Integrity / App Check

Firebase **App Check** attests that the request really came from a genuine, unmodified Kairos install rather than someone poking the Firestore API with a script. Release builds use **Play Integrity**; debug builds use a debug provider. This is why the `:app` build file has `debugImplementation` and `releaseImplementation` lines for different App Check providers. Firestore rules can then reject unattested reads.

### Locked but not hostile

A locked app still lets the user pick a folder and export a full backup. Losing access to the software must never mean losing access to the patient records. That is an ethical design decision expressed in code.

## 3 — Backups

The phone holds the only copy, so backup is a first-class feature, not a nicety: a verified backup format covering database *and* media, a scheduled worker, generational retention (keeping several older backups rather than overwriting one), a health warning when backups have not run, and an export path that works while locked. See [[Features/Settings and Backup|Settings and Backup]].

## 4 — Keeping data in

```xml
android:allowBackup="false"
```

That single manifest line disables Android's automatic cloud backup, so the clinical database is never copied to Google's servers by the OS. Kairos owns its backups instead. Reinforcing choices:

- **App-specific storage** for all media — other apps cannot read it.
- **FileProvider** for sharing — a shared PDF is exposed as a temporary permissioned URI rather than a raw file path.
- **No clinical data in Firestore.** The network is used only for the authorization check.
- **Widget destination whitelist.** `MainActivity` is exported, so it only accepts a fixed set of destinations from an incoming intent instead of navigating wherever an intent extra says.

## 5 — Write safety

`DataSafetyCoordinator.withDataLock { }` serialises case writes against backup runs, so a backup can never capture a half-written case. Inside that, Room transactions make each save atomic. See [[Components/Repositories/DataSafetyCoordinator|DataSafetyCoordinator]].

## Release signing

Every Android app is cryptographically signed. The signature proves updates come from the same author; Android refuses to install an update signed by a different key. Kairos keeps its key credentials in `keystore.properties`, deliberately **outside version control**, and the build fails if they are missing or the key file is absent. Losing that key means never being able to update the app again.

## What is not protected

Honest limits, worth knowing:

- The database file itself is not encrypted at rest beyond Android's own full-disk encryption. A rooted or unlocked device with physical access is outside the model.
- There is no per-user login, so anyone who can unlock the phone and open the app sees the data. Device lock screen is the first line of defence.
- Device authorization controls *which devices*, not *which people*.

## Related pages

- [[Features/Device Authorization|Device Authorization]]
- [[Components/Utilities/AuthorizationLeasePolicy|AuthorizationLeasePolicy]]
- [[Components/Utilities/CachedAuthorizationGuard|CachedAuthorizationGuard]]
- [[Execution Flows/Login Flow|Login Flow]]
- [[Features/Settings and Backup|Settings and Backup]]
