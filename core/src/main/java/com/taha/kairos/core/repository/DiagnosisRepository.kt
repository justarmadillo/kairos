package com.taha.kairos.core.repository

import com.taha.kairos.core.model.Diagnosis
import com.taha.kairos.core.model.DiagnosisSortMode
import kotlinx.coroutines.flow.Flow

enum class DiagnosisRenameResult {
    RENAMED,
    UNCHANGED,
    BLANK_NAME,
    ALREADY_EXISTS,
    NOT_FOUND,
}

interface DiagnosisRepository {
    /** Atomic lookup-or-insert (case-insensitive trimmed match). Returns id. */
    suspend fun getOrCreate(name: String): Long

    /** Atomically rename a diagnosis after trimming and checking for case-insensitive duplicates. */
    suspend fun rename(id: Long, newName: String): DiagnosisRenameResult

    fun observeAll(sort: DiagnosisSortMode): Flow<List<Diagnosis>>

    /** Prefix search for autocomplete. */
    suspend fun searchByPrefix(prefix: String, limit: Int = 10): List<Diagnosis>

    suspend fun getById(id: Long): Diagnosis?
}
