package com.kairos.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kairos.core.model.Case
import com.kairos.core.model.ConsultationSession
import com.kairos.core.model.Patient
import com.kairos.core.model.Shift
import com.kairos.core.repository.CaseRepository
import com.kairos.core.repository.ConsultationRepository
import com.kairos.core.repository.PatientRepository
import com.kairos.core.repository.ShiftRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TrashUiState(
    val patients: List<Patient> = emptyList(),
    val cases: List<Case> = emptyList(),
    val shifts: List<Shift> = emptyList(),
    val sessions: List<ConsultationSession> = emptyList(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class TrashViewModel @Inject constructor(
    private val patientRepo: PatientRepository,
    private val caseRepo: CaseRepository,
    private val shiftRepo: ShiftRepository,
    private val consultationRepo: ConsultationRepository,
) : ViewModel() {

    val ui: StateFlow<TrashUiState> = combine(
        patientRepo.observeTrashed(),
        caseRepo.observeTrashed(),
        shiftRepo.observeTrashed(),
        consultationRepo.observeTrashed(),
    ) { patients, cases, shifts, sessions ->
        TrashUiState(
            patients = patients,
            cases = cases,
            shifts = shifts,
            sessions = sessions,
            isLoading = false,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TrashUiState())

    fun restorePatient(id: Long) = viewModelScope.launch { patientRepo.restore(id) }
    fun restoreCase(id: Long) = viewModelScope.launch { caseRepo.restore(id) }
    fun restoreShift(id: Long) = viewModelScope.launch { shiftRepo.restore(id) }
    fun restoreSession(id: Long) = viewModelScope.launch { consultationRepo.restore(id) }
}
