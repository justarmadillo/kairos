package com.taha.kairos.features.cases

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taha.kairos.core.model.Case
import com.taha.kairos.core.repository.CaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class CaseFeedUiState(
    val cases: List<Case> = emptyList(),
    val diagnosisName: String = "",
    val isLoading: Boolean = true,
)

@HiltViewModel
class CaseFeedViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    caseRepo: CaseRepository,
) : ViewModel() {

    private val diagnosisId: Long = savedStateHandle.get<Long>("diagnosisId") ?: -1L
    private val diagnosisName: String = savedStateHandle["diagnosisName"] ?: ""

    val ui: StateFlow<CaseFeedUiState> = if (diagnosisId == -1L) {
        kotlinx.coroutines.flow.flowOf(CaseFeedUiState(isLoading = false))
    } else {
        caseRepo
            .observeByDiagnosis(diagnosisId)
            .map { cases -> CaseFeedUiState(cases = cases, diagnosisName = diagnosisName, isLoading = false) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CaseFeedUiState(diagnosisName = diagnosisName))
}
