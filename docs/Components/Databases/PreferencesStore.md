# PreferencesStore

> **In plain words** — where user settings are kept. Not a database of records, just named values: theme, consultation weekday, diagnosis sort, backup folder, backup schedule, and the last backup's time and outcome. Every read supplies a default, so a setting never written yet simply behaves sensibly, and an unrecognised stored value falls back instead of crashing — that tolerance is what makes it safe to add or rename options later. See [[Learn/Data Storage Choices|Data Storage Choices]].

## Purpose

Maps the `kairos_prefs` Preferences DataStore to the typed `AppSettings` model.

## Responsibilities

- Read seven setting keys into one reactive model.
- Supply safe defaults and tolerate unknown enum strings.
- Edit individual settings and backup-run metadata.

## Dependencies

- AndroidX DataStore Preferences and application `Context`.
- `AppSettings`, `ThemeMode`, `DiagnosisSortMode`, `BackupSchedule`, and `DayOfWeek`.

## Called By

- [[Components/Repositories/SettingsRepository]]
- [[Components/Services/BackupEngine]] reads the physical file during export and restore.

## Calls

- `Context.dataStore.data.map` and `DataStore.edit`.

## Important Methods

- `settings` maps stored values and defaults consultation day to Thursday, theme to system, diagnosis sort to alphabetical, and backup schedule to off.
- Setter methods write their corresponding key.
- `setBackupFolderUri(null)` removes the key.
- `recordBackupRun(timestampMs, success)` edits both metadata values together.

## Design Patterns

- Typed adapter over key-value persistence.
- Single-source reactive configuration and atomic preference edits.

## Common Pitfalls

- The physical filename is `datastore/kairos_prefs.preferences_pb`; [[Components/Services/BackupEngine]] relies on it.
- Persistable access to a selected backup folder is granted outside this class.
- DataStore I/O failures propagate through its Flow or suspend setters.

## Related Pages

- [[Components/Repositories/SettingsRepository]]
- [[Architecture/Configuration]]
- [[Components/Workers/ScheduledBackupWorker]]

## Source References

- `data/src/main/java/com/taha/kairos/data/settings/PreferencesStore.kt`
- `core/src/main/java/com/taha/kairos/core/model/AppSettings.kt`

