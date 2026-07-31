# SettingsViewModel

> **In plain words** — the brain behind the settings screen. Changing a value writes it straight through to storage; the screen then updates because it is subscribed to that storage, not because the ViewModel told it to. It also drives the three heavy commands — backup now, restore, and compact the database — each of which returns a result to display rather than throwing.

## Purpose

Expose persisted application settings and coordinate manual backup, restore, and database optimization commands.

## Responsibilities

- Observe `AppSettings`.
- Persist consultation weekday, theme, diagnosis sort, backup folder, and backup schedule.
- Run manual export and record its result.
- Run destructive restore and signal completion.
- Run database vacuum and expose progress/messages.

## Dependencies

- [[Components/Repositories/SettingsRepository|SettingsRepository]]
- [[Components/Repositories/BackupRepository|BackupRepository]]

## Called By

`SettingsScreen` collects `settings` and `backupUi`, binds dropdown and button actions, displays snackbar messages, and prompts for restart after restore.

## Calls

- Settings setters plus `observeSettings()` and `recordBackupRun(timestamp, success)`.
- `BackupRepository.export(folderUri)`, `restore(zipUri)`, and `vacuumDatabase()`.

## Important Methods

- `setConsultationDay`, `setTheme`, `setDiagnosisSort`, `setBackupFolder`, and `setBackupSchedule`.
- `exportNow()` — validates folder, runs export, records result, and publishes a message.
- `restoreBackup(zipUri)` — runs restore and sets `restoreCompleted` on success.
- `vacuumDatabase()` — catches exceptions and reports completion/failure.
- `clearBackupMessage()` / `clearRestoreCompleted()`.

## Design Patterns

- `@HiltViewModel` with repository contracts.
- Persistent settings Flow separated from ephemeral command state.
- Immutable `BackupUiState` updates and one-shot state consumption by screen effects.
- Repository result objects for backup/restore; exception mapping for vacuum.

## Common Pitfalls

- Setter and export/restore calls do not catch thrown repository exceptions.
- Public methods are not mutex-protected; UI state normally prevents overlapping operations, but direct duplicate calls can overlap.
- Failure text interpolates `result.error`, which can produce `null`.
- Successful restore requires a real process/app restart, but the ViewModel only raises a prompt flag.
- Persisting diagnosis sort does not currently affect `DiagnosisBrowseViewModel`.

## Related Pages

- [[Features/Settings and Backup|Settings and Backup]]
- [[Features/Dashboard|Dashboard]]
- [[Features/Trash and Retention|Trash and Retention]]
- [[Architecture/Configuration]]
- [[Architecture/Background Work]]

## Source references

- `features/src/main/java/com/taha/kairos/features/settings/SettingsViewModel.kt`
- `features/src/main/java/com/taha/kairos/features/settings/SettingsScreen.kt`
- `core/src/main/java/com/taha/kairos/core/repository/SettingsRepository.kt`
- `core/src/main/java/com/taha/kairos/core/repository/BackupRepository.kt`
- `core/src/main/java/com/taha/kairos/core/model/AppSettings.kt`
