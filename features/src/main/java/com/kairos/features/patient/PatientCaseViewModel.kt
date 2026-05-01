package com.kairos.features.patient

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kairos.core.media.AudioRecorderEngine
import com.kairos.core.media.MediaFileManager
import com.kairos.core.model.Case
import com.kairos.core.model.Diagnosis
import com.kairos.core.model.MediaItem
import com.kairos.core.model.MediaType
import com.kairos.core.model.Patient
import com.kairos.core.model.PatientPhone
import com.kairos.core.repository.CaseRepository
import com.kairos.core.repository.DiagnosisRepository
import com.kairos.core.repository.MediaRepository
import com.kairos.core.repository.PatientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

// ── Pending media (held in dialog state before the case is saved) ────────────
data class PendingMedia(
    val localId: Int,       // VM-assigned id for list keying
    val sourceFile: File,   // already written on disk (temp path or camera output)
    val mediaType: MediaType,
    val durationMs: Long? = null,
    val isPrimary: Boolean = false,
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
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class PatientCaseViewModel @Inject constructor(
    private val patientRepo: PatientRepository,
    private val caseRepo: CaseRepository,
    private val diagnosisRepo: DiagnosisRepository,
    private val mediaRepo: MediaRepository,
    val mediaFileManager: MediaFileManager,
    private val audioEngine: AudioRecorderEngine,
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

    private var nextLocalId = 0

    /** Pre-fill form from an existing case for editing. */
    fun loadCase(caseId: Long) {
        if (caseId <= 0L || editingCaseId == caseId) return
        editingCaseId = caseId
        viewModelScope.launch {
            val case = caseRepo.getById(caseId) ?: return@launch
            val patient = case.patient
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
                )
            }
        }
    }
    private var recordingStartMs = 0L
    private var timerJob: Job? = null

    // ── Field updates ──────────────────────────────────────────────────────

    fun setName(v: String) = _state.update { it.copy(name = v) }
    fun setAge(v: String) = _state.update { it.copy(age = v) }
    fun setCaseDate(ms: Long) = _state.update { it.copy(caseDate = ms) }
    fun setMechanism(v: String) = _state.update { it.copy(mechanism = v) }
    fun setNotes(html: String) = _state.update { it.copy(notesHtml = html) }

    fun addPhone(number: String, label: String?) {
        if (number.isBlank()) return
        _state.update {
            it.copy(phones = it.phones + PatientPhone(number = number.trim(), label = label))
        }
    }

    fun removePhone(phone: PatientPhone) =
        _state.update { it.copy(phones = it.phones - phone) }

    // ── Diagnosis autocomplete ─────────────────────────────────────────────

    fun setDiagnosisQuery(q: String) {
        _state.update { it.copy(diagnosisQuery = q) }
        if (q.length >= 1) {
            viewModelScope.launch {
                val results = diagnosisRepo.searchByPrefix(q, limit = 8)
                _state.update { it.copy(diagnosisSuggestions = results) }
            }
        } else {
            _state.update { it.copy(diagnosisSuggestions = emptyList()) }
        }
    }

    fun selectDiagnosis(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        _state.update { s ->
            if (s.diagnoses.any { it.equals(trimmed, ignoreCase = true) }) s
            else s.copy(
                diagnoses = s.diagnoses + trimmed,
                diagnosisQuery = "",
                diagnosisSuggestions = emptyList(),
            )
        }
    }

    fun removeDiagnosis(name: String) =
        _state.update { it.copy(diagnoses = it.diagnoses - name) }

    // ── Existing patient tab ───────────────────────────────────────────────

    fun setSearchQuery(q: String) {
        _state.update { it.copy(searchQuery = q) }
        _searchQuery.value = q
    }

    fun selectExistingPatient(patient: Patient) =
        _state.update { it.copy(selectedPatient = patient) }

    fun clearSelectedPatient() =
        _state.update { it.copy(selectedPatient = null) }

    // ── Media attachment ───────────────────────────────────────────────────

    /**
     * Called after camera/gallery returns a file already written to disk.
     * [sourceFile] is the temp file (e.g. from FileProvider or copied from gallery URI).
     */
    fun attachFile(sourceFile: File, type: MediaType) {
        val item = PendingMedia(
            localId = nextLocalId++,
            sourceFile = sourceFile,
            mediaType = type,
            isPrimary = _state.value.pendingMedia.isEmpty(),
        )
        _state.update { it.copy(pendingMedia = it.pendingMedia + item) }
    }

    fun removePendingMedia(localId: Int) {
        _state.update { s ->
            val item = s.pendingMedia.find { it.localId == localId }
            item?.sourceFile?.delete()
            val remaining = s.pendingMedia.filter { it.localId != localId }
            val fixed = if (remaining.isNotEmpty() && remaining.none { it.isPrimary }) {
                remaining.mapIndexed { i, m -> if (i == 0) m.copy(isPrimary = true) else m }
            } else remaining
            s.copy(pendingMedia = fixed)
        }
    }

    fun setPrimaryMedia(localId: Int) {
        _state.update { s ->
            s.copy(pendingMedia = s.pendingMedia.map { m ->
                m.copy(isPrimary = m.localId == localId)
            })
        }
    }

    // ── Audio recording ────────────────────────────────────────────────────

    fun startRecording(caseId: Long = 0L) {
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
        if (s.isSaving) return

        val patientToUse = s.selectedPatient
        val isNewPatient = patientToUse == null

        if (isNewPatient && s.name.isBlank()) {
            _state.update { it.copy(error = "Patient name is required") }
            return
        }

        _state.update { it.copy(isSaving = true, error = null) }

        viewModelScope.launch {
            try {
                val now = System.currentTimeMillis()

                // 1. Patient
                val patientId: Long = if (isNewPatient) {
                    patientRepo.upsert(
                        Patient(
                            name = s.name.trim(),
                            age = s.age.toIntOrNull(),
                            phones = s.phones,
                            createdAt = now,
                            updatedAt = now,
                        )
                    )
                } else {
                    patientToUse?.id ?: run {
                        _state.update { it.copy(isSaving = false, error = "No patient selected") }
                        return@launch
                    }
                }

                // 2. Case (id = 0 → insert, id > 0 → update)
                val caseId = caseRepo.upsertCase(
                    case = Case(
                        id = editingCaseId,
                        patientId = patientId,
                        caseDate = s.caseDate,
                        mechanism = s.mechanism.trim().ifEmpty { null },
                        notesHtml = s.notesHtml.ifEmpty { null },
                        createdAt = now,
                        updatedAt = now,
                    ),
                    diagnosisNames = s.diagnoses,
                    linkShiftId = linkShiftId,
                    linkSessionId = linkSessionId,
                )

                // 3. Media — move temp files to final location and insert records
                s.pendingMedia.forEach { pending ->
                    val finalFile = mediaFileManager.newCaseMediaFile(caseId, pending.mediaType)
                    pending.sourceFile.copyTo(finalFile, overwrite = true)
                    pending.sourceFile.delete()

                    mediaRepo.add(
                        MediaItem(
                            caseId = caseId,
                            filePath = mediaFileManager.toRelative(finalFile),
                            mediaType = pending.mediaType,
                            durationMs = pending.durationMs,
                            isPrimary = pending.isPrimary,
                            createdAt = now,
                        )
                    )
                }

                _state.update { it.copy(isSaving = false, saved = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false, error = e.message ?: "Save failed") }
            }
        }
    }

    fun clearError() = _state.update { it.copy(error = null) }

    override fun onCleared() {
        // Clean up any un-committed temp media
        cancelRecording()
        _state.value.pendingMedia.forEach { it.sourceFile.delete() }
        super.onCleared()
    }
}
