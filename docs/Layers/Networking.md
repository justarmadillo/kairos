# Networking

> **In plain words** — the shortest layer in the wiki, because Kairos barely uses the internet. There is no server holding patient data and no sync. The app makes exactly **one** kind of remote request: reading one Firestore document to ask whether this device is authorized, with a 12-second timeout, and access is granted only if the document exists and says `authorized = true`. Everything else works offline. See [[Learn/Security And Privacy Basics|Security And Privacy Basics]].

## Scope

Kairos has no Retrofit/OkHttp client and no patient/case synchronization. Its only remote operation is a Firebase Firestore server read used by [[Features/Device Authorization|Device Authorization]].

## Request path

`FirebaseDeviceAuthorizationRepository.verifyWithServer()` reads `authorized_devices/{deviceId}` with `Source.SERVER`, a 12-second timeout, and Firebase App Check. A document grants access only when it exists and `authorized == true`; failures are classified by whether offline grace may remain usable.

Debug builds install the App Check debug provider; release builds install Play Integrity. Firestore rules permit only document `get` in the authorization collection and deny all writes/list operations.

## Offline behavior

The foreground gate evaluates the cached lease before deciding whether a server request is needed. A fresh lease is local-only; a grace lease may remain open while refreshing. Invalid, expired, or absent cache fails closed without validated internet. See [[Execution Flows/API Request Lifecycle|API Request Lifecycle]].

## Related pages

- [[Components/APIs/Firebase Authorization API|Firebase Authorization API]]
- [[Components/Repositories/DeviceAuthorizationRepository|Device Authorization Repository]]
- [[Diagrams/API Flow|API Flow]]

## Source references

- `data/src/main/java/com/taha/kairos/data/authorization/FirebaseDeviceAuthorizationRepository.kt`
- `app/src/debug/java/com/taha/kairos/FirebaseAppCheckInitializer.kt`
- `app/src/release/java/com/taha/kairos/FirebaseAppCheckInitializer.kt`
- `firestore.rules`
- `gradle/libs.versions.toml`
