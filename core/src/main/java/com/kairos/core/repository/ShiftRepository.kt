package com.kairos.core.repository

import com.kairos.core.model.Shift
import kotlinx.coroutines.flow.Flow

interface ShiftRepository {
    suspend fun upsert(shift: Shift): Long
    suspend fun getById(id: Long): Shift?
    fun observeAll(): Flow<List<Shift>>
    fun observeById(id: Long): Flow<Shift?>
    suspend fun softDelete(id: Long)
    suspend fun restore(id: Long)
    fun observeTrashed(): Flow<List<Shift>>
}
