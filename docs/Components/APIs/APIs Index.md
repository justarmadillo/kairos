# APIs Index

> **In plain words** — an *API* here means a call to something outside the app. Kairos has exactly one: reading a single Firestore document to check device authorization. There is no HTTP client library in the project at all, which is itself the clearest statement of the design — no clinical data ever travels. See [[Layers/Networking|Networking]].

Kairos has one remote API interaction: a direct Firebase Firestore document read for device authorization. The project contains no Retrofit, OkHttp, REST service interface, or clinical-data synchronization client.

- [[Components/APIs/Firebase Authorization API]]
- [[Components/Repositories/DeviceAuthorizationRepository]] — application-facing abstraction.
- [[Layers/Networking]] — networking boundary overview.

## Source References

- `data/src/main/java/com/taha/kairos/data/authorization/FirebaseDeviceAuthorizationRepository.kt`
- `data/build.gradle.kts`
- `gradle/libs.versions.toml`

