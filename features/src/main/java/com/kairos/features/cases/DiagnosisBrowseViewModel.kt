package com.kairos.features.cases

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kairos.core.model.Diagnosis
import com.kairos.core.model.DiagnosisSortMode
import com.kairos.core.repository.DiagnosisRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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

data class DiagnosisBrowseUiState(
    val diagnoses: List<Diagnosis> = emptyList(),
    val searchQuery: String = "",
    val sortMode: DiagnosisSortMode = DiagnosisSortMode.ALPHABETICAL,
    val isLoading: Boolean = true,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DiagnosisBrowseViewModel @Inject constructor(
    private val diagnosisRepo: DiagnosisRepository,
) : ViewModel() {

    private val _sortMode = MutableStateFlow(DiagnosisSortMode.ALPHABETICAL)
    private val _query = MutableStateFlow("")

    val ui: StateFlow<DiagnosisBrowseUiState> = combine(
        _sortMode.flatMapLatest { sort -> diagnosisRepo.observeAll(sort) },
        _query,
        _sortMode,
    ) { all, query, sort ->
        val filtered = if (query.isBlank()) all
        else all.filter { it.name.contains(query, ignoreCase = true) }
        DiagnosisBrowseUiState(
            diagnoses = filtered,
            searchQuery = query,
            sortMode = sort,
            isLoading = false,
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
}
