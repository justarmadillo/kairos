package com.kairos.data.repository

import androidx.room.withTransaction
import com.kairos.core.model.ConsultationSession
import com.kairos.core.repository.ConsultationRepository
import com.kairos.data.db.KairosDatabase
import com.kairos.data.db.dao.ConsultationSessionDao
import com.kairos.data.db.entities.ConsultationSessionEntity
import com.kairos.data.mapper.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConsultationRepositoryImpl @Inject constructor(
    private val dao: ConsultationSessionDao,
    private val db: KairosDatabase,
) : ConsultationRepository {

    override suspend fun getOrCreateForDate(dateMillis: Long): Long {
        val now = System.currentTimeMillis()
        return db.withTransaction {
            dao.findByDate(dateMillis)?.id
                ?: dao.insert(ConsultationSessionEntity(date = dateMillis, createdAt = now)).let { id ->
                    if (id != -1L) id
                    else dao.findByDate(dateMillis)?.id ?: -1L
                }
        }
    }

    override suspend fun getById(id: Long): ConsultationSession? =
        dao.getById(id)?.toDomain()

    override fun observeForDateRange(
        startMillis: Long,
        endMillis: Long,
    ): Flow<List<ConsultationSession>> =
        dao.observeForRange(startMillis, endMillis)
            .map { list -> list.map { it.toDomain() } }

    override suspend fun softDelete(id: Long) =
        dao.softDelete(id, System.currentTimeMillis())

    override suspend fun restore(id: Long) =
        dao.restore(id)

    override fun observeTrashed(): Flow<List<ConsultationSession>> =
        dao.observeTrashed().map { list -> list.map { it.toDomain() } }
}
