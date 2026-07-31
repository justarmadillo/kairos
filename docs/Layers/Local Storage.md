# Local Storage

> **In plain words** — how data is kept safe *on the phone*, which matters more here than in most apps because the phone holds the only copy. Three ideas do the heavy lifting. **Transactions**: a save either completes fully or not at all. **Relative media paths**: the database records where a photo is *relative to* the app's media folder, so a backup restored on a new phone still finds it. **Soft delete**: deleting marks a record hidden and a background job removes it permanently only after 30 days, so mistakes are recoverable. See [[Learn/Databases And Room|Databases And Room]].

## Stores

[[Components/Databases/KairosDatabase|Kairos Database]] is a Room v2 database for clinical records. [[Components/Databases/PreferencesStore|Settings DataStore]] stores user configuration, while [[Components/Databases/Device Authorization DataStore|Authorization DataStore]] stores the device lease independently. Case attachment metadata is in Room; bytes live beneath the app's external-files media root managed by [[Components/Managers/MediaFileManager|Media File Manager]].

## Consistency

- Room foreign keys cascade child/junction rows.
- Repositories use Room transactions for aggregate changes.
- Relative media paths make records portable across app-storage locations.
- [[Components/Repositories/DataSafetyCoordinator|Data Safety Coordinator]] excludes backup, restore, purge, and writes that could otherwise interleave.
- Android Auto Backup and device transfer are disabled; [[Components/Services/BackupEngine|Backup Engine]] creates the supported DB + media + settings snapshot.

## Retention

Clinical records use soft deletion. [[Components/Workers/TrashPurgeWorker|Trash Purge Worker]] hard-deletes eligible records after 30 days and cleans orphaned media and diagnoses.

## Related pages

- [[Execution Flows/Database Operations|Database Operations]]
- [[Features/Trash and Retention|Trash and Retention]]
- [[Diagrams/Database Relationships|Database Relationships]]

## Source references

- `data/src/main/java/com/taha/kairos/data/db/KairosDatabase.kt`
- `data/src/main/java/com/taha/kairos/data/settings/PreferencesStore.kt`
- `data/src/main/java/com/taha/kairos/data/authorization/FirebaseDeviceAuthorizationRepository.kt`
- `core/src/main/java/com/taha/kairos/core/media/MediaFileManager.kt`
- `app/src/main/res/xml/backup_rules.xml`
