## Stack
Jetpack Compose + Material3 — declarative Android UI; screens expose callbacks and collect ViewModel StateFlow with lifecycle awareness.
Navigation Compose — single-activity route graph; optional route args encode create/link/edit patient-case flows.
Hilt + hilt-navigation-compose + hilt-work — inject Application/Activity/ViewModels/repositories/HiltWorkerFactory/CoroutineWorkers.
Room + KSP — local relational store for patients/cases/diagnoses/media/shifts/consultation sessions with exported schema.
DataStore Preferences — durable app settings; stores enum names and SAF backup folder URI.
WorkManager — periodic trash purge and scheduled backups with Hilt workers and battery-not-low constraints.
DocumentFile + Storage Access Framework — user-chosen backup folder and restore zip access with persisted tree permission.
Coil 3 — Compose image/video thumbnail loading from app-private media files.
Media3 ExoPlayer — audio note playback and full-screen video playback.
compose-rich-editor — rich clinical notes editor; persisted as HTML.
Accompanist permissions — Compose permission state for RECORD_AUDIO.
Android PdfDocument — on-device case PDF export without external PDF library.
FileProvider — camera output and PDF sharing for app-private files.
## Architecture
[Android launcher]
  -> KairosApplication: Hilt graph + WorkManager config + schedule purge/backup
  -> MainActivity: edge-to-edge + theme from SettingsRepository + Scaffold shell
  -> KairosNavHost: dashboard/search/shifts/consultation/cases/settings/patient_case routes
  -> Feature Screen: collectAsStateWithLifecycle(StateFlow) + user callbacks
  -> Hilt ViewModel: MutableStateFlow/stateIn + viewModelScope coroutines
  -> core Repository interface: feature/data boundary
  -> data RepositoryImpl: DataSafetyCoordinator lock + Room transaction/mapping
  -> Room DAO: Flow queries + soft-delete filters + junction tables
  -> kairos.db: patients/cases/diagnoses/media/shifts/sessions
[PatientCase/NewPatientTab]
  -> MediaFileManager + AudioRecorderEngine: temp cases/0 files before save
  -> CaseRepository/MediaRepository: final cases/{caseId} relative paths in DB
[Settings/Workers]
  -> BackupEngine: DataSafetyCoordinator lock + WAL checkpoint + SAF zip + manifest/checksums
  -> TrashPurgeWorker: 30-day hard purge + orphan diagnosis cleanup + media file delete
## File Map
settings.gradle.kts — four-module boundary app/core/data/features; features never depends on data — Gradle.
gradle/libs.versions.toml — centralized versions for Compose/Hilt/Room/DataStore/WorkManager/Coil/Media3/rich editor — Gradle version catalogs.
app/src/main/AndroidManifest.xml — CAMERA/RECORD_AUDIO/POST_NOTIFICATIONS, allowBackup=false, FileProvider, WorkManager initializer removal — Android framework.
app/src/main/res/xml/file_paths.xml — FileProvider exposes external-files Pictures/kairos and cache exports only — AndroidX FileProvider.
app/src/main/java/com/kairos/KairosApplication.kt — startup schedules daily purge and observes backup schedule changes — WorkerScheduler, SettingsRepository, HiltWorkerFactory.
app/src/main/java/com/kairos/MainActivity.kt — shell observes settings for theme and shows BottomBar only on exact top-level routes — SettingsRepository, KairosTheme, KairosNavHost.
app/src/main/java/com/kairos/navigation/KairosNavHost.kt — full route graph; transitions disabled; patient_case uses optional shiftId/sessionId/caseId sentinels — Navigation Compose, feature screens.
app/src/main/java/com/kairos/navigation/Destinations.kt — top-level tab metadata with active/inactive Material icons — Compose material-icons.
app/src/main/java/com/kairos/ui/BottomBar.kt — bottom navigation color policy uses app extra colors, not raw Material defaults — TopLevelDestination, LocalKairosExtraColors.
core/src/main/java/com/kairos/core/model/AppSettings.kt — defaults: consultation Thursday, theme SYSTEM, diagnosis alphabetical, backup OFF — java.time.DayOfWeek.
core/src/main/java/com/kairos/core/model/MediaItem.kt — DB-facing media item stores relative filePath under media root — MediaType enum.
core/src/main/java/com/kairos/core/repository/*.kt — feature-facing contracts; no data imports allowed here — kotlinx.coroutines.flow.Flow, core models.
core/src/main/java/com/kairos/core/media/MediaFileManager.kt — app-private media root, relative path conversion, FileProvider URIs — Context, Environment, FileProvider.
core/src/main/java/com/kairos/core/media/AudioRecorderEngine.kt — AAC/MPEG_4 mic recorder wrapper with defensive stop/cancel — MediaRecorder.
core/src/main/java/com/kairos/core/components/RichNotesEditor.kt — rich text HTML editor with toolbar and bring-into-view behavior — compose-rich-editor.
core/src/main/java/com/kairos/core/components/MediaAttachmentSection.kt — shared pending/saved media grid; audio separated from visual media — Coil, AudioPlayerItem.
core/src/main/java/com/kairos/core/components/AudioPlayerItem.kt — ExoPlayer-backed voice note row with slider and delete action — Media3.
core/src/main/java/com/kairos/core/components/AudioRecorderModal.kt — modal mic/stop UI; package-internal duration formatter reused by audio row — Material3.
core/src/main/java/com/kairos/core/components/DiagnosisAutocomplete.kt — chip selector with prefix suggestions and inline add-new option — Diagnosis model.
core/src/main/java/com/kairos/core/components/CaseCard.kt — reusable case summary card; thumbnail prefers primary media then first media — Coil, Case.
core/src/main/java/com/kairos/core/components/PhoneInputRow.kt — phone chips plus draft input; label parameter currently unused by UI — PatientPhone.
core/src/main/java/com/kairos/core/theme/KairosTheme.kt — Material color schemes plus LocalKairosExtraColors tokens — Color/Type/Shape.
data/src/main/java/com/kairos/data/di/DataModule.kt — Room singleton providers and interface-to-impl Hilt bindings — KairosDatabase, repositories, BackupEngine.
data/src/main/java/com/kairos/data/db/KairosDatabase.kt — schema version 1; entities include sync columns but no sync engine — RoomDatabase.
data/src/main/java/com/kairos/data/db/migrations/Migrations.kt — empty migration list with explicit future migration instructions — Room Migration.
data/src/main/java/com/kairos/data/db/entities/CaseEntities.kt — cases, case_diagnoses, case_media; patient/case/media cascades — Room foreign keys.
data/src/main/java/com/kairos/data/db/entities/PatientEntities.kt — patients + phones; patients soft-delete, phones cascade — Room entities.
data/src/main/java/com/kairos/data/db/entities/DiagnosisEntities.kt — diagnosis names have unique binary index while lookups are case-insensitive — Room Index.
data/src/main/java/com/kairos/data/db/entities/ShiftEntities.kt — shifts soft-delete; shift_cases junction cascades from shift or case — Room foreign keys.
data/src/main/java/com/kairos/data/db/entities/ConsultationEntities.kt — date-keyed sessions; consultation_cases junction cascades — Room foreign keys.
data/src/main/java/com/kairos/data/db/relations/CaseWithRelations.kt — Room aggregate loads patient phones, diagnoses, media for each case — @Relation, @Junction.
data/src/main/java/com/kairos/data/db/dao/CaseDao.kt — active case feeds, dashboard counts, recent rows, token-anchor search rows, trash purge IDs — Room SQL.
data/src/main/java/com/kairos/data/db/dao/PatientDao.kt — name-only patient search, soft restore, purge protected by active cases — Room SQL.
data/src/main/java/com/kairos/data/db/dao/DiagnosisDao.kt — case-count sorts, prefix autocomplete, orphan cleanup after hard delete — Room SQL.
data/src/main/java/com/kairos/data/db/dao/ShiftDao.kt — shift list includes active case counts; long-press soft delete uses this data — Room SQL.
data/src/main/java/com/kairos/data/db/dao/ConsultationSessionDao.kt — range query returns only non-deleted sessions with active case counts — Room SQL.
data/src/main/java/com/kairos/data/db/dao/CaseMediaDao.kt — primary media is maintained by clear-then-set transaction in repository — Room SQL.
data/src/main/java/com/kairos/data/mapper/CaseMapper.kt — CaseWithRelations to domain; entity mapper drops sync fields and deletion flags on writes — core.model.
data/src/main/java/com/kairos/data/mapper/PatientMapper.kt — patient upsert rewrites phones wholesale via delete+insert — core.model.
data/src/main/java/com/kairos/data/mapper/DiagnosisMapper.kt — DiagnosisWithCount preserves computed caseCount — core.model.
data/src/main/java/com/kairos/data/mapper/ShiftMapper.kt — list mapper preserves caseCount; entity mapper uses caseCount=0 — core.model.
data/src/main/java/com/kairos/data/mapper/ConsultationMapper.kt — range mapper preserves caseCount; entity mapper uses caseCount=0 — core.model.
data/src/main/java/com/kairos/data/settings/PreferencesStore.kt — DataStore key mapping and enum fallback defaults — AppSettings.
data/src/main/java/com/kairos/data/backup/DataSafetyCoordinatorImpl.kt — reentrant coroutine-context mutex for backup/restore/purge/repository writes — Mutex.
data/src/main/java/com/kairos/data/backup/BackupEngine.kt — export/restore/vacuum; validates manifest/checksums/schema and rolls back live-file restore failures — BackupRepository, DocumentFile.
data/src/main/java/com/kairos/data/backup/WorkerScheduler.kt — unique periodic backup UPDATE policy and trash purge KEEP policy — WorkManager.
data/src/main/java/com/kairos/data/backup/ScheduledBackupWorker.kt — scheduled backup reads settings once, records result, prunes via BackupPruner generational policy, notifies if permitted — BackupEngine.
data/src/main/java/com/kairos/data/backup/BackupPruner.kt — pure retention policy: keep 5 newest zips plus newest per month for 12 months — no Android deps, unit tested.
data/src/main/java/com/kairos/data/backup/TrashPurgeWorker.kt — collects media paths before DB hard delete; deletes files after DB consistency — DAOs, MediaFileManager.
data/src/main/java/com/kairos/data/repository/CaseRepositoryImpl.kt — transactional case save, diagnosis link replacement, optional shift/session linking, absolute media path resolution — CaseDao, DiagnosisDao.
data/src/main/java/com/kairos/data/repository/PatientRepositoryImpl.kt — patient upsert with full phone replacement inside transaction — PatientDao.
data/src/main/java/com/kairos/data/repository/DiagnosisRepositoryImpl.kt — lock-protected case-insensitive get-or-create and sort switching — DiagnosisDao.
data/src/main/java/com/kairos/data/repository/MediaRepositoryImpl.kt — DB media insert/delete; file delete occurs after DB row delete — CaseMediaDao, MediaFileManager.
data/src/main/java/com/kairos/data/repository/SearchRepositoryImpl.kt — longest-token SQL LIKE anchor plus Kotlin all-token filtering — CaseDao.SearchCaseRow.
data/src/main/java/com/kairos/data/repository/SettingsRepositoryImpl.kt — thin DataStore-backed settings facade — PreferencesStore.
data/src/main/java/com/kairos/data/repository/ShiftRepositoryImpl.kt — shift upsert/soft-delete/restore with data lock — ShiftDao.
data/src/main/java/com/kairos/data/repository/ConsultationRepositoryImpl.kt — date get-or-create and date-range session observation — ConsultationSessionDao.
data/src/main/java/com/kairos/data/repository/DashboardRepositoryImpl.kt — dashboard aggregates from DAO counts and recent rows — PatientDao, CaseDao, ShiftDao.
features/src/main/java/com/kairos/features/patient/PatientCaseViewModel.kt — combined new patient/existing patient/edit case workflow; pending media staging and recording — repositories, media helpers.
features/src/main/java/com/kairos/features/patient/PatientCaseScreen.kt — two-tab entry shell; edit and selected-existing modes collapse to one form — PatientCaseViewModel.
features/src/main/java/com/kairos/features/patient/NewPatientTab.kt — form launchers for camera/video/gallery/audio/date/rich notes — MediaFileManager, ActivityResultContracts.
features/src/main/java/com/kairos/features/patient/ExistingPatientTab.kt — existing-patient search/select tab by name only — PatientCaseUiState.
features/src/main/java/com/kairos/features/cases/CaseDetailViewModel.kt — case detail Flow plus PDF share state and media actions — CaseRepository, MediaRepository, CasePdfExporter.
features/src/main/java/com/kairos/features/cases/CaseDetailScreen.kt — patient/case detail, phone dial intents, PDF share FileProvider, media grids — CaseDetailViewModel.
features/src/main/java/com/kairos/features/cases/CasePdfExporter.kt — cache PDF writer; exports patient/case/notes/images, omits video/audio — PdfDocument.
features/src/main/java/com/kairos/features/cases/ImageViewerScreen.kt — full-screen visual media pager, zoomable images, ExoPlayer videos, gallery save — CaseDetailViewModel.
features/src/main/java/com/kairos/features/cases/DiagnosisBrowseViewModel.kt — diagnosis sorting/filtering and add-diagnosis command — DiagnosisRepository.
features/src/main/java/com/kairos/features/cases/DiagnosisBrowseScreen.kt — diagnosis list toolbar sort/add and FAB to patient_case — DiagnosisBrowseViewModel.
features/src/main/java/com/kairos/features/cases/CaseFeedViewModel.kt — diagnosis-specific case feed from SavedStateHandle diagnosisId/name — CaseRepository.
features/src/main/java/com/kairos/features/cases/CaseFeedScreen.kt — list of CaseCard results for one diagnosis — CaseFeedViewModel.
features/src/main/java/com/kairos/features/consultation/ConsultationViewModel.kt — one-year date strip around today; selectable days constrained by setting — repositories, java.time.
features/src/main/java/com/kairos/features/consultation/ConsultationCalendarScreen.kt — horizontal consultation-date strip; FAB creates session before patient entry — ConsultationViewModel.
features/src/main/java/com/kairos/features/dashboard/DashboardViewModel.kt — aggregate counts, period deltas, milestone celebration state — DashboardRepository.
features/src/main/java/com/kairos/features/dashboard/DashboardScreen.kt — stats cards, period comparison, milestone bar, recent activity — DashboardViewModel.
features/src/main/java/com/kairos/features/search/SearchViewModel.kt — debounced query StateFlow backed by SearchRepository Flow — SearchRepository.
features/src/main/java/com/kairos/features/search/SearchScreen.kt — focused global search UI rendering result cards from query state — SearchViewModel.
features/src/main/java/com/kairos/features/settings/SettingsViewModel.kt — settings mutations plus manual export/restore/vacuum state — SettingsRepository, BackupRepository.
features/src/main/java/com/kairos/features/settings/SettingsScreen.kt — consultation/theme/sort/backup/trash UI; persists SAF folder permission; restore restart prompt — SettingsViewModel.
features/src/main/java/com/kairos/features/settings/TrashViewModel.kt — combines trashed patients/cases/shifts/sessions and restores individually — repositories.
features/src/main/java/com/kairos/features/settings/TrashScreen.kt — trash grouped by entity with days-until-purge labels — TrashViewModel.
features/src/main/java/com/kairos/features/shifts/ShiftsViewModel.kt — shift list, add dialog state, soft-delete undo state — ShiftRepository.
features/src/main/java/com/kairos/features/shifts/ShiftsListScreen.kt — shift cards; long-press soft-deletes with Snackbar undo — ShiftsViewModel.
features/src/main/java/com/kairos/features/shifts/ShiftDetailViewModel.kt — shift detail combines shift Flow and linked cases Flow — ShiftRepository, CaseRepository.
features/src/main/java/com/kairos/features/shifts/ShiftDetailScreen.kt — linked case list; long-press case unlinks from shift — ShiftDetailViewModel.
features/src/main/java/com/kairos/features/shifts/AddShiftDialog.kt — shift date/label dialog using Material DatePicker — ShiftsViewModel callback.
## Key Symbols
KairosApplication.onCreate(): Unit — schedules purge and backup observer
KairosApplication.workManagerConfiguration: Configuration — supplies HiltWorkerFactory
MainActivity.onCreate(savedInstanceState: Bundle?): Unit — hosts themed nav shell
KairosNavHost(navController: NavHostController, modifier: Modifier): Unit — declares all routes
BottomBar(currentRoute: String?, onTabSelected: (TopLevelDestination) -> Unit): Unit — renders top-level navigation
KairosTheme(darkTheme: Boolean, content: @Composable () -> Unit): Unit — installs Material/extras theme
MediaFileManager.resolve(relativePath: String): File — resolves stored media path
MediaFileManager.newCaseMediaFile(caseId: Long, type: MediaType): File — reserves case media file
MediaFileManager.toRelative(file: File): String — stores media-root-relative path
MediaFileManager.delete(relativePath: String): Boolean — deletes one stored media file
MediaFileManager.deleteCaseDir(caseId: Long): Boolean — removes all case media files
MediaFileManager.contentUriFor(file: File): Uri — returns FileProvider URI
MediaFileManager.rootDir(): File — exposes backup media root
AudioRecorderEngine.start(outputFile: File): Unit — starts AAC mic recording
AudioRecorderEngine.stop(): Unit — stops and releases recorder
AudioRecorderEngine.cancel(outputFile: File): Unit — stops and deletes recording
DataSafetyCoordinator.withDataLock(block: suspend () -> T): T — serializes data mutations
DataSafetyCoordinatorImpl.withDataLock(block: suspend () -> T): T — reentrant mutex wrapper
PreferencesStore.settings: Flow<AppSettings> — maps preferences to defaults
WorkerScheduler.scheduleBackup(schedule: BackupSchedule): Unit — registers/cancels periodic backup
WorkerScheduler.scheduleTrashPurge(): Unit — idempotently registers purge worker
ScheduledBackupWorker.doWork(): Result — exports scheduled backup once
TrashPurgeWorker.doWork(): Result — purges expired trash safely
BackupEngine.export(folderUri: String): BackupResult — zips DB/media/settings
BackupEngine.restore(zipUri: String): RestoreResult — replaces DB/media/settings atomically
BackupEngine.vacuumDatabase(): Unit — runs SQLite VACUUM under lock
CaseRepositoryImpl.upsertCase(case: Case, diagnosisNames: List<String>, linkShiftId: Long?, linkSessionId: Long?): Long — saves case graph transactionally
CaseRepositoryImpl.getById(id: Long): Case? — loads case with absolute media
CaseRepositoryImpl.observeById(id: Long): Flow<Case?> — streams case with relations
CaseRepositoryImpl.observeByDiagnosis(diagnosisId: Long): Flow<List<Case>> — streams active diagnosis feed
CaseRepositoryImpl.observeByShift(shiftId: Long): Flow<List<Case>> — streams active shift cases
CaseRepositoryImpl.observeBySession(sessionId: Long): Flow<List<Case>> — streams active session cases
CaseRepositoryImpl.unlinkFromShift(caseId: Long, shiftId: Long): Unit — removes shift link
CaseRepositoryImpl.unlinkFromSession(caseId: Long, sessionId: Long): Unit — removes session link
CaseRepositoryImpl.softDelete(id: Long): Unit — marks case trashed
CaseRepositoryImpl.restore(id: Long): Unit — untrashes case
CaseRepositoryImpl.observeTrashed(): Flow<List<Case>> — streams trashed cases
PatientRepositoryImpl.upsert(patient: Patient): Long — saves patient and phones
PatientRepositoryImpl.search(query: String): Flow<List<Patient>> — searches active names
PatientRepositoryImpl.softDelete(id: Long): Unit — marks patient trashed
PatientRepositoryImpl.restore(id: Long): Unit — untrashes patient
DiagnosisRepositoryImpl.getOrCreate(name: String): Long — case-insensitive diagnosis insert
DiagnosisRepositoryImpl.observeAll(sort: DiagnosisSortMode): Flow<List<Diagnosis>> — streams sorted diagnoses
DiagnosisRepositoryImpl.searchByPrefix(prefix: String, limit: Int): List<Diagnosis> — autocomplete by prefix
MediaRepositoryImpl.add(item: MediaItem): Long — inserts saved media row
MediaRepositoryImpl.delete(id: Long): Unit — deletes media row and file
MediaRepositoryImpl.setPrimary(caseId: Long, mediaId: Long): Unit — switches primary media
MediaRepositoryImpl.observeForCase(caseId: Long): Flow<List<MediaItem>> — streams raw relative media
SearchRepositoryImpl.observeSearch(query: String): Flow<List<SearchResult>> — token filters global case search
SettingsRepositoryImpl.observeSettings(): Flow<AppSettings> — streams DataStore settings
ShiftRepositoryImpl.upsert(shift: Shift): Long — saves shift
ShiftRepositoryImpl.observeAll(): Flow<List<Shift>> — streams active shifts with counts
ConsultationRepositoryImpl.getOrCreateForDate(dateMillis: Long): Long — returns date session id
ConsultationRepositoryImpl.observeForDateRange(startMillis: Long, endMillis: Long): Flow<List<ConsultationSession>> — streams active sessions in range
DashboardRepositoryImpl.observeRecentCases(): Flow<List<RecentCase>> — streams five newest active cases
DashboardRepositoryImpl.countCasesInRange(startMs: Long, endMs: Long): Int — counts by created_at
CaseDao.observeSearchCases(likeQuery: String, limit: Int): Flow<List<SearchCaseRow>> — SQL anchor for search
CaseDao.observeRecentCases(): Flow<List<RecentCaseRow>> — dashboard recent-case query
CaseDao.countCasesInRange(startMs: Long, endMs: Long): Int — created_at period count
CaseDao.listExpiredTrash(threshold: Long): List<Long> — expired trashed case IDs
CaseDao.hardDelete(ids: List<Long>): Unit — cascades case deletion
PatientDao.purgeOlderThan(threshold: Long): Int — purges orphaned trashed patients
DiagnosisDao.deleteOrphaned(): Int — removes unreferenced diagnoses
ConsultationSessionDao.findByDate(dateMillis: Long): ConsultationSessionEntity? — lookup ignores deletion state
CasePdfExporter.export(case: Case): File — writes cache PDF report
PatientCaseViewModel.loadCase(caseId: Long): Unit — pre-fills edit form
PatientCaseViewModel.setDiagnosisQuery(q: String): Unit — fetches autocomplete suggestions
PatientCaseViewModel.selectDiagnosis(name: String): Unit — adds unique diagnosis chip
PatientCaseViewModel.setSearchQuery(q: String): Unit — drives existing-patient search
PatientCaseViewModel.selectExistingPatient(patient: Patient): Unit — locks patient fields for case
PatientCaseViewModel.attachFile(sourceFile: File, type: MediaType): Unit — stages pending media
PatientCaseViewModel.removePendingMedia(localId: Int): Unit — deletes staged file
PatientCaseViewModel.setPrimaryMedia(localId: Int): Unit — marks staged primary media
PatientCaseViewModel.startRecording(caseId: Long): Unit — records temp audio file
PatientCaseViewModel.stopRecording(): Unit — stages recorded audio
PatientCaseViewModel.cancelRecording(): Unit — discards active audio
PatientCaseViewModel.save(linkShiftId: Long?, linkSessionId: Long?): Unit — persists patient/case/media
PatientCaseViewModel.onCleared(): Unit — deletes unsaved temp media
PatientCaseScreen(linkShiftId: Long?, linkSessionId: Long?, editCaseId: Long?, onNavigateBack: () -> Unit): Unit — entry/edit patient-case shell
NewPatientTab(state: PatientCaseUiState, mediaFileManager: MediaFileManager, ...): Unit — renders full patient/case form
ExistingPatientTab(state: PatientCaseUiState, onQueryChange: (String) -> Unit, onSelectPatient: (Patient) -> Unit): Unit — renders patient picker
CaseDetailViewModel.softDelete(onDeleted: () -> Unit): Unit — trashes current case
CaseDetailViewModel.deleteMedia(mediaId: Long): Unit — removes media from detail
CaseDetailViewModel.setPrimaryMedia(mediaId: Long): Unit — promotes saved media
CaseDetailViewModel.exportPdf(): Unit — creates shareable PDF
CaseDetailScreen(onNavigateBack: () -> Unit, onEditCase: (Long) -> Unit, onNavigateToCaseFeed: (Long,String) -> Unit, onOpenImageViewer: (Long,Int) -> Unit): Unit — renders case detail/actions
ImageViewerScreen(initialIndex: Int, onNavigateBack: () -> Unit): Unit — pages visual media full-screen
DiagnosisBrowseViewModel.setQuery(q: String): Unit — filters diagnosis list locally
DiagnosisBrowseViewModel.setSortMode(mode: DiagnosisSortMode): Unit — switches repository sort
DiagnosisBrowseViewModel.addDiagnosis(name: String): Unit — creates diagnosis and clears query
DiagnosisBrowseScreen(onNavigateToCaseFeed: (Long,String) -> Unit, onAddCase: () -> Unit): Unit — browses diagnosis index
CaseFeedScreen(onNavigateBack: () -> Unit, onCaseClick: (Long) -> Unit): Unit — lists cases for route diagnosis
ConsultationViewModel.selectDate(date: LocalDate): Unit — accepts configured weekdays only
ConsultationViewModel.selectPreviousConsultationDate(): Unit — jumps to prior consult day
ConsultationViewModel.selectNextConsultationDate(): Unit — jumps to next consult day
ConsultationViewModel.getOrCreateSessionForSelected(onReady: (Long) -> Unit): Unit — creates session before navigation
ConsultationCalendarScreen(onAddPatient: (Long) -> Unit, onCaseClick: (Long) -> Unit): Unit — displays consult-day timeline
nextMilestone(currentCases: Int): Int — 10/50/100 step progression
previousMilestone(currentCases: Int): Int — computes previous milestone floor
DashboardScreen(onCaseClick: (Long) -> Unit, onSearchClick: () -> Unit): Unit — renders dashboard overview
SearchViewModel.setQuery(value: String): Unit — updates debounced query
SearchViewModel.clearQuery(): Unit — clears search input
SearchScreen(onNavigateBack: () -> Unit, onCaseClick: (Long) -> Unit): Unit — focused global search surface
SettingsViewModel.setConsultationDay(day: DayOfWeek): Job — persists consult weekday
SettingsViewModel.setTheme(mode: ThemeMode): Job — persists theme mode
SettingsViewModel.setDiagnosisSort(mode: DiagnosisSortMode): Job — persists default diagnosis sort
SettingsViewModel.setBackupFolder(uri: String?): Job — persists SAF tree URI
SettingsViewModel.setBackupSchedule(schedule: BackupSchedule): Job — persists WorkManager schedule
SettingsViewModel.exportNow(): Unit — runs manual backup
SettingsViewModel.restoreBackup(zipUri: String): Unit — restores selected zip
SettingsViewModel.vacuumDatabase(): Unit — optimizes database file
SettingsScreen(onNavigateToTrash: () -> Unit): Unit — renders settings/backup/trash entry
TrashViewModel.restorePatient(id: Long): Job — restores trashed patient
TrashViewModel.restoreCase(id: Long): Job — restores trashed case
TrashViewModel.restoreShift(id: Long): Job — restores trashed shift
TrashViewModel.restoreSession(id: Long): Job — restores trashed session
TrashScreen(onNavigateBack: () -> Unit): Unit — renders grouped trash restore UI
ShiftsViewModel.addShift(date: Long, label: String?, onCreated: (Long) -> Unit): Unit — creates shift then navigates
ShiftsViewModel.softDelete(shift: Shift): Unit — trashes shift for undo
ShiftsViewModel.undoDelete(): Unit — restores recent shift
ShiftsListScreen(onAddPatient: () -> Unit, onNavigateToDetail: (Long) -> Unit): Unit — lists shifts with undo delete
ShiftDetailViewModel.deleteShift(onDeleted: () -> Unit): Unit — trashes current shift
ShiftDetailViewModel.unlinkCase(caseId: Long): Unit — removes case from shift
ShiftDetailScreen(onNavigateBack: () -> Unit, onAddPatient: (Long) -> Unit, onCaseClick: (Long) -> Unit): Unit — renders one shift's cases
AddShiftDialog(onDismiss: () -> Unit, onConfirm: (Long,String?) -> Unit): Unit — captures shift date/label
## Conventions
Error handling — ViewModel operations expose user messages through state; PatientCaseViewModel.save sets error, SettingsViewModel.exportNow sets BackupUiState.lastMessage.
Worker failure handling — scheduled jobs return Result.failure without retry loops; ScheduledBackupWorker relies on next periodic run.
Data safety — repository writes, backup, restore, purge, and vacuum use DataSafetyCoordinator.withDataLock; nested calls are reentrant via coroutine context element.
Data access boundary — features import core.repository interfaces; data imports Room and binds implementations in RepositoryModule.
Transactions — multi-table writes use db.withTransaction; CaseRepositoryImpl.upsertCase saves case, replaces diagnosis links, adds shift/session links.
Soft delete — active list/feed/search queries filter is_deleted=0; trash screens observe is_deleted=1 and restore clears deleted_at.
Hard purge — TrashPurgeWorker collects media paths, hard-deletes cases, purges other old trash, deletes orphan diagnoses, then deletes files.
Media paths — DB stores relative paths; CaseRepositoryImpl.resolveMediaPaths converts to absolute paths before UI rendering.
Settings persistence — enums are stored by name; missing/invalid values fall back to AppSettings defaults in PreferencesStore.
Navigation args — optional Long route args use -1L sentinel then takeIf { it != -1L } in KairosNavHost.
Route naming — top-level routes are plain lowercase strings; detail routes embed required IDs and optional query args.
Async state — most ViewModels expose StateFlow via stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), initial).
UI collection — screens use collectAsStateWithLifecycle rather than raw collect in composables.
Search strategy — SearchViewModel debounces 250ms; SearchRepositoryImpl anchors SQL LIKE on longest token and Kotlin-filters all tokens.
Diagnosis creation — getOrCreate trims input and uses case-insensitive lookup before insert.
Patient phone updates — PatientRepositoryImpl deletes all existing phones and reinserts current list on every upsert.
Auth — no user/session/auth layer; app is local-only even though sync columns exist.
Permissions — NewPatientTab requests CAMERA before TakePicture/CaptureVideo and RECORD_AUDIO before AudioRecorderModal.
SAF permissions — SettingsScreen calls takePersistableUriPermission for backup folder tree URIs.
PDF sharing — CaseDetailScreen shares cache PDF through FileProvider with ClipData and FLAG_GRANT_READ_URI_PERMISSION.
Naming — Room types use Entity/Dao/CrossRef/WithRelations; data implementations use RepositoryImpl; UI files use ViewModel/Screen suffixes.
Date storage — dates are epoch millis; ConsultationViewModel normalizes LocalDate at system-default start of day.
Verification — README names .\gradlew.bat assembleDebug as default full build check.
## State & Gotchas
- No test source files are present; verification is currently build/compile oriented.
- app/src/main/res/drawable/ic_launcher_foreground.xml still marks launcher art as placeholder.
- Sync fields remote_id/sync_state/last_synced_at exist in entities but no network sync/auth/merge engine exists.
- Room schema is version 1 and Migrations.ALL_MIGRATIONS is empty; any entity change needs version bump plus explicit migration.
- Backup format version is 2 while Room schema version is 1; do not conflate manifest format with DB version.
- Android allowBackup=false is intentional because backups are custom zip exports.
- WorkManager default initializer is intentionally disabled so KairosApplication can provide HiltWorkerFactory.
- Navigation transitions are intentionally EnterTransition.None/ExitTransition.None for faster screen changes.
- CaseRepositoryImpl.getById/observeById and PatientDao/ShiftDao direct get/observe methods do not filter deleted rows; list/feed/search queries do.
- ConsultationSessionDao.findByDate ignores is_deleted; getOrCreateForDate restores a soft-deleted session before returning its id.
- Dashboard period counts use cases.created_at, not case_date; backdated cases count in the creation period.
- ConsultationViewModel date range is fixed at VM creation: today.minusYears(1) through today.plusYears(1).
- PatientCaseViewModel.loadCase does not hydrate existing media into pendingMedia; edit form can add media but not show/remove current attachments.
- NewPatientTab rich editor only resets when state.notesHtml is empty; non-empty existing notes may not hydrate into editor.
- CaseRepositoryImpl.upsertCase replaces diagnosis links but never clears media; existing saved media survive case edits.
- MediaRepository.observeForCase returns relative paths; CaseRepositoryImpl is the path-resolving read path used by UI.
- Pending camera/gallery/audio files are created under cases/0 and backup export explicitly excludes cases/0/.
- PatientCaseViewModel.onCleared deletes pending media, but app/process death before onCleared can leave unbacked cases/0 files.
- Existing-patient case mode locks patient fields; new case creation for selected patient does not update patient demographics.
- PatientRepositoryImpl.upsert rewrites all phones, so omitted phones are deleted.
- DiagnosisEntity unique index is binary while getOrCreate lookup is case-insensitive; app lock prevents normal duplicate entry but DB uniqueness is not case-insensitive.
- DiagnosisDao.deleteOrphaned removes diagnoses only after hard delete cascades remove case_diagnoses; soft-deleted cases still keep diagnoses referenced.
- ScheduledBackupWorker requires settings.backupFolderUri; missing URI returns failure and waits for next scheduled run.
- ScheduledBackupWorker records backup result only after BackupEngine.export returns; exceptions before that may leave last-run unchanged.
- BackupEngine.restore closes Room and SettingsScreen prompts restart; live UI should not be expected to use restored DB safely before restart.
- BackupEngine validates zip entry paths, entry count, max entry size 10GB, max backup size 50GB, checksums, quick_check, foreign_key_check, and user_version.
- Backup export includes DataStore prefs file if present; restore overwrites it from zip and prompts restart.
- Trash purge hard-deletes cases first, then patients/shifts/sessions older than 30 days, then orphan diagnoses, then media files.
- PatientDao.purgeOlderThan keeps deleted patients that still have active cases.
- ShiftDetailScreen long-press on CaseCard unlinks the case from the shift; ShiftsListScreen long-press on a shift soft-deletes it.
- CasePdfExporter exports patient/case fields, plain-text notes, and image media only; audio/video are omitted.
- ImageViewerScreen saveToGallery writes through MediaStore and does not request legacy external storage permission.
- CaseDetailScreen renders notes as plain text from HTML; rich formatting is not shown in detail view.
