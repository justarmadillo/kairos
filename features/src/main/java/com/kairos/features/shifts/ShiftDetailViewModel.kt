package com.kairos.features.shifts

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kairos.core.model.Case
import com.kairos.core.model.Shift
import com.kairos.core.repository.CaseRepository
import com.kairos.core.repository.ShiftRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ShiftDetailUiState(
    val shift: Shift? = null,
    val cases: List<Case> = emptyList(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class ShiftDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val shiftRepo: ShiftRepository,
    private val caseRepo: CaseRepository,
) : ViewModel() {

    private val shiftId: Long = savedStateHandle.get<Long>("shiftId") ?: -1L

    private val _ui = MutableStateFlow(ShiftDetailUiState())
    val ui: StateFlow<ShiftDetailUiState> get() = _ui

    init {
        if (shiftId != -1L) {
            viewModelScope.launch {
                shiftRepo.observeById(shiftId).collect { shift ->
                    _ui.update { it.copy(shift = shift, isLoading = false) }
                }
            }
            viewModelScope.launch {
                caseRepo.observeByShift(shiftId).collect { cases ->
                    _ui.update { it.copy(cases = cases) }
                }
            }
        } else {
            _ui.update { it.copy(isLoading = false) }
        }
    }

    fun deleteShift(onDeleted: () -> Unit) {
        viewModelScope.launch {
            shiftRepo.softDelete(shiftId)
            onDeleted()
        }
    }

    fun unlinkCase(caseId: Long) {
        viewModelScope.launch {
            caseRepo.unlinkFromShift(caseId, shiftId)
        }
    }
}
