package com.taha.kairos.features.cases

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taha.kairos.core.model.Diagnosis
import com.taha.kairos.core.model.DiagnosisSortMode
import com.taha.kairos.core.repository.DiagnosisRepository
import com.taha.kairos.core.repository.DiagnosisRenameResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DiagnosisRenameUiState(
    val diagnosisId: Long,
    val name: String,
    val isSaving: Boolean = false,
    val error: String? = null,
)

data class DiagnosisBrowseUiState(
    val diagnoses: List<Diagnosis> = emptyList(),
    val searchQuery: String = "",
    val sortMode: DiagnosisSortMode = DiagnosisSortMode.ALPHABETICAL,
    val isLoading: Boolean = true,
    val rename: DiagnosisRenameUiState? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DiagnosisBrowseViewModel @Inject constructor(
    private val diagnosisRepo: DiagnosisRepository,
) : ViewModel() {

    private val _sortMode = MutableStateFlow(DiagnosisSortMode.ALPHABETICAL)
    private val _query = MutableStateFlow("")
    private val _rename = MutableStateFlow<DiagnosisRenameUiState?>(null)

    val ui: StateFlow<DiagnosisBrowseUiState> = combine(
        _sortMode.flatMapLatest { sort -> diagnosisRepo.observeAll(sort) },
        _query,
        _sortMode,
        _rename,
    ) { all, query, sort, rename ->
        val filtered = if (query.isBlank()) all
        else all.filter { it.name.contains(query, ignoreCase = true) }
        DiagnosisBrowseUiState(
            diagnoses = filtered,
            searchQuery = query,
            sortMode = sort,
            isLoading = false,
            rename = rename,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DiagnosisBrowseUiState(),
    )

    fun setQuery(q: String) = _query.update { q }

    fun setSortMode(mode: DiagnosisSortMode) = _sortMode.update { mode }

    fun addDiagnosis(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch {
            diagnosisRepo.getOrCreate(trimmed)
            _query.update { "" }
        }
    }

    fun startRenaming(diagnosis: Diagnosis) {
        _rename.value = DiagnosisRenameUiState(
            diagnosisId = diagnosis.id,
            name = diagnosis.name,
        )
    }

    fun updateRenameName(name: String) {
        _rename.update { current ->
            current?.takeUnless { it.isSaving }?.copy(name = name, error = null) ?: current
        }
    }

    fun dismissRename() {
        _rename.update { current -> if (current?.isSaving == true) current else null }
    }

    fun confirmRename() {
        val editor = _rename.value ?: return
        val trimmed = editor.name.trim()
        if (trimmed.isEmpty()) {
            _rename.update { current ->
                current?.takeIf { it.diagnosisId == editor.diagnosisId }
                    ?.copy(error = "Diagnosis name is required")
            }
            return
        }

        _rename.update { current ->
            current?.takeIf { it.diagnosisId == editor.diagnosisId }
                ?.copy(name = trimmed, isSaving = true, error = null)
        }
        viewModelScope.launch {
            val result = try {
                diagnosisRepo.rename(editor.diagnosisId, trimmed)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                null
            }

            _rename.update { current ->
                if (current?.diagnosisId != editor.diagnosisId) {
                    current
                } else {
                    when (result) {
                        DiagnosisRenameResult.RENAMED,
                        DiagnosisRenameResult.UNCHANGED,
                        -> null

                        DiagnosisRenameResult.BLANK_NAME ->
                            current.copy(isSaving = false, error = "Diagnosis name is required")

                        DiagnosisRenameResult.ALREADY_EXISTS ->
                            current.copy(
                                isSaving = false,
                                error = "A diagnosis with this name already exists",
                            )

                        DiagnosisRenameResult.NOT_FOUND ->
                            current.copy(
                                isSaving = false,
                                error = "This diagnosis no longer exists",
                            )

                        null ->
                            current.copy(
                                isSaving = false,
                                error = "Couldn't rename diagnosis",
                            )
                    }
                }
            }
        }
    }
}
