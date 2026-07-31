# Databases Index

> **In plain words** — three separate stores, kept apart on purpose. The **Room database** (`kairos.db`) holds the clinical records. The **settings DataStore** holds preferences like theme and backup schedule. The **authorization DataStore** holds the device lease, deliberately separate so that clearing or restoring records never touches security state. See [[Learn/Data Storage Choices|Data Storage Choices]].

Kairos uses Room for clinical records and two independent Preferences DataStores for settings and authorization leases.

- [[Components/Databases/KairosDatabase]]
- [[Components/Databases/PreferencesStore]]
- [[Components/Databases/Device Authorization DataStore]]

## Source References

- `data/src/main/java/com/taha/kairos/data/db/`
- `data/src/main/java/com/taha/kairos/data/settings/PreferencesStore.kt`
- `data/src/main/java/com/taha/kairos/data/authorization/FirebaseDeviceAuthorizationRepository.kt`

