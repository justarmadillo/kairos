# Configuration

> **In plain words** — *configuration* is every setting that is not code: things decided at build time (which Android versions, which signing key, which Firebase project) and things decided at run time by the user (theme, backup schedule, consultation weekday). The distinction matters because build-time settings require a new app release to change, while runtime settings live in DataStore and take effect immediately — changing the backup schedule re-registers the background job by itself. See [[Learn/Data Storage Choices|Data Storage Choices]] and [[Learn/Build And Run|Build And Run]].

Kairos configuration is split across build-time Android settings, Firebase deployment files, manifest/resources, and two runtime DataStores.

## Build and Deployment

- SDK levels, application version, release minification, and signing are in `app/build.gradle.kts`.
- Dependency versions are in `gradle/libs.versions.toml`.
- Release signing reads `keystore.properties`; `preReleaseBuild` verifies required fields and the key file.
- The Google Services plugin consumes `app/google-services.json`.
- Firestore rules expose only exact `authorized_devices/{deviceId}` reads and deny client writes/listing and all other paths.
- Debug builds use App Check debug; release builds use Play Integrity. Enforcement itself is configured in Firebase, not in source.

## Android Runtime

- The manifest declares camera, microphone, network, legacy write-storage, and notification permissions.
- Camera and microphone hardware are optional.
- Android auto-backup/device transfer are disabled by XML rules.
- FileProvider exposes managed pictures and cache paths.
- WorkManager's default initializer is removed so Hilt can supply its worker factory.

## User Settings

`kairos_prefs` stores consultation weekday, theme, diagnosis sort, backup folder/schedule, and last backup outcome. Defaults are Thursday, system theme, alphabetical diagnoses, and backup off.

`device_authorization` separately stores the current device lease, clock observations, and durable server-check marker. Ordinary `kairos_prefs` settings are included in app backup/restore; the authorization store is not, so restoring data does not transfer access.

No `BuildConfig` fields, environment-variable runtime switches, or remote feature flags exist. See [[Overview/Build System|Build System]], [[Components/Databases/PreferencesStore|PreferencesStore]], and [[Features/Settings and Backup|Settings and Backup]].

## Source references

- `app/build.gradle.kts`
- `app/src/main/AndroidManifest.xml`
- `app/src/main/res/xml/backup_rules.xml`
- `app/src/main/res/xml/data_extraction_rules.xml`
- `app/src/main/res/xml/file_paths.xml`
- `firestore.rules`
- `data/src/main/java/com/taha/kairos/data/settings/PreferencesStore.kt`
- `data/src/main/java/com/taha/kairos/data/authorization/FirebaseDeviceAuthorizationRepository.kt`
- `data/src/main/java/com/taha/kairos/data/backup/BackupEngine.kt`
