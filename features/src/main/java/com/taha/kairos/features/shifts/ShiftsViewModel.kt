package com.taha.kairos.features.shifts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taha.kairos.core.model.Shift
import com.taha.kairos.core.repository.ShiftRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class ShiftsUiState(
    val shifts: List<Shift> = emptyList(),
    val isLoading: Boolean = true,
    val showAddDialog: Boolean = false,
    val recentlyDeletedShift: Shift? = null,
)

@HiltViewModel
class ShiftsViewModel @Inject constructor(
    private val repo: ShiftRepository,
) : ViewModel() {

    private val _ui = MutableStateFlow(ShiftsUiState())
    val ui: StateFlow<ShiftsUiState> get() = _ui

    init {
        viewModelScope.launch {
            repo.observeAll().collect { list ->
                _ui.update { it.copy(shifts = list, isLoading = false) }
            }
        }
    }

    fun openAddDialog() = _ui.update { it.copy(showAddDialog = true) }
    fun closeAddDialog() = _ui.update { it.copy(showAddDialog = false) }

    /** Returns the new shift id so the caller can navigate to detail. */
    fun addShift(date: Long, label: String?, onCreated: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repo.upsert(
                Shift(date = date, label = label?.trim()?.ifEmpty { null })
            )
            _ui.update { it.copy(showAddDialog = false) }
            onCreated(id)
        }
    }

    fun softDelete(shift: Shift) {
        viewModelScope.launch {
            repo.softDelete(shift.id)
            _ui.update { it.copy(recentlyDeletedShift = shift) }
        }
    }

    fun undoDelete() {
        val shift = _ui.value.recentlyDeletedShift ?: return
        viewModelScope.launch {
            repo.restore(shift.id)
            _ui.update { it.copy(recentlyDeletedShift = null) }
        }
    }

    fun clearUndo() = _ui.update { it.copy(recentlyDeletedShift = null) }
}
