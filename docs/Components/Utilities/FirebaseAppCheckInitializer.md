# FirebaseAppCheckInitializer

> **In plain words** — installs the proof-of-genuineness mechanism before any server check happens. There are **two versions of this file**, one compiled into debug builds and one into release builds, which is how the debug provider and Play Integrity are selected without any runtime `if`. If initialisation fails it logs and continues, because authorization will then fail closed on its own — the safe outcome. See [[Learn/Security And Privacy Basics|Security And Privacy Basics]].

## Purpose

Installs the build-appropriate Firebase App Check provider before Firestore authorization requests begin.

## Responsibilities

- Obtain or initialize `FirebaseApp`.
- Install Debug App Check in debug builds.
- Install Play Integrity App Check in release builds.
- Log initialization failure while leaving authorization fail-closed.

## Dependencies

- Firebase App and App Check SDKs.
- `DebugAppCheckProviderFactory` in `src/debug` and `PlayIntegrityAppCheckProviderFactory` in `src/release`.

## Called By

- `KairosApplication.onCreate`.

## Calls

- `FirebaseApp.getInstance` / `initializeApp`.
- `FirebaseAppCheck.installAppCheckProviderFactory(..., true)`.
- Android `Log.e` on failure.

## Important Methods

- `initialize(context)` performs idempotent best-effort initialization for the active build variant.

## Design Patterns

- Source-set-specific implementation behind one symbol.
- Fail-closed security initialization with recovery export preserved.

## Common Pitfalls

- Debug and release files define the same object and must remain in mutually exclusive source sets.
- A debug token must be registered as described by Firebase setup documentation.
- Initialization failure is logged, not thrown; subsequent authorization fails safely.

## Related Pages

- [[Components/APIs/Firebase Authorization API]]
- [[Features/Device Authorization]]
- [[Execution Flows/App Startup]]

## Source References

- `app/src/debug/java/com/taha/kairos/FirebaseAppCheckInitializer.kt`
- `app/src/release/java/com/taha/kairos/FirebaseAppCheckInitializer.kt`
- `app/src/main/java/com/taha/kairos/KairosApplication.kt`
- `FIREBASE_DEVICE_AUTH_SETUP.md`

