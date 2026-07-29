package com.taha.kairos.features.consultation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taha.kairos.core.model.Case
import com.taha.kairos.core.model.ConsultationSession
import com.taha.kairos.core.repository.CaseRepository
import com.taha.kairos.core.repository.ConsultationRepository
import com.taha.kairos.core.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

data class DateItem(
    val date: LocalDate,
    val isConsultationDay: Boolean,
    val session: ConsultationSession? = null,
)

data class ConsultationUiState(
    val consultationDayOfWeek: DayOfWeek = DayOfWeek.THURSDAY,
    val selectedDate: LocalDate = LocalDate.now(),
    val dates: List<DateItem> = emptyList(),
    val selectedSession: ConsultationSession? = null,
    val isLoading: Boolean = true,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ConsultationViewModel @Inject constructor(
    private val settingsRepo: SettingsRepository,
    private val consultationRepo: ConsultationRepository,
    private val caseRepo: CaseRepository,
) : ViewModel() {

    private val _selectedDate = MutableStateFlow(LocalDate.now())

    // Range: 1 year back → 1 year forward
    private val rangeStart = LocalDate.now().minusYears(1)
    private val rangeEnd = LocalDate.now().plusYears(1)

    // All sessions in range, refreshed reactively
    private val _sessions: StateFlow<List<ConsultationSession>> = combine(
        flowOf(Unit),
        settingsRepo.observeSettings(),
    ) { _, _ -> Unit }.flatMapLatest {
        val startMs = rangeStart.toEpochMs()
        val endMs = rangeEnd.toEpochMs()
        consultationRepo.observeForDateRange(startMs, endMs)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val ui: StateFlow<ConsultationUiState> = combine(
        settingsRepo.observeSettings(),
        _selectedDate,
        _sessions,
    ) { settings, selectedDate, sessions ->
        val dow = settings.consultationDayOfWeek
        val dates = buildDateList(rangeStart, rangeEnd, dow, sessions)
        val selectedMs = selectedDate.toEpochMs()
        val selectedSession = sessions.find { it.date == selectedMs }
        ConsultationUiState(
            consultationDayOfWeek = dow,
            selectedDate = selectedDate,
            dates = dates,
            selectedSession = selectedSession,
            isLoading = false,
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        ConsultationUiState(),
    )

    // Cases for selected session — re-derives when either selected date or sessions list changes
    val cases: StateFlow<List<Case>> = combine(_selectedDate, _sessions) { date, sessions ->
        sessions.find { it.date == date.toEpochMs() }?.id
    }.flatMapLatest { sessionId ->
        if (sessionId != null) caseRepo.observeBySession(sessionId)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun selectDate(date: LocalDate) {
        val dow = ui.value.consultationDayOfWeek
        if (date.dayOfWeek == dow) {
            _selectedDate.update { date }
        }
    }

    fun selectPreviousConsultationDate() = selectAdjacentConsultationDate(direction = -1L)

    fun selectNextConsultationDate() = selectAdjacentConsultationDate(direction = 1L)

    /** Get-or-create the session for the selected date, return its ID for navigation. */
    fun getOrCreateSessionForSelected(onReady: (sessionId: Long) -> Unit) {
        viewModelScope.launch {
            val ms = _selectedDate.value.toEpochMs()
            val sessionId = consultationRepo.getOrCreateForDate(ms)
            onReady(sessionId)
        }
    }

    private fun buildDateList(
        start: LocalDate,
        end: LocalDate,
        consultationDow: DayOfWeek,
        sessions: List<ConsultationSession>,
    ): List<DateItem> {
        val sessionByDate = sessions.associateBy { it.date }
        val result = mutableListOf<DateItem>()
        var current = start
        while (!current.isAfter(end)) {
            val isConsultDay = current.dayOfWeek == consultationDow
            val ms = current.toEpochMs()
            result.add(
                DateItem(
                    date = current,
                    isConsultationDay = isConsultDay,
                    session = sessionByDate[ms],
                )
            )
            current = current.plusDays(1)
        }
        return result
    }

    private fun selectAdjacentConsultationDate(direction: Long) {
        val consultationDow = ui.value.consultationDayOfWeek
        var candidate = _selectedDate.value.plusDays(direction)
        while (candidate.dayOfWeek != consultationDow) {
            candidate = candidate.plusDays(direction)
        }
        if (!candidate.isBefore(rangeStart) && !candidate.isAfter(rangeEnd)) {
            _selectedDate.update { candidate }
        }
    }
}

private fun LocalDate.toEpochMs(): Long =
    atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
