package com.taha.kairos.core.repository

import kotlinx.coroutines.flow.Flow

interface DashboardRepository {
    fun observeTotalPatients(): Flow<Int>
    fun observeTotalCases(): Flow<Int>
    fun observeTotalShifts(): Flow<Int>
    fun observeRecentCases(): Flow<List<RecentCase>>
    suspend fun countCasesInRange(startMs: Long, endMs: Long): Int
}

data class RecentCase(
    val caseId: Long,
    val patientName: String,
    val diagnosisName: String?,
    val caseDate: Long,
)
