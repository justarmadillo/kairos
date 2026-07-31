# MediaFileManager

> **In plain words** — the single authority on where attachment files live. It converts between the **relative** paths stored in the database and the **absolute** paths needed to actually open a file, and it creates the temporary permissioned links used when sharing with another app. Centralising this is what makes backups portable: restore onto a new phone, where the absolute location differs, and every relative path still resolves. See [[Learn/Data Storage Choices|Data Storage Choices]].

## Purpose

Owns attachment file locations, relative-path conversion, deletion, and FileProvider URI creation.

## Responsibilities

- Create and expose the persistent media root.
- Create final case files or pending cache files with safe extensions.
- Convert between database-relative and filesystem-absolute paths.
- Delete individual files or entire case directories.
- Produce `content://` URIs for camera and sharing flows.

## Dependencies

- Application `Context`, Android external files/cache directories, `FileProvider`, and `MediaType`.

## Called By

- [[Components/ViewModels/PatientCaseViewModel]] and patient media UI.
- [[Components/Repositories/CaseRepository]] and [[Components/Repositories/MediaRepository]].
- [[Components/Services/BackupEngine]], [[Components/Workers/TrashPurgeWorker]], [[Components/Services/CasePdfExporter]], and [[Components/Services/CaseZipExporter]].

## Calls

- `getExternalFilesDir(Environment.DIRECTORY_PICTURES)`, `filesDir`, and `cacheDir`.
- Java `File` operations and `FileProvider.getUriForFile`.

## Important Methods

- `newCaseMediaFile(caseId, type, originalExtension)` uses `cases/{id}` for positive IDs and `kairos_pending_media` cache otherwise.
- `resolve(relativePath)` joins a persisted path to the media root.
- `toRelative(file)` prepares a database path.
- `delete`, `deleteCaseDir`, `contentUriFor`, and `rootDir` expose controlled operations.

## Design Patterns

- Singleton filesystem gateway and path-policy encapsulation.
- Database stores portable relative paths while presentation receives absolute paths.

## Common Pitfalls

- `toRelative` logs and falls back to only the basename for a file outside the media root; that fallback may not later resolve to the original file.
- The caller must copy or write bytes into a newly reserved file.
- Pending cache files are not included in backup.
- FileProvider authority must remain `${applicationId}.fileprovider`.

## Related Pages

- [[Layers/Local Storage]]
- [[Components/Repositories/MediaRepository]]
- [[Components/DAOs/CaseMediaDao]]

## Source References

- `core/src/main/java/com/taha/kairos/core/media/MediaFileManager.kt`
- `app/src/main/res/xml/file_paths.xml`
- `app/src/main/AndroidManifest.xml`

