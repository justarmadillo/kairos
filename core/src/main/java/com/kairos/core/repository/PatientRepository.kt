package com.kairos.core.repository

import com.kairos.core.model.Patient
import kotlinx.coroutines.flow.Flow

interface PatientRepository {
    suspend fun upsert(patient: Patient): Long
    suspend fun getById(id: Long): Patient?
    fun observeById(id: Long): Flow<Patient?>
    fun search(query: String): Flow<List<Patient>>
    suspend fun softDelete(id: Long)
    suspend fun restore(id: Long)
    fun observeTrashed(): Flow<List<Patient>>
}
