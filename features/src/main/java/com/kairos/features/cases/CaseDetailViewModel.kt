package com.kairos.features.cases

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kairos.core.model.Case
import com.kairos.core.repository.CaseRepository
import com.kairos.core.repository.MediaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CaseDetailUiState(
    val case: Case? = null,
    val isLoading: Boolean = true,
    val isError: Boolean = false,
)

@HiltViewModel
class CaseDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val caseRepo: CaseRepository,
    private val mediaRepo: MediaRepository,
) : ViewModel() {

    private val caseId: Long = savedStateHandle.get<Long>("caseId") ?: run {
        // Log error and use -1 as sentinel; the UI will show empty/error state
        -1L
    }

    val ui: StateFlow<CaseDetailUiState> = if (caseId == -1L) {
        flowOf(CaseDetailUiState(isLoading = false, isError = true))
    } else {
        caseRepo
            .observeById(caseId)
            .map { c -> CaseDetailUiState(case = c, isLoading = false) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CaseDetailUiState())

    fun softDelete(onDeleted: () -> Unit) {
        viewModelScope.launch {
            caseRepo.softDelete(caseId)
            onDeleted()
        }
    }

    fun deleteMedia(mediaId: Long) {
        viewModelScope.launch { mediaRepo.delete(mediaId) }
    }

    fun setPrimaryMedia(mediaId: Long) {
        viewModelScope.launch { mediaRepo.setPrimary(caseId, mediaId) }
    }
}
