package com.kairos.core.repository

import com.kairos.core.model.ConsultationSession
import kotlinx.coroutines.flow.Flow

interface ConsultationRepository {
    /** Get-or-create session for a given epoch-day-aligned date (day boundary). */
    suspend fun getOrCreateForDate(dateMillis: Long): Long

    suspend fun getById(id: Long): ConsultationSession?

    fun observeForDateRange(startMillis: Long, endMillis: Long): Flow<List<ConsultationSession>>

    suspend fun softDelete(id: Long)
    suspend fun restore(id: Long)
    fun observeTrashed(): Flow<List<ConsultationSession>>
}
