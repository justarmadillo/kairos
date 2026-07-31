# SettingsRepository

> **In plain words** — the doorway to user preferences. Callers deal in a typed `AppSettings` object; the storage keys and the string-to-enum conversion stay hidden here. Because settings are exposed as a live stream, saving a new theme or backup schedule updates the whole app by itself — the screen re-themes and the background job re-registers with nobody calling them. See [[Learn/Data Storage Choices|Data Storage Choices]].

## Purpose

Exposes typed application settings while hiding DataStore preference keys and serialization details.

## Responsibilities

- Observe `AppSettings` as a Flow.
- Update consultation day, theme, diagnosis sort, backup folder, and backup schedule.
- Record the last backup timestamp and success flag.

## Dependencies

- [[Components/Databases/PreferencesStore]].
- `AppSettings`, `ThemeMode`, `DiagnosisSortMode`, `BackupSchedule`, and `DayOfWeek`.

## Called By

- [[Components/ViewModels/SettingsViewModel]]
- [[Components/ViewModels/DashboardViewModel]]
- [[Components/ViewModels/ConsultationViewModel]]
- `MainActivity` and `KairosApplication`
- [[Components/Workers/ScheduledBackupWorker]]

## Calls

- The corresponding `PreferencesStore` Flow and setter for every operation.

## Important Methods

- `observeSettings()` returns the typed DataStore mapping.
- `setBackupFolderUri` can remove the stored URI with `null`.
- `setBackupSchedule` drives WorkManager rescheduling through `KairosApplication`'s collector.
- `recordBackupRun` updates timestamp and success together in one DataStore edit.

## Design Patterns

- Repository facade over a key-value data source.
- Reactive configuration as a single typed model.
- Dependency inversion between app/features and DataStore.

## Common Pitfalls

- Setting a folder URI does not itself persist Android URI permission; the picker caller must do that.
- A 30-day `MONTHLY` interval is not calendar-month scheduling.
- Invalid stored enum names silently fall back to defaults.

## Related Pages

- [[Components/Databases/PreferencesStore]]
- [[Architecture/Configuration]]
- [[Components/Managers/WorkerScheduler]]

## Source References

- `core/src/main/java/com/taha/kairos/core/repository/SettingsRepository.kt`
- `data/src/main/java/com/taha/kairos/data/repository/SettingsRepositoryImpl.kt`
- `data/src/main/java/com/taha/kairos/data/di/DataModule.kt`

