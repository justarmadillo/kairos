package com.kairos.features.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kairos.core.repository.DashboardRepository
import com.kairos.core.repository.RecentCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

data class DashboardUiState(
    val totalPatients: Int = 0,
    val totalCases: Int = 0,
    val totalShifts: Int = 0,
    val casesThisWeek: Int = 0,
    val casesLastWeek: Int = 0,
    val casesThisMonth: Int = 0,
    val casesLastMonth: Int = 0,
    val recentCases: List<RecentCase> = emptyList(),
    val milestone: MilestoneProgress = MilestoneProgress(0, 10, 0),
    val milestoneCelebration: Int? = null,
)

data class MilestoneProgress(
    val current: Int,
    val target: Int,
    val previous: Int,
)

private data class PeriodCounts(
    val thisWeek: Int = 0,
    val lastWeek: Int = 0,
    val thisMonth: Int = 0,
    val lastMonth: Int = 0,
)

private data class CaseMetrics(
    val totalCases: Int,
    val periods: PeriodCounts,
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repo: DashboardRepository,
) : ViewModel() {

    private val milestoneCelebration = MutableStateFlow<Int?>(null)
    private var hasObservedTotalCases = false
    private var lastCelebratedMilestone: Int? = null

    private val caseMetrics = repo.observeTotalCases()
        .map { totalCases ->
            maybeTriggerMilestoneCelebration(totalCases)
            CaseMetrics(
                totalCases = totalCases,
                periods = loadPeriodCounts(),
            )
        }

    val ui: StateFlow<DashboardUiState> = combine(
        repo.observeTotalPatients(),
        caseMetrics,
        repo.observeTotalShifts(),
        repo.observeRecentCases(),
        milestoneCelebration,
    ) { totalPatients, metrics, totalShifts, recentCases, celebration ->
        val displayedMilestone = if (celebration == metrics.totalCases) {
            MilestoneProgress(
                current = metrics.totalCases,
                target = celebration,
                previous = previousMilestone((celebration - 1).coerceAtLeast(0)),
            )
        } else {
            MilestoneProgress(
                current = metrics.totalCases,
                target = nextMilestone(metrics.totalCases),
                previous = previousMilestone(metrics.totalCases),
            )
        }

        DashboardUiState(
            totalPatients = totalPatients,
            totalCases = metrics.totalCases,
            totalShifts = totalShifts,
            casesThisWeek = metrics.periods.thisWeek,
            casesLastWeek = metrics.periods.lastWeek,
            casesThisMonth = metrics.periods.thisMonth,
            casesLastMonth = metrics.periods.lastMonth,
            recentCases = recentCases,
            milestone = displayedMilestone,
            milestoneCelebration = celebration,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DashboardUiState(),
    )

    private suspend fun loadPeriodCounts(): PeriodCounts {
        val zone = ZoneId.systemDefault()
        val today = LocalDate.now(zone)

        val thisWeekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val nextWeekStart = thisWeekStart.plusWeeks(1)
        val lastWeekStart = thisWeekStart.minusWeeks(1)

        val thisMonthStart = today.withDayOfMonth(1)
        val nextMonthStart = thisMonthStart.plusMonths(1)
        val lastMonthStart = thisMonthStart.minusMonths(1)

        return PeriodCounts(
            thisWeek = repo.countCasesInRange(
                startMs = thisWeekStart.toStartOfDayMillis(zone),
                endMs = nextWeekStart.toStartOfDayMillis(zone),
            ),
            lastWeek = repo.countCasesInRange(
                startMs = lastWeekStart.toStartOfDayMillis(zone),
                endMs = thisWeekStart.toStartOfDayMillis(zone),
            ),
            thisMonth = repo.countCasesInRange(
                startMs = thisMonthStart.toStartOfDayMillis(zone),
                endMs = nextMonthStart.toStartOfDayMillis(zone),
            ),
            lastMonth = repo.countCasesInRange(
                startMs = lastMonthStart.toStartOfDayMillis(zone),
                endMs = thisMonthStart.toStartOfDayMillis(zone),
            ),
        )
    }

    private fun maybeTriggerMilestoneCelebration(totalCases: Int) {
        val isMilestone = totalCases > 0 && previousMilestone(totalCases) == totalCases
        if (hasObservedTotalCases && isMilestone && lastCelebratedMilestone != totalCases) {
            lastCelebratedMilestone = totalCases
            milestoneCelebration.value = totalCases
            viewModelScope.launch {
                delay(2_500)
                if (milestoneCelebration.value == totalCases) {
                    milestoneCelebration.value = null
                }
            }
        }
        hasObservedTotalCases = true
    }
}

fun nextMilestone(currentCases: Int): Int = when {
    currentCases < 100 -> ((currentCases / 10) + 1) * 10
    currentCases < 500 -> ((currentCases / 50) + 1) * 50
    else -> ((currentCases / 100) + 1) * 100
}

fun previousMilestone(currentCases: Int): Int = when {
    currentCases < 10 -> 0
    currentCases < 100 -> (currentCases / 10) * 10
    currentCases < 500 -> (currentCases / 50) * 50
    else -> (currentCases / 100) * 100
}

private fun LocalDate.toStartOfDayMillis(zone: ZoneId): Long =
    atStartOfDay(zone).toInstant().toEpochMilli()
