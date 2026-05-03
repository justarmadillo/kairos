# Kairos Developer Documentation

Kairos is an Android medical documentation app for a surgeon. It stores patients,
cases, diagnoses, shifts, consultation sessions, rich notes, media attachments,
backups, dashboard statistics, global search, and PDF exports.

This document is meant as a developer handoff. If AI assistance becomes
unavailable, expensive, or unreliable, this README should help a human developer
understand the codebase quickly and debug it safely.

## Current App Summary

Kairos is a single Android app split into four Gradle modules:

- `:app` - Android application entry point, navigation, bottom bar, app-level theme setup.
- `:core` - Domain models, repository interfaces, shared UI components, theme tokens, media helpers.
- `:data` - Room database, DAOs, repository implementations, DataStore settings, backup/restore, WorkManager jobs.
- `:features` - Compose screens and view models for dashboard, search, shifts, consultation, cases, patient/case entry, and settings.

The app follows this general architecture:

```text
Compose Screen
  -> Hilt ViewModel
    -> core repository interface
      -> data repository implementation
        -> Room DAO / DataStore / file system / WorkManager
```

Most state is reactive:

- Room queries return `Flow`.
- ViewModels expose `StateFlow`.
- Screens collect via `collectAsStateWithLifecycle()`.

## Build And Run

Open the project in Android Studio, then run the `app` configuration.

Command line:

```powershell
.\gradlew.bat assembleDebug
.\gradlew.bat :app:installDebug
```

Useful compile checks:

```powershell
.\gradlew.bat :features:compileDebugKotlin
.\gradlew.bat :data:kspDebugKotlin
.\gradlew.bat assembleDebug
```

The full debug build should be the default verification command after code
changes:

```powershell
.\gradlew.bat assembleDebug
```

## Tech Stack

Versions are centralized in `gradle/libs.versions.toml`.

Important libraries:

- Android Gradle Plugin 8.7.3
- Kotlin 2.1.0
- Jetpack Compose with Compose BOM 2024.12.01
- Material 3
- Navigation Compose
- Lifecycle ViewModel and lifecycle-aware Compose collection
- Room 2.7.0 with KSP
- Hilt 2.54
- WorkManager
- DataStore Preferences
- Coil 3 for image loading
- Media3 ExoPlayer for video/audio playback
- Accompanist permissions
- `compose-rich-editor` for rich text notes
- Android `PdfDocument` for PDF export

## Top-Level Project Files

- `settings.gradle.kts`
  - Includes `:app`, `:core`, `:data`, and `:features`.
- `build.gradle.kts`
  - Declares plugin aliases at root.
- `gradle/libs.versions.toml`
  - Central dependency and plugin versions.
- `app/build.gradle.kts`
  - Android application config and app dependencies.
- `core/build.gradle.kts`
  - Shared model/theme/component/media library.
- `data/build.gradle.kts`
  - Room, DataStore, WorkManager, Hilt, backup code.
- `features/build.gradle.kts`
  - Compose screens, feature view models, Hilt navigation.

## Module Responsibilities

### `:app`

Primary files:

- `app/src/main/java/com/kairos/KairosApplication.kt`
- `app/src/main/java/com/kairos/MainActivity.kt`
- `app/src/main/java/com/kairos/navigation/KairosNavHost.kt`
- `app/src/main/java/com/kairos/navigation/Destinations.kt`
- `app/src/main/java/com/kairos/ui/BottomBar.kt`
- `app/src/main/AndroidManifest.xml`
- `app/src/main/res/xml/file_paths.xml`

Responsibilities:

- Starts Hilt with `@HiltAndroidApp`.
- Provides WorkManager configuration for Hilt workers.
- Observes app settings to apply light/dark/system theme.
- Hosts navigation and bottom tabs.
- Defines the FileProvider used for camera output and PDF sharing.

### `:core`

Primary folders:

- `core/src/main/java/com/kairos/core/model`
- `core/src/main/java/com/kairos/core/repository`
- `core/src/main/java/com/kairos/core/components`
- `core/src/main/java/com/kairos/core/theme`
- `core/src/main/java/com/kairos/core/media`

Responsibilities:

- Domain models such as `Patient`, `Case`, `Diagnosis`, `MediaItem`, `Shift`, and `AppSettings`.
- Repository interfaces. Feature modules depend on interfaces, not data implementations.
- Shared Compose components, including case cards, top bar, empty states, rich notes editor, media UI, phone input, and audio UI.
- Theme colors, typography, shapes, and app-specific extra colors.
- Media file helper and audio recorder engine.

### `:data`

Primary folders:

- `data/src/main/java/com/kairos/data/db`
- `data/src/main/java/com/kairos/data/db/dao`
- `data/src/main/java/com/kairos/data/db/entities`
- `data/src/main/java/com/kairos/data/db/relations`
- `data/src/main/java/com/kairos/data/mapper`
- `data/src/main/java/com/kairos/data/repository`
- `data/src/main/java/com/kairos/data/settings`
- `data/src/main/java/com/kairos/data/backup`
- `data/src/main/java/com/kairos/data/di`

Responsibilities:

- Room database schema and DAOs.
- Mapping database entities to core domain models.
- Repository implementations.
- DataStore preferences.
- Backup export, restore, validation, and database vacuum.
- WorkManager scheduling for automatic backups and trash purge.
- Hilt module bindings.

### `:features`

Primary folders:

- `features/src/main/java/com/kairos/features/dashboard`
- `features/src/main/java/com/kairos/features/search`
- `features/src/main/java/com/kairos/features/shifts`
- `features/src/main/java/com/kairos/features/consultation`
- `features/src/main/java/com/kairos/features/cases`
- `features/src/main/java/com/kairos/features/patient`
- `features/src/main/java/com/kairos/features/settings`

Responsibilities:

- Compose screens and feature ViewModels.
- Patient/case creation and editing.
- Dashboard statistics and gamification.
- Global search UI.
- Case detail, diagnosis browsing, image viewer, PDF export.
- Shifts and consultation workflows.
- Settings, backup, restore, trash.

## App Startup Flow

1. Android launches `KairosApplication`.
2. Hilt initializes dependency graph.
3. `KairosApplication.onCreate()`:
   - Schedules daily trash purge.
   - Observes `SettingsRepository.observeSettings()`.
   - Re-schedules backup worker whenever backup schedule changes.
4. Android launches `MainActivity`.
5. `MainActivity`:
   - Enables edge-to-edge system bars.
   - Observes app settings.
   - Applies `KairosTheme`.
   - Creates `NavController`.
   - Shows `BottomBar` on top-level destinations.
   - Hosts `KairosNavHost`.

## Navigation

Navigation lives in:

- `app/src/main/java/com/kairos/navigation/KairosNavHost.kt`
- `app/src/main/java/com/kairos/navigation/Destinations.kt`
- `app/src/main/java/com/kairos/ui/BottomBar.kt`

Top-level tabs are defined by `TopLevelDestination`:

```text
Dashboard | Shifts | Consultation | Cases | Settings
```

Current start destination:

```kotlin
TopLevelDestination.Dashboard.route
```

Important routes:

- `dashboard`
- `search`
- `shifts`
- `shift_detail/{shiftId}`
- `consultation`
- `cases`
- `case_feed/{diagnosisId}?name={diagnosisName}`
- `case_detail/{caseId}`
- `image_viewer/{caseId}?index={index}`
- `settings`
- `trash`
- `patient_case?shiftId={shiftId}&sessionId={sessionId}&caseId={caseId}`

The `patient_case` route is used for:

- Creating a new patient and case.
- Creating a case for an existing patient.
- Creating a case linked to a shift.
- Creating a case linked to a consultation session.
- Editing an existing case.

Navigation transitions are disabled in `KairosNavHost`:

```kotlin
enterTransition = { EnterTransition.None }
exitTransition = { ExitTransition.None }
popEnterTransition = { EnterTransition.None }
popExitTransition = { ExitTransition.None }
```

This was done to make switching between app views feel faster.

## Theme And Design System

Theme files:

- `core/src/main/java/com/kairos/core/theme/Color.kt`
- `core/src/main/java/com/kairos/core/theme/Type.kt`
- `core/src/main/java/com/kairos/core/theme/Shape.kt`
- `core/src/main/java/com/kairos/core/theme/KairosTheme.kt`

The app uses a cream, charcoal, and coral/orange palette.

Important custom color tokens:

- `LocalKairosExtraColors.current.surfaceCard`
- `LocalKairosExtraColors.current.onSurfaceMuted`
- `LocalKairosExtraColors.current.selectedDark`
- `LocalKairosExtraColors.current.divider`

Material shapes are currently:

- `extraSmall` 4 dp
- `small` 6 dp
- `medium` 8 dp
- `large` 12 dp
- `extraLarge` 16 dp

Some newer dashboard cards use an explicit 24 dp radius as part of the
dashboard design.

Typography uses Inter for most text and Serif for display/headline styles.

## Domain Models

Domain models live in `core/src/main/java/com/kairos/core/model`.

### Patient

File: `core/model/Patient.kt`

Fields:

- `id`
- `name`
- `age`
- `phones`
- `createdAt`
- `updatedAt`
- `deletedAt`

`PatientPhone` fields:

- `id`
- `number`
- `label`

### Case

File: `core/model/Case.kt`

Fields:

- `id`
- `patientId`
- `patient`
- `caseDate`
- `mechanism`
- `notesHtml`
- `diagnoses`
- `media`
- `createdAt`
- `updatedAt`
- `deletedAt`

Notes are stored as HTML generated by the rich text editor.

### Diagnosis

File: `core/model/Diagnosis.kt`

Fields:

- `id`
- `name`
- `caseCount`

### MediaItem

File: `core/model/MediaItem.kt`

Fields:

- `id`
- `caseId`
- `filePath`
- `mediaType`
- `durationMs`
- `isPrimary`
- `createdAt`

`MediaType` can be:

- `IMAGE`
- `VIDEO`
- `AUDIO`

### Shift

File: `core/model/Shift.kt`

Represents an operating shift. Cases can be linked through `shift_cases`.

### ConsultationSession

File: `core/model/ConsultationSession.kt`

Represents a consultation day/session. Cases can be linked through
`consultation_cases`.

### AppSettings

File: `core/model/AppSettings.kt`

Fields:

- `consultationDayOfWeek`
- `themeMode`
- `diagnosisSortMode`
- `backupFolderUri`
- `backupSchedule`
- `backupLastRunAt`
- `backupLastRunSuccess`

Enums:

- `ThemeMode`: `LIGHT`, `DARK`, `SYSTEM`
- `DiagnosisSortMode`: `ALPHABETICAL`, `MOST_USED`, `RECENT`
- `BackupSchedule`: `OFF`, `DAILY`, `WEEKLY`, `MONTHLY`

## Database

Room database:

- File: `data/src/main/java/com/kairos/data/db/KairosDatabase.kt`
- Database name: `kairos.db`
- Current schema version: `1`
- Schema export: enabled
- Exported schema path: `data/schemas/...`

Current entities:

- `PatientEntity`
- `PatientPhoneEntity`
- `CaseEntity`
- `CaseDiagnosisCrossRef`
- `CaseMediaEntity`
- `DiagnosisEntity`
- `ShiftEntity`
- `ShiftCaseCrossRef`
- `ConsultationSessionEntity`
- `ConsultationCaseCrossRef`

No schema migrations are currently active because the database is still version
1. Migration instructions and templates live in:

- `data/src/main/java/com/kairos/data/db/migrations/Migrations.kt`

When changing schema:

1. Update entities.
2. Bump `version` in `KairosDatabase.kt`.
3. Add an explicit migration in `Migrations.kt`.
4. Add it to `ALL_MIGRATIONS`.
5. Rebuild and test upgrade from an existing install.

Do not change entities without a migration once the app is in real use.

## Database Tables

### `patients`

Entity: `PatientEntity`

Purpose: Main patient record.

Important columns:

- `id`
- `name`
- `age`
- `created_at`
- `updated_at`
- `is_deleted`
- `deleted_at`
- `remote_id`
- `sync_state`
- `last_synced_at`

Patients are soft-deleted using `is_deleted` and `deleted_at`.

### `patient_phones`

Entity: `PatientPhoneEntity`

Purpose: Multiple phone numbers per patient.

Important columns:

- `id`
- `patient_id`
- `number`
- `label`

Foreign key:

- `patient_id -> patients.id`

### `cases`

Entity: `CaseEntity`

Purpose: Clinical case record.

Important columns:

- `id`
- `patient_id`
- `case_date`
- `mechanism`
- `notes_html`
- `created_at`
- `updated_at`
- `is_deleted`
- `deleted_at`
- `remote_id`
- `sync_state`
- `last_synced_at`

Foreign key:

- `patient_id -> patients.id`

### `diagnoses`

Entity: `DiagnosisEntity`

Purpose: Unique diagnosis names.

Important columns:

- `id`
- `name`
- `created_at`
- `remote_id`
- `sync_state`

`name` has a unique index.

### `case_diagnoses`

Entity: `CaseDiagnosisCrossRef`

Purpose: Many-to-many relation between cases and diagnoses.

Primary key:

- `case_id`
- `diagnosis_id`

### `case_media`

Entity: `CaseMediaEntity`

Purpose: Media attachments for cases.

Important columns:

- `id`
- `case_id`
- `file_path`
- `media_type`
- `duration_ms`
- `is_primary`
- `created_at`

`file_path` is stored relative to the app media root in the database. Repository
mapping resolves it to an absolute file path before UI uses it.

### `shifts`

Entity: `ShiftEntity`

Purpose: Operating shift/day.

Important columns:

- `id`
- `label`
- `date`
- `created_at`
- `is_deleted`
- `deleted_at`

### `shift_cases`

Entity: `ShiftCaseCrossRef`

Purpose: Many-to-many relation between shifts and cases.

### `consultation_sessions`

Entity: `ConsultationSessionEntity`

Purpose: Consultation day/session record.

Important columns:

- `id`
- `date`
- `created_at`
- `is_deleted`
- `deleted_at`

### `consultation_cases`

Entity: `ConsultationCaseCrossRef`

Purpose: Many-to-many relation between consultation sessions and cases.

## DAOs

DAO files live in `data/src/main/java/com/kairos/data/db/dao`.

### PatientDao

File: `PatientDao.kt`

Important operations:

- Insert/update patients.
- Insert and replace phone numbers.
- Get/observe patient by id with phones.
- Search patients by name.
- Soft-delete and restore patients.
- Observe trashed patients.
- Purge old trashed patients if they have no active cases.
- Observe total non-deleted patients for dashboard.

### CaseDao

File: `CaseDao.kt`

Important operations:

- Insert/update cases.
- Manage diagnosis links.
- Link/unlink cases to shifts.
- Link/unlink cases to consultation sessions.
- Get/observe case by id with patient, diagnoses, and media.
- Observe cases by diagnosis, shift, or consultation session.
- Soft-delete and restore cases.
- Observe trashed cases.
- Hard-delete expired trashed cases.
- Dashboard total case count.
- Dashboard period counts.
- Dashboard recent cases.
- Global search query across patient/case/diagnosis/phones/notes.

Global search returns `SearchCaseRow`, then `SearchRepositoryImpl` performs
multi-token filtering in Kotlin.

### DiagnosisDao

File: `DiagnosisDao.kt`

Important operations:

- Insert diagnosis names.
- Find by case-insensitive name.
- Search by prefix for patient/case form autocomplete.
- Observe all diagnoses with counts using different sort modes.

### CaseMediaDao

File: `CaseMediaDao.kt`

Important operations:

- Add media.
- Delete media.
- Set primary media for a case.
- Remove media for trashed or hard-deleted cases.

### ShiftDao

File: `ShiftDao.kt`

Important operations:

- Insert/update shifts.
- Observe all non-deleted shifts with case counts.
- Observe shift by id.
- Soft-delete and restore shifts.
- Purge old deleted shifts.
- Observe total non-deleted shifts for dashboard.

### ConsultationSessionDao

File: `ConsultationSessionDao.kt`

Important operations:

- Get or create consultation sessions.
- Observe sessions by date.
- Link cases through `consultation_cases`.

## Relations And Mappers

Relation files:

- `data/db/relations/PatientWithPhones.kt`
- `data/db/relations/CaseWithRelations.kt`
- `data/db/relations/Counts.kt`

Mapper files:

- `data/mapper/PatientMapper.kt`
- `data/mapper/CaseMapper.kt`
- `data/mapper/DiagnosisMapper.kt`
- `data/mapper/ShiftMapper.kt`
- `data/mapper/ConsultationMapper.kt`

Important mapper behavior:

- `CaseRepositoryImpl.resolveMediaPaths()` converts database-relative media
  paths to absolute file paths for UI loading.
- `PatientRepositoryImpl.upsert()` replaces all phone rows for a patient on
  update.
- `CaseRepositoryImpl.upsertCase()` clears and recreates diagnosis links on
  each save.

## Repository Layer

Repository interfaces are in `:core`. Implementations are in `:data`.

Interfaces:

- `PatientRepository`
- `CaseRepository`
- `DiagnosisRepository`
- `MediaRepository`
- `ShiftRepository`
- `ConsultationRepository`
- `SettingsRepository`
- `BackupRepository`
- `DashboardRepository`
- `SearchRepository`
- `DataSafetyCoordinator`

Implementations:

- `PatientRepositoryImpl`
- `CaseRepositoryImpl`
- `DiagnosisRepositoryImpl`
- `MediaRepositoryImpl`
- `ShiftRepositoryImpl`
- `ConsultationRepositoryImpl`
- `SettingsRepositoryImpl`
- `BackupEngine`
- `DashboardRepositoryImpl`
- `SearchRepositoryImpl`
- `DataSafetyCoordinatorImpl`

Why this matters:

- Feature code should depend on `core.repository.*` interfaces.
- Data code owns Room, DataStore, file backup, and Android storage details.
- This makes screens easier to reason about and keeps Room out of feature code.

## Hilt Dependency Injection

Hilt setup:

- `KairosApplication` uses `@HiltAndroidApp`.
- ViewModels use `@HiltViewModel`.
- Data repositories use `@Singleton` and `@Inject`.
- Hilt bindings are in `data/src/main/java/com/kairos/data/di/DataModule.kt`.

`DatabaseModule` provides:

- `KairosDatabase`
- DAOs

`RepositoryModule` binds:

- Core repository interfaces to data implementations.

If Hilt fails to compile:

- Check a new repository implementation has a `@Binds` entry.
- Check constructor dependencies can be provided.
- Check the module that needs the class depends on the right Gradle module.

## Media Storage

Media helper:

- `core/src/main/java/com/kairos/core/media/MediaFileManager.kt`

Storage root:

```text
context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)/kairos
```

Case media path pattern:

```text
Pictures/kairos/cases/{caseId}/{timestamp}_{uuid}.{ext}
```

Extensions:

- Images: `.jpg`
- Videos: `.mp4`
- Audio: `.m4a`

Temporary unsaved media is created under:

```text
cases/0/
```

After the case is saved, pending media is copied into the real case folder.

Important:

- Database stores relative paths.
- UI receives absolute paths via repository mapping.
- Backup excludes temporary `cases/0/` media.
- `FileProvider` authority is `${applicationId}.fileprovider`.

FileProvider config:

- Manifest: `app/src/main/AndroidManifest.xml`
- Paths: `app/src/main/res/xml/file_paths.xml`

Provider paths currently include:

- External files pictures under `Pictures/kairos/`
- Cache directory for exports/sharing

## Permissions

Declared in `app/src/main/AndroidManifest.xml`:

- `CAMERA`
- `RECORD_AUDIO`
- `POST_NOTIFICATIONS`

Hardware features are optional:

- Camera
- Microphone

Runtime permission handling:

- Camera and audio permissions are requested from patient/case entry UI.
- Backup notifications depend on notification permission on Android 13+.

## Features

### Dashboard

Files:

- `features/dashboard/DashboardScreen.kt`
- `features/dashboard/DashboardViewModel.kt`
- `core/repository/DashboardRepository.kt`
- `data/repository/DashboardRepositoryImpl.kt`

Purpose:

- Home tab.
- Shows gamified statistics.

Stats:

- Total patients.
- Total cases.
- Total shifts.
- Cases this week vs last week.
- Cases this month vs last month.
- Next milestone progress.
- Recent five cases.

Milestone logic:

- Under 100 cases: target every 10.
- Under 500 cases: target every 50.
- 500+ cases: target every 100.
- When the total exactly hits a milestone after the screen is already observing,
  a short celebration state is shown before advancing to the next milestone.

Dashboard also has the search icon that opens global search.

### Global Search

Files:

- `features/search/SearchScreen.kt`
- `features/search/SearchViewModel.kt`
- `core/repository/SearchRepository.kt`
- `data/repository/SearchRepositoryImpl.kt`
- `CaseDao.observeSearchCases()`

Search fields:

- Patient name.
- Patient age.
- Patient phone numbers.
- Case mechanism.
- Case notes HTML.
- Diagnosis names.

How it works:

1. User types in `SearchScreen`.
2. `SearchViewModel` debounces input by 250 ms.
3. `SearchRepositoryImpl` tokenizes the search.
4. The longest token is used as a Room SQL anchor to avoid loading everything.
5. Kotlin filters the returned rows so all tokens must match somewhere in the
   combined searchable text.
6. Results are limited and shown as cards.
7. Tapping a result navigates to `case_detail/{caseId}`.

Debugging search:

- If no results appear, inspect `CaseDao.observeSearchCases()`.
- If single-word search works but multi-word search does not, inspect token
  filtering in `SearchRepositoryImpl`.
- If notes look odd, remember notes are stored as HTML.
- If phone search fails, check `patient_phones` rows for the patient.

### Add/Edit Patient And Case

Files:

- `features/patient/PatientCaseScreen.kt`
- `features/patient/PatientCaseViewModel.kt`
- `features/patient/NewPatientTab.kt`
- `features/patient/ExistingPatientTab.kt`
- `core/components/PhoneInputRow.kt`
- `core/components/DiagnosisAutocomplete.kt`
- `core/components/RichNotesEditor.kt`
- `core/components/MediaAttachmentSection.kt`

Entry route:

```text
patient_case?shiftId={shiftId}&sessionId={sessionId}&caseId={caseId}
```

Modes:

- No ids: create a new patient and case.
- `shiftId`: create patient/case linked to a shift.
- `sessionId`: create patient/case linked to consultation session.
- Existing patient selected: create a new case for that patient.
- `caseId`: edit an existing case and update its patient demographics.

Important state:

- `PatientCaseUiState`
- `editingCaseId`
- `editingCase`
- `pendingMedia`
- `selectedPatient`

Save behavior:

1. Validate patient name when needed.
2. Upsert patient:
   - New patient inserts patient and phones.
   - Edit case updates selected patient name, age, and phones.
   - Existing patient new-case flow reuses patient id.
3. Upsert case.
4. Recreate diagnosis links.
5. Copy pending media from temp folder to real case folder.
6. Add media rows.

Past bug fixed:

- Editing a case used to switch into the locked "existing patient selected"
  form, so name/age/phones could not be edited.
- Edit mode now uses an editable form and saves patient demographic changes.

### Case Detail

Files:

- `features/cases/CaseDetailScreen.kt`
- `features/cases/CaseDetailViewModel.kt`
- `features/cases/CasePdfExporter.kt`

Shows:

- Patient name.
- Age.
- Phone numbers.
- Case date.
- Diagnoses.
- Mechanism.
- Notes.
- Images and videos.
- Voice notes.

Actions:

- Back.
- Share/export PDF.
- Edit case.
- Soft-delete case.
- Tap phone to open dialer.
- Tap diagnosis to navigate to diagnosis feed.
- Tap visual media to open image viewer.

PDF export:

- Uses Android `PdfDocument`.
- Writes a PDF into `context.cacheDir/exports`.
- Shares through Android share sheet using FileProvider.
- Includes:
  - Patient name.
  - Age.
  - Phone numbers.
  - Case date.
  - Diagnoses.
  - Mechanism.
  - Notes converted from HTML to plain text.
  - Image attachments.
- Excludes:
  - Videos.
  - Voice notes.

If PDF sharing fails:

- Check `file_paths.xml` includes `cache-path`.
- Check `Intent.FLAG_GRANT_READ_URI_PERMISSION`.
- Check `ClipData` is set on the share intent.
- Check the receiving app supports PDFs.

### Diagnosis Browsing And Case Feed

Files:

- `features/cases/DiagnosisBrowseScreen.kt`
- `features/cases/DiagnosisBrowseViewModel.kt`
- `features/cases/CaseFeedScreen.kt`
- `features/cases/CaseFeedViewModel.kt`

Cases tab behavior:

- Shows diagnoses.
- Supports search/sort within diagnoses.
- Opens a feed of cases attached to a diagnosis.
- Floating action button opens patient/case entry.

Diagnosis sort modes:

- Alphabetical.
- Most used.
- Recent.

### Image Viewer

File:

- `features/cases/ImageViewerScreen.kt`

Purpose:

- Opens case media from case detail.
- Starts at the tapped media index.

### Shifts

Files:

- `features/shifts/ShiftsListScreen.kt`
- `features/shifts/ShiftsViewModel.kt`
- `features/shifts/ShiftDetailScreen.kt`
- `features/shifts/ShiftDetailViewModel.kt`
- `features/shifts/AddShiftDialog.kt`

Behavior:

- Shifts tab lists non-deleted shifts.
- Each shift card shows date, label, and case count.
- Add shift from top bar.
- Shift detail shows linked cases.
- Add patient/case from shift detail links the new case to that shift.
- Long press on a shift soft-deletes it and shows undo snackbar.

### Consultation

Files:

- `features/consultation/ConsultationCalendarScreen.kt`
- `features/consultation/ConsultationViewModel.kt`

Behavior:

- Shows consultation date strip.
- The active consultation weekday comes from settings.
- Adds cases to the selected consultation session.
- Shows cases linked to selected consultation date.

### Settings

Files:

- `features/settings/SettingsScreen.kt`
- `features/settings/SettingsViewModel.kt`
- `features/settings/TrashScreen.kt`
- `features/settings/TrashViewModel.kt`

Settings include:

- Consultation day.
- Theme.
- Default diagnosis sort.
- Backup folder.
- Auto-backup schedule.
- Export now.
- Restore from backup.
- Last backup status.
- Optimize database.
- Trash.
- Developer attribution footer.

Footer text:

```text
Developed by Taha Douhi
tahadouhi@gmail.com
```

### Trash

Trash uses soft-delete flags and restore operations.

Trash purge:

- Scheduled daily by `WorkerScheduler`.
- Implemented by `TrashPurgeWorker`.
- Permanently removes expired trashed records and related media where safe.

## Backup And Restore

Main file:

- `data/backup/BackupEngine.kt`

Interfaces:

- `core/repository/BackupRepository.kt`

Settings:

- Backup folder URI is stored in DataStore.
- Schedule is stored in DataStore.
- Last backup timestamp/success is stored in DataStore.

Export process:

1. Acquire data lock through `DataSafetyCoordinator`.
2. Checkpoint WAL with `PRAGMA wal_checkpoint(TRUNCATE)`.
3. Validate no pending WAL frames.
4. Zip `kairos.db`.
5. Zip media files under media root, excluding temp `cases/0/`.
6. Zip DataStore preferences if present.
7. Write `manifest.json` with:
   - Backup format version.
   - App version.
   - DB schema version.
   - Timestamp.
   - Platform.
   - File hashes and sizes.

Restore process:

1. Acquire data lock.
2. Extract zip to temp cache folder.
3. Validate manifest.
4. Validate checksums and sizes.
5. Validate SQLite database quick check and foreign keys.
6. Copy database to a temporary restore DB.
7. Prepare new media root.
8. Close Room database.
9. Move old DB/media/preferences aside.
10. Move restored DB/media/preferences into place.
11. Roll back if live files were touched and restore fails.
12. Ask user to restart app after successful restore.

Safety limits:

- Max backup entries: 20,000.
- Max single entry size: 10 GB.
- Max backup payload: 50 GB.

## Data Safety Lock

Interface:

- `core/repository/DataSafetyCoordinator.kt`

Implementation:

- `data/backup/DataSafetyCoordinatorImpl.kt`

Purpose:

- Serializes sensitive writes and backup/restore operations.
- Prevents backup/restore from racing with normal data mutation.

Used by:

- Patient repository.
- Case repository.
- Shift repository.
- Consultation repository.
- Backup engine.
- Trash purge.

If data corruption or inconsistent backups are suspected, inspect usage of
`dataSafetyCoordinator.withDataLock`.

## Settings Storage

File:

- `data/settings/PreferencesStore.kt`

DataStore name:

```text
kairos_prefs
```

Stored keys:

- `consultation_day_of_week`
- `theme_mode`
- `diagnosis_sort_mode`
- `backup_folder_uri`
- `backup_schedule`
- `backup_last_run_at`
- `backup_last_success`

Settings repository:

- Interface: `core/repository/SettingsRepository.kt`
- Implementation: `data/repository/SettingsRepositoryImpl.kt`

## WorkManager

Files:

- `data/backup/WorkerScheduler.kt`
- `data/backup/ScheduledBackupWorker.kt`
- `data/backup/TrashPurgeWorker.kt`

WorkManager Hilt setup:

- `KairosApplication` implements `Configuration.Provider`.
- `HiltWorkerFactory` is injected.
- Default WorkManager initializer is disabled in the manifest.

Unique work names:

- Backup worker name is defined in `ScheduledBackupWorker`.
- Purge worker name is defined in `ScheduledBackupWorker.PURGE_WORK_NAME`.

Automatic backup:

- Off cancels unique backup work.
- Daily, weekly, monthly create/update periodic work.
- Requires battery not low.

Trash purge:

- Enqueued daily.
- Uses KEEP policy so it does not duplicate.

## Shared UI Components

Folder:

- `core/src/main/java/com/kairos/core/components`

Important components:

- `KairosTopBar` - common title/action row.
- `EmptyState` - empty screens.
- `CaseCard` - compact case list card with thumbnail, patient, date, mechanism, diagnosis chips.
- `DiagnosisAutocomplete` - search/select diagnosis chips in patient form.
- `PhoneInputSection` - patient phone chips and add row.
- `RichNotesEditor` - rich text notes.
- `MediaAttachmentSection` - image/video/audio attachments in patient form.
- `AudioRecorderModal` - recording UI.
- `AudioPlayerItem` - playback UI for voice notes.

## Important User Workflows

### Create new patient and case

1. User taps add case/patient from Dashboard/Cases/Shifts/Consultation.
2. Navigation opens `patient_case`.
3. `NewPatientTab` captures demographics, date, mechanism, diagnoses, notes, media.
4. `PatientCaseViewModel.save()` inserts patient.
5. It inserts case.
6. It creates diagnosis links.
7. It moves media from temp files to case media folder.
8. It inserts media rows.
9. UI navigates back.

### Add case for existing patient

1. User searches existing patient in `ExistingPatientTab`.
2. Selecting patient sets `selectedPatient`.
3. Patient fields are populated but locked for this flow.
4. New case is saved with existing patient id.

### Edit existing case

1. Case detail edit icon navigates to `patient_case?caseId={id}`.
2. `PatientCaseViewModel.loadCase()` loads case and patient.
3. Edit mode shows editable patient/case form.
4. Save updates patient demographics and case details.
5. Diagnosis links are recreated.

### Export/share PDF

1. User opens case detail.
2. User taps share icon.
3. `CaseDetailViewModel.exportPdf()` calls `CasePdfExporter`.
4. PDF is written to cache.
5. `CaseDetailScreen` receives `pdfToShare`.
6. Screen creates a FileProvider URI.
7. Android share sheet opens.

### Search for anything

1. User taps Dashboard search icon.
2. User enters query.
3. `SearchViewModel` debounces and calls repository.
4. DAO searches joined case/patient/diagnosis/phone data.
5. Repository filters all tokens.
6. Screen displays result cards.
7. Tap result opens case detail.

## Error Handling Patterns

Common patterns:

- ViewModels expose `error`, `message`, or snackbar state.
- Screens observe these and show snackbar.
- Long-running operations have `isSaving`, `isExporting`, `isRestoring`, or similar flags.
- Many writes are wrapped in `viewModelScope.launch`.
- Data writes and backup/restore use `DataSafetyCoordinator`.

Notable UI state classes:

- `PatientCaseUiState`
- `CaseDetailUiState`
- `DashboardUiState`
- `SearchUiState`
- `BackupUiState`
- `ShiftsUiState`
- `DiagnosisBrowseUiState`

## Debugging Playbook

### Build fails in Hilt

Check:

- Did you add a new repository interface but forget a `@Binds` method?
- Is constructor injection missing `@Inject`?
- Is the implementation in a module visible to the binding module?
- Did you add a ViewModel constructor dependency that Hilt cannot provide?

Important file:

- `data/di/DataModule.kt`

### Build fails in Room/KSP

Check:

- DAO query column aliases match Kotlin data class property names.
- Entities match table/column names.
- SQL table names are correct.
- For new schema fields, database version and migration are updated.

Important files:

- `data/db/KairosDatabase.kt`
- `data/db/dao/*.kt`
- `data/db/entities/*.kt`
- `data/db/migrations/Migrations.kt`

### App crashes opening a case

Check:

- `CaseDao.observeById()`.
- `CaseWithRelations`.
- `CaseMapper.toDomain()`.
- Media path resolution in `CaseRepositoryImpl.resolveMediaPaths()`.
- Missing files in the media folder.

### Patient edit fields are locked

Check:

- `PatientCaseScreen` edit-mode branch.
- `EditablePatientCaseForm`.
- `PatientCaseViewModel.loadCase()`.
- `PatientCaseViewModel.save()`.

Edit mode should not use the locked existing-patient form.

### Media does not show

Check:

- Is `case_media.file_path` relative or absolute?
- Repository should resolve relative DB paths to absolute UI paths.
- Does the file exist under `Pictures/kairos/cases/{caseId}`?
- Is media type correct (`IMAGE`, `VIDEO`, `AUDIO`)?
- Does FileProvider cover the path if sharing/camera is involved?

### Camera or recorder fails

Check:

- Manifest permissions.
- Runtime permission request in `NewPatientTab`.
- `MediaFileManager.newCaseMediaFile()`.
- FileProvider authority: `${context.packageName}.fileprovider`.

### Search misses expected results

Check:

- Does the case have `is_deleted = 0`?
- Does the patient have `is_deleted = 0`?
- Does the searched diagnosis exist in `case_diagnoses`?
- Is the phone number in `patient_phones`?
- Does the notes HTML contain the text?
- Is the longest token too specific or misspelled?

Files:

- `CaseDao.observeSearchCases()`
- `SearchRepositoryImpl`
- `SearchViewModel`
- `SearchScreen`

### PDF share does not work

Check:

- PDF file exists in `cacheDir/exports`.
- `file_paths.xml` includes `cache-path`.
- Share intent includes `FLAG_GRANT_READ_URI_PERMISSION`.
- Share intent includes `ClipData`.
- Receiving app supports `application/pdf`.

Files:

- `CasePdfExporter.kt`
- `CaseDetailViewModel.kt`
- `CaseDetailScreen.kt`
- `file_paths.xml`

### Backup fails

Check:

- Backup folder URI still has persisted write permission.
- `BackupEngine.ensureWalCheckpointed()`.
- `BackupEngine.ensureNoPendingWalFrames()`.
- Available storage.
- DocumentFile folder can write.

Files:

- `BackupEngine.kt`
- `SettingsScreen.kt`
- `SettingsViewModel.kt`

### Restore fails

Check:

- Backup zip has `manifest.json`.
- Checksums match.
- DB schema version is not newer than current app.
- SQLite integrity check passes.
- Foreign key check passes.
- App restart after restore.

### Bottom nav or routing weirdness

Check:

- `TopLevelDestination.entries`.
- `BottomBar` selected state compares `dest.route == currentRoute`.
- `KairosNavHost` route definitions.
- Whether current destination is top-level. Bottom bar hides on non-top-level
  screens.

## Adding New Features

Recommended pattern:

1. Add domain model or repository interface in `:core` if needed.
2. Add DAO/query/entity changes in `:data` if storage is needed.
3. Add repository implementation in `:data/repository`.
4. Bind implementation in `data/di/DataModule.kt`.
5. Add feature ViewModel and screen in `:features`.
6. Add navigation route in `KairosNavHost`.
7. Add a top-level destination only if it belongs in the bottom bar.
8. Run `.\gradlew.bat assembleDebug`.

## Adding A New Database Table

Steps:

1. Create entity in `data/db/entities`.
2. Add it to `KairosDatabase.entities`.
3. Create DAO in `data/db/dao`.
4. Add abstract DAO getter to `KairosDatabase`.
5. Provide DAO in `DatabaseModule`.
6. Create core repository interface if feature code needs it.
7. Create data repository implementation.
8. Bind implementation in `RepositoryModule`.
9. Bump database version.
10. Add migration.
11. Run build and test app upgrade.

## Adding A New Screen

Steps:

1. Create `FeatureScreen.kt` in `:features`.
2. Create `FeatureViewModel.kt` if the screen has state or data.
3. Inject repository interfaces into the ViewModel.
4. Expose immutable `StateFlow`.
5. Use `collectAsStateWithLifecycle()` in Compose.
6. Add route in `KairosNavHost`.
7. Add bottom-tab destination only if top-level.

## Rebuilding The App From Scratch

This section describes how a developer could recreate Kairos from an empty
Android project by following the current implementation.

### Required Development Environment

Use:

- Android Studio with Android Gradle Plugin 8.7.x support.
- JDK 17.
- Android SDK compile SDK 35.
- Kotlin 2.1.0.
- Gradle wrapper compatible with the checked-in project.

The project is currently configured for:

```text
compileSdk = 35
minSdk = 26
targetSdk = 35
Java = 17
Kotlin JVM target = 17
```

Do not rely on globally installed Gradle if the wrapper is available. Use:

```powershell
.\gradlew.bat assembleDebug
```

### Rebuild Order

The safest order is:

1. Create the Gradle multi-module skeleton.
2. Add dependency versions and plugin aliases.
3. Build the `:core` module models, repository interfaces, theme, media helpers, and shared UI.
4. Build the `:data` module Room entities, DAOs, mappers, repositories, DataStore, backup, and DI.
5. Build the `:features` module screens and ViewModels.
6. Build the `:app` module application class, activity, navigation, manifest, and resources.
7. Run `assembleDebug`.
8. Install on a device and manually verify workflows.

### Step 1: Create The Gradle Module Skeleton

Create:

```text
Kairos/
  settings.gradle.kts
  build.gradle.kts
  gradle/libs.versions.toml
  app/
  core/
  data/
  features/
```

`settings.gradle.kts` must include:

```kotlin
rootProject.name = "Kairos"
include(":app")
include(":core")
include(":data")
include(":features")
```

Dependency direction must stay one-way:

```text
app -> core, data, features
features -> core
data -> core
core -> no project modules
```

Important:

- `features` must not depend on `data`.
- `core` must not depend on Android app implementation details.
- Repository interfaces live in `core`; implementations live in `data`.

### Step 2: Recreate Gradle Dependencies

Use `gradle/libs.versions.toml` as the source of truth.

Required plugin aliases:

- Android application.
- Android library.
- Kotlin Android.
- Kotlin Compose plugin.
- KSP.
- Hilt.

Required library groups:

- Compose UI, Material 3, Foundation, tooling.
- Activity Compose.
- Navigation Compose.
- Lifecycle runtime Compose and ViewModel Compose.
- Hilt Android, compiler, navigation compose, WorkManager integration.
- Room runtime, KTX, compiler.
- DataStore Preferences.
- WorkManager runtime KTX.
- Coil Compose.
- Media3 ExoPlayer and UI.
- Rich editor Compose.
- Accompanist permissions.
- DocumentFile.

Module dependency highlights:

- `app` needs Compose, Navigation, Hilt, WorkManager, and all three project modules.
- `features` needs Compose, Navigation, Hilt, Lifecycle, and `core`.
- `data` needs Room, KSP, Hilt, DataStore, WorkManager, DocumentFile, and `core`.
- `core` needs Compose components, theme resources, media helpers, Coil/Media3 where shared components use them, and repository/model code.

### Step 3: Rebuild `:core`

Recommended order:

1. Theme colors in `core/theme/Color.kt`.
2. Typography in `core/theme/Type.kt`.
3. Shapes in `core/theme/Shape.kt`.
4. `KairosTheme` and `LocalKairosExtraColors`.
5. Domain models in `core/model`.
6. Repository interfaces in `core/repository`.
7. Media helpers in `core/media`.
8. Shared Compose components in `core/components`.

Core must define the app's public contracts.

Minimum core model set:

- `Patient`
- `PatientPhone`
- `Case`
- `Diagnosis`
- `MediaItem`
- `MediaType`
- `Shift`
- `ConsultationSession`
- `AppSettings`
- Settings enums

Minimum repository interface set:

- `PatientRepository`
- `CaseRepository`
- `DiagnosisRepository`
- `MediaRepository`
- `ShiftRepository`
- `ConsultationRepository`
- `SettingsRepository`
- `BackupRepository`
- `DashboardRepository`
- `SearchRepository`
- `DataSafetyCoordinator`

Core media requirements:

- `MediaFileManager` must create files under app external files pictures directory.
- It must store database paths as relative paths.
- It must convert files back to FileProvider URIs for camera/share flows.
- `AudioRecorderEngine` must handle start, stop, and cancel.

Core shared UI requirements:

- `KairosTopBar`
- `CaseCard`
- `EmptyState`
- `DiagnosisAutocomplete`
- `PhoneInputSection`
- `RichNotesEditor`
- `MediaAttachmentSection`
- `AudioRecorderModal`
- `AudioPlayerItem`

### Step 4: Rebuild `:data`

Recommended order:

1. Room entities.
2. Room relations.
3. DAOs.
4. `KairosDatabase`.
5. Migrations container.
6. Mappers.
7. DataStore preferences.
8. Data safety coordinator.
9. Repository implementations.
10. Backup and restore engine.
11. WorkManager workers and scheduler.
12. Hilt modules.

Room entity set:

```text
patients
patient_phones
cases
case_diagnoses
case_media
diagnoses
shifts
shift_cases
consultation_sessions
consultation_cases
```

Current database version:

```kotlin
version = 1
```

If rebuilding the same app with no installed users, version 1 is fine. If
shipping updates to real users, any schema change after release needs a
migration.

Room KSP settings:

```kotlin
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
    arg("room.generateKotlin", "true")
}
```

DAO design requirements:

- All active lists must filter `is_deleted = 0`.
- Trash screens observe `is_deleted = 1`.
- Case queries usually return `CaseWithRelations` so patient, diagnoses, and media are loaded.
- Dashboard queries must be reactive where possible.
- Search query must join cases to patients and use subqueries for phones and diagnoses.

Repository implementation requirements:

- Wrap important writes in `DataSafetyCoordinator.withDataLock`.
- Patient updates replace all phone rows.
- Case updates clear/recreate diagnosis cross refs.
- Case media paths are resolved to absolute paths before reaching UI.
- Backup export/restore must hold the data lock.

Hilt requirements:

- `DatabaseModule` provides Room database and DAOs.
- `RepositoryModule` binds all repository interfaces to implementations.
- Every new repository implementation needs a binding.

### Step 5: Rebuild `:features`

Recommended feature order:

1. Settings, because theme and app preferences depend on settings repository.
2. Patient/case entry, because most workflows create cases.
3. Case detail, diagnosis browsing, case feed, and image viewer.
4. Shifts.
5. Consultation.
6. Dashboard.
7. Search.

ViewModel pattern:

```kotlin
@HiltViewModel
class ExampleViewModel @Inject constructor(
    private val repo: SomeRepository,
) : ViewModel() {
    val ui: StateFlow<ExampleUiState> = ...
}
```

Screen pattern:

```kotlin
@Composable
fun ExampleScreen(
    viewModel: ExampleViewModel = hiltViewModel(),
) {
    val state by viewModel.ui.collectAsStateWithLifecycle()
}
```

Feature screens should not import Room DAOs or data repository implementations.

### Step 6: Rebuild `:app`

Required app files:

```text
app/src/main/java/com/kairos/KairosApplication.kt
app/src/main/java/com/kairos/MainActivity.kt
app/src/main/java/com/kairos/navigation/KairosNavHost.kt
app/src/main/java/com/kairos/navigation/Destinations.kt
app/src/main/java/com/kairos/ui/BottomBar.kt
app/src/main/AndroidManifest.xml
app/src/main/res/xml/file_paths.xml
app/src/main/res/xml/backup_rules.xml
app/src/main/res/xml/data_extraction_rules.xml
app/src/main/res/values/colors.xml
app/src/main/res/values/themes.xml
```

`KairosApplication` must:

- Use `@HiltAndroidApp`.
- Implement `Configuration.Provider`.
- Inject `HiltWorkerFactory`.
- Inject `WorkerScheduler`.
- Inject `SettingsRepository`.
- Schedule trash purge on app start.
- Observe backup schedule and reschedule WorkManager backup jobs.

`MainActivity` must:

- Use `@AndroidEntryPoint`.
- Enable edge-to-edge.
- Observe settings.
- Apply `KairosTheme`.
- Create `NavController`.
- Show `BottomBar` for top-level routes.
- Host `KairosNavHost`.

Manifest must:

- Declare camera, audio, and notification permissions.
- Mark camera and microphone hardware as optional.
- Register `MainActivity`.
- Register FileProvider with authority `${applicationId}.fileprovider`.
- Disable default WorkManager initializer because Hilt provides configuration.

FileProvider paths must include:

- External files path for media.
- Cache path for PDF exports.

### Step 7: Recreate Navigation

Top-level destinations:

```text
Dashboard
Shifts
Consultation
Cases
Settings
```

Non-top-level routes:

```text
search
shift_detail/{shiftId}
case_feed/{diagnosisId}?name={diagnosisName}
case_detail/{caseId}
image_viewer/{caseId}?index={index}
trash
patient_case?shiftId={shiftId}&sessionId={sessionId}&caseId={caseId}
```

Navigation details:

- Bottom bar renders `TopLevelDestination.entries`.
- Start destination is Dashboard.
- Bottom bar is visible only when current route matches a top-level route.
- Search is reached from the Dashboard top bar.
- Case detail is reached from dashboard recent list, search results, case feeds, shifts, and consultation.
- Patient/case entry is reused for create and edit flows.

### Step 8: Android Resource Requirements

Resources that must exist:

- App icon resources in mipmap/drawable.
- `app_name` string.
- Splash/theme definitions.
- FileProvider XML.
- Backup/data extraction XML.
- Fonts in `core/src/main/res/font`.

Core fonts currently expected:

- `inter_regular.ttf`
- `inter_medium.ttf`
- `inter_semibold.ttf`
- `inter_bold.ttf`

If fonts are missing, `core/theme/Type.kt` will fail to compile or runtime
typography will not match.

### Step 9: Verification Checklist After Rebuild

Run:

```powershell
.\gradlew.bat clean
.\gradlew.bat assembleDebug
```

Then manually test:

1. App launches to Dashboard.
2. Bottom tabs switch without transition lag.
3. Add shift.
4. Open shift detail.
5. Add patient/case from shift.
6. Add patient name, age, phone, diagnosis, mechanism, notes.
7. Attach image.
8. Record audio if microphone permission is available.
9. Save case.
10. Open case detail.
11. Edit case and change patient name/age/phone.
12. Confirm changes persist.
13. Search for patient name.
14. Search for phone number.
15. Search for diagnosis.
16. Search for text from notes.
17. Export/share PDF.
18. Confirm PDF title is patient name.
19. Confirm PDF does not include app branding.
20. Confirm images appear in PDF.
21. Soft-delete a case.
22. Open trash and restore/delete.
23. Set backup folder.
24. Export backup.
25. Restore backup on a test install.
26. Restart app after restore.

### Minimum Rebuild Acceptance Criteria

A rebuilt app is not equivalent until all of these work:

- Hilt graph compiles.
- Room schema compiles.
- App launches without crashing.
- Dashboard numbers update after adding data.
- Search finds data across all requested fields.
- Patient/case creation works.
- Patient/case editing works, including demographic fields.
- Media attachment works.
- Case detail opens.
- PDF export/share works.
- Backup export works.
- Trash restore/delete works.

### Rebuild Failure Matrix

Use this table to localize failures:

| Failure | Likely Area | First Files To Inspect |
| --- | --- | --- |
| Hilt binding error | DI setup | `DataModule.kt`, ViewModel constructors |
| Room query error | DAO/entity mismatch | `CaseDao.kt`, entity column names |
| App will not launch | Application/activity/theme | `KairosApplication.kt`, `MainActivity.kt`, manifest |
| Bottom bar missing | Route mismatch | `BottomBar.kt`, `Destinations.kt`, `KairosNavHost.kt` |
| Search empty | DAO query/token filtering | `CaseDao.kt`, `SearchRepositoryImpl.kt` |
| Case detail empty | Case relation query | `CaseDao.kt`, `CaseWithRelations.kt`, `CaseMapper.kt` |
| Images missing | File path resolution | `MediaFileManager.kt`, `CaseRepositoryImpl.kt` |
| PDF share fails | FileProvider/share intent | `file_paths.xml`, `CaseDetailScreen.kt`, `CasePdfExporter.kt` |
| Backup fails | Storage/WAL/permissions | `BackupEngine.kt`, `SettingsScreen.kt` |
| Restore fails | Manifest/db validation | `BackupEngine.kt`, `KairosDatabase.kt` |

### Reconstructing Feature Behavior Without AI

If a developer must reimplement a feature manually, use these contracts:

- Dashboard reads counts and recent rows from `DashboardRepository`.
- Search reads `SearchRepository.observeSearch(query)` and navigates to case detail.
- Patient/case entry writes through `PatientRepository`, `CaseRepository`, `DiagnosisRepository`, and `MediaRepository`.
- Case detail reads `CaseRepository.observeById(caseId)`.
- PDF export uses a domain `Case`, not direct DAO rows.
- Shifts read/write through `ShiftRepository`.
- Consultation reads/writes through `ConsultationRepository`.
- Settings reads/writes through `SettingsRepository`.
- Backup/restore uses `BackupRepository`.

Do not bypass repositories from screens. If a screen needs new data, add it to
a repository interface and implement it in `:data`.

## Coding Conventions In This App

- Prefer repository interfaces from `:core`.
- Keep Room out of feature code.
- Keep UI state in data classes.
- Use `StateFlow` for ViewModel state.
- Use `Flow` for reactive Room/DataStore reads.
- Use `viewModelScope.launch` for writes.
- Use `DataSafetyCoordinator` for writes that must not race backup/restore.
- Use `MaterialTheme` and `LocalKairosExtraColors`.
- Use existing shared components before creating new ones.
- Keep database file paths relative; resolve absolute paths in repositories.

## Current Known Technical Debt

This app is functional, but a developer should be aware of these areas:

- There are no formal unit or instrumentation tests yet.
- Database version is still `1`; future schema changes need migrations.
- Some generated build outputs may appear dirty if the project was built locally.
- Rich notes render as plain text in case detail and PDF export.
- PDF export includes images only, not video frames or audio transcripts.
- Sync-related columns exist (`remote_id`, `sync_state`, `last_synced_at`) but
  there is no remote sync implementation yet.

## Useful Commands

List Kotlin files:

```powershell
rg --files -g "*.kt"
```

Search code:

```powershell
rg -n "PatientCaseViewModel|CaseDao|BackupEngine"
```

Build everything:

```powershell
.\gradlew.bat assembleDebug
```

Compile a module:

```powershell
.\gradlew.bat :features:compileDebugKotlin
.\gradlew.bat :data:compileDebugKotlin
```

Install debug app:

```powershell
.\gradlew.bat :app:installDebug
```

## File Map

```text
app/
  MainActivity.kt
  KairosApplication.kt
  navigation/
    KairosNavHost.kt
    Destinations.kt
  ui/
    BottomBar.kt

core/
  components/
  media/
  model/
  repository/
  theme/

data/
  backup/
  db/
    dao/
    entities/
    migrations/
    relations/
  di/
  mapper/
  repository/
  settings/

features/
  dashboard/
  search/
  patient/
  cases/
  shifts/
  consultation/
  settings/
```

## Mental Model For Debugging

When something breaks, follow the data path in this order:

```text
Screen
  -> ViewModel state/action
    -> core repository interface
      -> data repository implementation
        -> DAO/DataStore/file helper
          -> database/file system
```

For UI bugs, start at the screen and ViewModel.

For missing or wrong data, start at the DAO query and mapper.

For crashes around media, PDF, backup, or restore, start at file paths and
FileProvider configuration.

For app-wide startup/scheduling issues, start at `KairosApplication`.

For navigation issues, start at `KairosNavHost` and `TopLevelDestination`.
