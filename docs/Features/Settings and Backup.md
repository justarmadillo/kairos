# Settings and Backup

> **In plain words** — the most important feature in the app, because the phone holds the only copy of the data. A backup bundles the database *and* the media files into one archive in a folder the user picks; it can run on demand or on a schedule, older backups are pruned generationally (keep the recent few, plus the newest of each older month), and the dashboard complains if backups have stopped happening. **Restore is destructive** — it replaces current data — which is why it requires explicit confirmation and an app restart. Changing the schedule here re-registers the background job automatically, because the app watches the setting. See [[Learn/Security And Privacy Basics|Security And Privacy Basics]] and [[Architecture/Background Work|Background Work]].

## Purpose

Configure consultation, appearance, diagnosis ordering, backup location/schedule, manual backup/restore, and database optimization.

## User Flow

The user chooses dropdown values, selects a persistent Storage Access Framework backup folder, exports immediately, selects and confirms a destructive restore, optimizes the database, or opens [[Features/Trash and Retention|Trash and Retention]]. A successful restore prompts for an app restart.

## Execution Flow

`SettingsViewModel.settings` observes `AppSettings`; setters write through `SettingsRepository`. Manual export calls `BackupRepository.export` and records the run result. Restore delegates to `BackupRepository.restore`. Vacuum delegates to `vacuumDatabase`. `KairosApplication` separately observes the saved backup schedule and updates WorkManager.

## Important Classes

- `SettingsScreen`, `DropdownSetting`, and `SettingsViewModel`.
- `AppSettings`, `BackupUiState`, and `PreferencesStore`.
- `BackupEngine`, `WorkerScheduler`, and `ScheduledBackupWorker`.

## Related ViewModels

- [[Components/ViewModels/SettingsViewModel|SettingsViewModel]]
- [[Components/ViewModels/DashboardViewModel|DashboardViewModel]]

## Related Repositories

- [[Components/Repositories/SettingsRepository|SettingsRepository]]
- [[Components/Repositories/BackupRepository|BackupRepository]]

## API Calls

- Settings calls: `observeSettings`, consultation/theme/diagnosis-sort/folder/schedule setters, and `recordBackupRun`.
- Backup calls: `export(folderUri)`, `restore(zipUri)`, and `vacuumDatabase()`.
- Android Storage Access Framework: `OpenDocumentTree`, `OpenDocument`, and persisted tree URI permission.
- WorkManager scheduling is performed outside this screen. No HTTP API is involved.

## State Flow

`settings` is a five-second `WhileSubscribed` `StateFlow`. `backupUi` is a separate hot state for export/restore/vacuum progress, snackbar message, and restore completion. Screen effects consume messages and show the restart prompt.

## Navigation

- Top-level route: `settings`.
- Trash row: `trash`.
- Folder and restore selection leave the app temporarily through Android document providers but do not change the navigation route.

## Design Decisions

- Android Auto Backup/device transfer are disabled; consistency-aware app backup is the supported path.
- Restore requires explicit confirmation because it replaces current data, but completion only requests a manual restart.
- Manual export records success/failure timestamp; locked-screen recovery export intentionally does not depend on settings.
- UI prevents the common conflicting operations, but ViewModel methods do not independently serialize duplicate direct calls.
- Persisted diagnosis sort is displayed and writable, but [[Features/Diagnosis Browser|Diagnosis Browser]] currently starts alphabetically and does not consume it.
- Backup error strings can include a nullable engine error value; setter failures are not represented in UI state.

## Related Pages

- [[Features/Dashboard|Dashboard]]
- [[Features/Trash and Retention|Trash and Retention]]
- [[Architecture/Background Work]]
- [[Architecture/Configuration]]
- [[Execution Flows/Background Jobs]]

## Source references

- `features/src/main/java/com/taha/kairos/features/settings/SettingsScreen.kt`
- `features/src/main/java/com/taha/kairos/features/settings/SettingsViewModel.kt`
- `core/src/main/java/com/taha/kairos/core/model/AppSettings.kt`
- `core/src/main/java/com/taha/kairos/core/repository/SettingsRepository.kt`
- `core/src/main/java/com/taha/kairos/core/repository/BackupRepository.kt`
- `data/src/main/java/com/taha/kairos/data/settings/PreferencesStore.kt`
- `data/src/main/java/com/taha/kairos/data/backup/BackupEngine.kt`
- `app/src/main/java/com/taha/kairos/KairosApplication.kt`
