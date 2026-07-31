# Data Sources

> **In plain words** — the actual physical places bytes end up. Kairos uses several, each for a different reason: the **Room database** for records, two **DataStores** for settings and the authorization lease, **app files** for photos and audio, **Firestore** for the device whitelist, and user-picked **folders** for exports and backups. The table below is the definitive answer to "where does this thing live?". Why each was chosen: [[Learn/Data Storage Choices|Data Storage Choices]].

## Inventory

| Source | Ownership | Used for |
|---|---|---|
| Room `kairos.db` | `:data` | Patients, cases, diagnoses, media metadata, shifts, sessions, junctions |
| Preferences DataStore `kairos_prefs` | `:data` | Theme, consultation day, diagnosis sort, backup configuration/status |
| Preferences DataStore `device_authorization` | `:data` | Cached device lease and fail-closed marker |
| Firestore `authorized_devices/{deviceId}` | `:data` | Server-authoritative device authorization |
| App external files | `:core` manager | Persisted case attachments |
| App cache | `:core`/`:features` | Pending media, share PDFs/ZIPs, restore staging |
| Storage Access Framework tree/document URIs | `:data`/`:features` | Backup export, restore, and file import |

## Access rules

Repositories are the normal boundary. Exceptions are backup/purge workers and `BackupEngine`, which use DAOs or files directly for consistent snapshots and maintenance. The only network-backed source is authorization; clinical data is local-only.

## Related pages

- [[Layers/Local Storage|Local Storage]]
- [[Layers/Networking|Networking]]
- [[Diagrams/Database Relationships|Database Relationships]]

## Source references

- `data/src/main/java/com/taha/kairos/data/db/KairosDatabase.kt`
- `data/src/main/java/com/taha/kairos/data/settings/PreferencesStore.kt`
- `data/src/main/java/com/taha/kairos/data/authorization/FirebaseDeviceAuthorizationRepository.kt`
- `core/src/main/java/com/taha/kairos/core/media/MediaFileManager.kt`
- `data/src/main/java/com/taha/kairos/data/backup/BackupEngine.kt`
