package com.taha.kairos.core.repository

import com.taha.kairos.core.model.Case
import kotlinx.coroutines.flow.Flow

interface CaseRepository {
    /**
     * Save a case (create if id == 0, update otherwise) along with its diagnoses and media.
     * If [linkShiftId] or [linkSessionId] is non-null, links the case to that shift/session.
     */
    suspend fun upsertCase(
        case: Case,
        diagnosisNames: List<String>,
        linkShiftId: Long? = null,
        linkSessionId: Long? = null
    ): Long

    suspend fun getById(id: Long): Case?
    fun observeById(id: Long): Flow<Case?>

    fun observeByDiagnosis(diagnosisId: Long): Flow<List<Case>>
    fun observeByShift(shiftId: Long): Flow<List<Case>>
    fun observeBySession(sessionId: Long): Flow<List<Case>>

    suspend fun unlinkFromShift(caseId: Long, shiftId: Long)
    suspend fun unlinkFromSession(caseId: Long, sessionId: Long)

    suspend fun softDelete(id: Long)
    suspend fun restore(id: Long)
    fun observeTrashed(): Flow<List<Case>>
}
