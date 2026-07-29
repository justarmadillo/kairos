package com.taha.kairos.features.patient

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taha.kairos.core.media.AudioRecorderEngine
import com.taha.kairos.core.media.MediaFileManager
import com.taha.kairos.core.model.Case
import com.taha.kairos.core.model.Diagnosis
import com.taha.kairos.core.model.MediaItem
import com.taha.kairos.core.model.MediaType
import com.taha.kairos.core.model.Patient
import com.taha.kairos.core.model.PatientPhone
import com.taha.kairos.core.model.toCapitalizedPatientName
import com.taha.kairos.core.repository.CaseRepository
import com.taha.kairos.core.repository.DataSafetyCoordinator
import com.taha.kairos.core.repository.DiagnosisRepository
import com.taha.kairos.core.repository.MediaRepository
import com.taha.kairos.core.repository.PatientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

// ── Pending media (held in dialog state before the case is saved) ────────────
data class PendingMedia(
    val localId: Int,       // VM-assigned id for list keying
    val sourceFile: File,   // already written on disk (temp path or camera output)
    val mediaType: MediaType,
    val durationMs: Long? = null,
    val isPrimary: Boolean = false,
    val originalFileName: String? = null,
)

/** Saved attachment shown in the edit form with a UI-only stable identifier. */
data class ExistingMedia(
    val localId: Int,
    val item: MediaItem,
)

// ── UI State ─────────────────────────────────────────────────────────────────
data class PatientCaseUiState(
    // form fields
    val name: String = "",
    val age: String = "",
    val phones: List<PatientPhone> = emptyList(),
    val caseDate: Long = System.currentTimeMillis(),
    val mechanism: String = "",
    val notesHtml: String = "",
    val diagnoses: List<String> = emptyList(),
    val existingMedia: List<ExistingMedia> = emptyList(),
    val pendingMedia: List<PendingMedia> = emptyList(),

    // diagnosis autocomplete
    val diagnosisQuery: String = "",
    val diagnosisSuggestions: List<Diagnosis> = emptyList(),

    // existing-patient search
    val searchQuery: String = "",
    val searchResults: List<Patient> = emptyList(),
    val selectedPatient: Patient? = null,

    // recording
    val isRecording: Boolean = false,
    val recordingElapsedMs: Long = 0L,
    val pendingAudioFile: File? = null,

    // submission
    val isLoadingCase: Boolean = false,
    val caseLoadFailed: Boolean = false,
    val isImportingMedia: Boolean = false,
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PatientCaseViewModel @Inject constructor(
    private val patientRepo: PatientRepository,
    private val caseRepo: CaseRepository,
    private val diagnosisRepo: DiagnosisRepository,
    private val mediaRepo: MediaRepository,
    val mediaFileManager: MediaFileManager,
    private val audioEngine: AudioRecorderEngine,
    private val dataSafetyCoordinator: DataSafetyCoordinator,
) : ViewModel() {

    private val _state = MutableStateFlow(PatientCaseUiState())
    val state: StateFlow<PatientCaseUiState> = _state.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        viewModelScope.launch {
            _searchQuery
                .flatMapLatest { q ->
                    if (q.isBlank()) flowOf(emptyList())
                    else patientRepo.search(q)
                }
                .collect { results ->
                    _state.update { it.copy(searchResults = results) }
                }
        }
    }

    // When editing an existing case, store the original id
    private var editingCaseId: Long = 0L
    private var editingCase: Case? = null
    private var loadingCaseId: Long = 0L
    private var persistedPatientForRetry: Patient? = null
    private val removedExistingMediaIds = mutableSetOf<Long>()

    private var nextLocalId = 0

    /** Pre-fill form from an existing case for editing. */
    fun loadCase(caseId: Long) {
        if (caseId <= 0L || editingCaseId == caseId || loadingCaseId == caseId) return
        loadingCaseId = caseId
        _state.update {
            it.copy(
                isLoadingCase = true,
                caseLoadFailed = false,
                error = null,
            )
        }
        viewModelScope.launch {
            try {
                val case = caseRepo.getById(caseId)
                    ?: error("Case could not be loaded")
                editingCaseId = caseId
                editingCase = case
                persistedPatientForRetry = case.patient
                removedExistingMediaIds.clear()
                val patient = case.patient
                val existingMedia = case.media.map { item ->
                    ExistingMedia(localId = nextLocalId++, item = item)
                }
                _state.update { s ->
                    s.copy(
                        name = patient?.name ?: "",
                        age = patient?.age?.toString() ?: "",
                        phones = patient?.phones ?: emptyList(),
                        caseDate = case.caseDate,
                        mechanism = case.mechanism ?: "",
                        notesHtml = case.notesHtml ?: "",
                        diagnoses = case.diagnoses.map { it.name },
                        selectedPatient = patient,
                        existingMedia = existingMedia,
                        isLoadingCase = false,
                        caseLoadFailed = false,
                    ).ensureVisualPrimary()
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                _state.update {
                    it.copy(
                        isLoadingCase = false,
                        caseLoadFailed = true,
                        error = "Case could not be loaded",
                    )
                }
            } finally {
                loadingCaseId = 0L
            }
        }
    }
    private var recordingStartMs = 0L
    private var timerJob: Job? = null

    // ── Field updates ──────────────────────────────────────────────────────

    fun setName(v: String) =
        updateEditable { it.copy(name = v.toCapitalizedPatientName()) }
    fun setAge(v: String) = updateEditable { it.copy(age = v) }
    fun setCaseDate(ms: Long) = updateEditable { it.copy(caseDate = ms) }
    fun setMechanism(v: String) = updateEditable { it.copy(mechanism = v) }
    fun setNotes(html: String) = updateEditable { it.copy(notesHtml = html) }

    fun addPhone(number: String, label: String?) {
        if (number.isBlank() || !isEditable()) return
        updateEditable {
            it.copy(phones = it.phones + PatientPhone(number = number.trim(), label = label))
        }
    }

    fun removePhone(phone: PatientPhone) =
        updateEditable { it.copy(phones = it.phones - phone) }

    // ── Diagnosis autocomplete ─────────────────────────────────────────────

    fun setDiagnosisQuery(q: String) {
        if (!isEditable()) return
        updateEditable { it.copy(diagnosisQuery = q) }
        if (q.length >= 1) {
            viewModelScope.launch {
                val results = diagnosisRepo.searchByPrefix(q, limit = 8)
                updateEditable { it.copy(diagnosisSuggestions = results) }
            }
        } else {
            updateEditable { it.copy(diagnosisSuggestions = emptyList()) }
        }
    }

    fun selectDiagnosis(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty() || !isEditable()) return
        updateEditable { s ->
            if (s.diagnoses.any { it.equals(trimmed, ignoreCase = true) }) s
            else s.copy(
                diagnoses = s.diagnoses + trimmed,
                diagnosisQuery = "",
                diagnosisSuggestions = emptyList(),
            )
        }
    }

    fun removeDiagnosis(name: String) =
        updateEditable { it.copy(diagnoses = it.diagnoses - name) }

    // ── Existing patient tab ───────────────────────────────────────────────

    fun setSearchQuery(q: String) {
        if (!isEditable()) return
        updateEditable { it.copy(searchQuery = q) }
        _searchQuery.value = q
    }

    fun selectExistingPatient(patient: Patient) =
        updateEditable {
            it.copy(
                name = patient.name,
                age = patient.age?.toString() ?: "",
                phones = patient.phones,
                selectedPatient = patient,
            )
        }

    fun clearSelectedPatient() =
        updateEditable { it.copy(selectedPatient = null) }

    // ── Media attachment ───────────────────────────────────────────────────

    /**
     * Called after camera/gallery returns a file already written to disk.
     * [sourceFile] is the temp file (e.g. from FileProvider or copied from gallery URI).
     */
    fun attachFile(sourceFile: File, type: MediaType, originalFileName: String? = null) {
        if (!isEditable()) {
            runCatching { sourceFile.delete() }
            return
        }
        val hasVisualAttachment = _state.value.existingMedia.any { it.item.mediaType.isVisual() } ||
            _state.value.pendingMedia.any { it.mediaType.isVisual() }
        val item = PendingMedia(
            localId = nextLocalId++,
            sourceFile = sourceFile,
            mediaType = type,
            isPrimary = type.isVisual() && !hasVisualAttachment,
            originalFileName = originalFileName,
        )
        updateEditable {
            it.copy(pendingMedia = it.pendingMedia + item).ensureVisualPrimary()
        }
    }

    /**
     * Removes a pending attachment immediately, but only stages deletion of saved media.
     * Saved files are not touched until the user presses Save, so closing the editor is safe.
     */
    fun removeMedia(localId: Int) {
        if (!isEditable()) return
        val existing = _state.value.existingMedia.firstOrNull { it.localId == localId }
        if (existing != null) {
            removedExistingMediaIds += existing.item.id
            _state.update { state ->
                state.copy(
                    existingMedia = state.existingMedia.filterNot { it.localId == localId },
                ).ensureVisualPrimary()
            }
            return
        }

        _state.update { s ->
            val item = s.pendingMedia.find { it.localId == localId }
            if (item == null) return@update s
            item.sourceFile.delete()
            s.copy(
                pendingMedia = s.pendingMedia.filterNot { it.localId == localId },
            ).ensureVisualPrimary()
        }
    }

    fun setPrimaryMedia(localId: Int) {
        updateEditable { s ->
            val targetIsVisual =
                s.existingMedia.any { it.localId == localId && it.item.mediaType.isVisual() } ||
                    s.pendingMedia.any { it.localId == localId && it.mediaType.isVisual() }
            if (!targetIsVisual) {
                s
            } else {
                s.copy(
                    existingMedia = s.existingMedia.map { media ->
                        media.copy(item = media.item.copy(isPrimary = media.localId == localId))
                    },
                    pendingMedia = s.pendingMedia.map { media ->
                        media.copy(isPrimary = media.localId == localId)
                    },
                )
            }
        }
    }

    fun beginMediaImport(): Boolean {
        var started = false
        _state.update { state ->
            if (state.isSaving ||
                state.isLoadingCase ||
                state.caseLoadFailed ||
                state.isImportingMedia
            ) {
                state
            } else {
                started = true
                state.copy(isImportingMedia = true)
            }
        }
        return started
    }

    fun endMediaImport() {
        _state.update { it.copy(isImportingMedia = false) }
    }

    // ── Audio recording ────────────────────────────────────────────────────

    fun startRecording(caseId: Long = 0L) {
        if (!isEditable()) return
        val file = mediaFileManager.newCaseMediaFile(caseId, MediaType.AUDIO)
        audioEngine.start(file)
        recordingStartMs = System.currentTimeMillis()
        _state.update { it.copy(isRecording = true, pendingAudioFile = file, recordingElapsedMs = 0L) }
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(500)
                _state.update { it.copy(recordingElapsedMs = System.currentTimeMillis() - recordingStartMs) }
            }
        }
    }

    fun stopRecording() {
        if (!isEditable()) return
        timerJob?.cancel()
        audioEngine.stop()
        val duration = System.currentTimeMillis() - recordingStartMs
        val file = _state.value.pendingAudioFile ?: return
        _state.update { it.copy(isRecording = false, pendingAudioFile = null) }
        val item = PendingMedia(
            localId = nextLocalId++,
            sourceFile = file,
            mediaType = MediaType.AUDIO,
            durationMs = duration,
        )
        _state.update { it.copy(pendingMedia = it.pendingMedia + item) }
    }

    fun cancelRecording() {
        timerJob?.cancel()
        _state.value.pendingAudioFile?.let { audioEngine.cancel(it) }
        _state.update { it.copy(isRecording = false, pendingAudioFile = null, recordingElapsedMs = 0L) }
    }

    // ── Save ───────────────────────────────────────────────────────────────

    fun save(linkShiftId: Long? = null, linkSessionId: Long? = null) {
        val s = _state.value
        if (s.isSaving || s.isLoadingCase || s.caseLoadFailed || s.isImportingMedia) return
        if (s.isRecording) {
            _state.update { it.copy(error = "Stop recording before saving") }
            return
        }

        val patientToUse = s.selectedPatient
        val caseIdAtStart = editingCaseId
        val existingCaseAtStart = editingCase
        val retryPatientAtStart = persistedPatientForRetry
        val removedMediaIds = removedExistingMediaIds.toSet()
        val isEditingCase = caseIdAtStart > 0L
        val isNewPatient = patientToUse == null
        val normalizedPatientName = s.name.trim().toCapitalizedPatientName()

        if ((isNewPatient || isEditingCase) && normalizedPatientName.isBlank()) {
            _state.update { it.copy(error = "Patient name is required") }
            return
        }

        _state.update { it.copy(isSaving = true, error = null) }

        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO + NonCancellable) {
                    dataSafetyCoordinator.withDataLock {
                        val now = System.currentTimeMillis()

                        // 1. Patient. Keep the successfully persisted model so
                        // a media failure can retry the same aggregate IDs.
                        val patientToPersist = when {
                            isEditingCase || retryPatientAtStart != null -> {
                                val patient = patientToUse
                                    ?: retryPatientAtStart
                                    ?: existingCaseAtStart?.patient
                                    ?: error("No patient selected")
                                patient.copy(
                                    name = normalizedPatientName,
                                    age = s.age.toIntOrNull(),
                                    phones = s.phones,
                                    updatedAt = now,
                                )
                            }

                            isNewPatient -> {
                                Patient(
                                    name = normalizedPatientName,
                                    age = s.age.toIntOrNull(),
                                    phones = s.phones,
                                    createdAt = now,
                                    updatedAt = now,
                                )
                            }

                            else -> null
                        }
                        val patientId = patientToPersist?.let { patient ->
                            patientRepo.upsert(patient).also { persistedId ->
                                persistedPatientForRetry = patient.copy(id = persistedId)
                            }
                        } ?: patientToUse?.id ?: error("No patient selected")

                        // 2. Case (id = 0 -> insert, id > 0 -> update)
                        val caseToPersist = Case(
                            id = caseIdAtStart,
                            patientId = patientId,
                            caseDate = s.caseDate,
                            mechanism = s.mechanism.trim().ifEmpty { null },
                            notesHtml = s.notesHtml.ifEmpty { null },
                            createdAt = existingCaseAtStart?.createdAt ?: now,
                            updatedAt = now,
                        )
                        val caseId = caseRepo.upsertCase(
                            case = caseToPersist,
                            diagnosisNames = s.diagnoses,
                            linkShiftId = linkShiftId,
                            linkSessionId = linkSessionId,
                        )
                        editingCaseId = caseId
                        editingCase = caseToPersist.copy(
                            id = caseId,
                            patient = persistedPatientForRetry ?: patientToUse,
                        )

                        // 3. Media: prepare final files, then commit all attachment
                        // rows/removals/primary selection in one database transaction.
                        val existingPrimaryId = s.existingMedia
                            .firstOrNull { it.item.isPrimary }
                            ?.item
                            ?.id
                        val preparedFiles = mutableListOf<File>()
                        try {
                            val additions = s.pendingMedia.map { pending ->
                                val ext = pending.originalFileName
                                    ?.substringAfterLast('.', "")
                                    ?.takeIf { it.isNotEmpty() }
                                val finalFile = mediaFileManager.newCaseMediaFile(
                                    caseId = caseId,
                                    type = pending.mediaType,
                                    originalExtension = ext,
                                )
                                preparedFiles += finalFile
                                pending.sourceFile.copyTo(finalFile, overwrite = true)
                                MediaItem(
                                    caseId = caseId,
                                    filePath = mediaFileManager.toRelative(finalFile),
                                    mediaType = pending.mediaType,
                                    durationMs = pending.durationMs,
                                    isPrimary = pending.isPrimary,
                                    originalFileName = pending.originalFileName,
                                    createdAt = now,
                                )
                            }

                            mediaRepo.applyCaseEdits(
                                caseId = caseId,
                                additions = additions,
                                removedIds = removedMediaIds,
                                existingPrimaryId = existingPrimaryId,
                            )
                        } catch (error: Exception) {
                            preparedFiles.forEach { it.delete() }
                            throw error
                        }

                        // Preserve temp sources through the entire commit so a
                        // failed save remains safely retryable.
                        s.pendingMedia.forEach { pending ->
                            runCatching { pending.sourceFile.delete() }
                        }
                    }
                }

                _state.update { it.copy(isSaving = false, saved = true) }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false, error = e.message ?: "Save failed") }
            }
        }
    }

    fun clearError() = _state.update { it.copy(error = null) }

    private fun isEditable(): Boolean =
        !_state.value.isSaving &&
            !_state.value.isLoadingCase &&
            !_state.value.caseLoadFailed

    private fun updateEditable(
        transform: (PatientCaseUiState) -> PatientCaseUiState,
    ) {
        _state.update { state ->
            if (state.isSaving || state.isLoadingCase || state.caseLoadFailed) {
                state
            } else {
                transform(state)
            }
        }
    }

    override fun onCleared() {
        // Clean up any un-committed temp media
        cancelRecording()
        if (!_state.value.isSaving) {
            _state.value.pendingMedia.forEach { it.sourceFile.delete() }
        }
        super.onCleared()
    }

    private fun PatientCaseUiState.ensureVisualPrimary(): PatientCaseUiState {
        val selectedLocalId = existingMedia
            .firstOrNull { it.item.mediaType.isVisual() && it.item.isPrimary }
            ?.localId
            ?: pendingMedia
                .firstOrNull { it.mediaType.isVisual() && it.isPrimary }
                ?.localId
            ?: existingMedia
                .firstOrNull { it.item.mediaType.isVisual() }
                ?.localId
            ?: pendingMedia
                .firstOrNull { it.mediaType.isVisual() }
                ?.localId

        return copy(
            existingMedia = existingMedia.map { media ->
                media.copy(
                    item = media.item.copy(isPrimary = media.localId == selectedLocalId),
                )
            },
            pendingMedia = pendingMedia.map { media ->
                media.copy(isPrimary = media.localId == selectedLocalId)
            },
        )
    }

    private fun MediaType.isVisual(): Boolean =
        this == MediaType.IMAGE || this == MediaType.VIDEO
}
