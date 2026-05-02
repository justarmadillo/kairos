package com.kairos.data.repository

import androidx.room.withTransaction
import com.kairos.core.model.Diagnosis
import com.kairos.core.model.DiagnosisSortMode
import com.kairos.core.repository.DataSafetyCoordinator
import com.kairos.core.repository.DiagnosisRepository
import com.kairos.data.db.KairosDatabase
import com.kairos.data.db.dao.DiagnosisDao
import com.kairos.data.db.entities.DiagnosisEntity
import com.kairos.data.mapper.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiagnosisRepositoryImpl @Inject constructor(
    private val dao: DiagnosisDao,
    private val db: KairosDatabase,
    private val dataSafetyCoordinator: DataSafetyCoordinator,
) : DiagnosisRepository {

    override suspend fun getOrCreate(name: String): Long = dataSafetyCoordinator.withDataLock {
        val trimmed = name.trim()
        val now = System.currentTimeMillis()
        db.withTransaction {
            dao.findByNameCi(trimmed)?.id
                ?: dao.insert(DiagnosisEntity(name = trimmed, createdAt = now)).let { id ->
                    if (id != -1L) id
                    else dao.findByNameCi(trimmed)?.id ?: -1L
                }
        }
    }

    override fun observeAll(sort: DiagnosisSortMode): Flow<List<Diagnosis>> {
        val flow = when (sort) {
            DiagnosisSortMode.ALPHABETICAL -> dao.observeAllAlphabetical()
            DiagnosisSortMode.MOST_USED -> dao.observeAllByMostUsed()
            DiagnosisSortMode.RECENT -> dao.observeAllByRecent()
        }
        return flow.map { list -> list.map { it.toDomain() } }
    }

    override suspend fun searchByPrefix(prefix: String, limit: Int): List<Diagnosis> =
        dao.searchByPrefix(prefix, limit).map { it.toDomain() }

    override suspend fun getById(id: Long): Diagnosis? =
        dao.getById(id)?.toDomain()
}
