package com.kairos.core.repository

import com.kairos.core.model.Diagnosis
import com.kairos.core.model.DiagnosisSortMode
import kotlinx.coroutines.flow.Flow

interface DiagnosisRepository {
    /** Atomic lookup-or-insert (case-insensitive trimmed match). Returns id. */
    suspend fun getOrCreate(name: String): Long

    fun observeAll(sort: DiagnosisSortMode): Flow<List<Diagnosis>>

    /** Prefix search for autocomplete. */
    suspend fun searchByPrefix(prefix: String, limit: Int = 10): List<Diagnosis>

    suspend fun getById(id: Long): Diagnosis?
}
