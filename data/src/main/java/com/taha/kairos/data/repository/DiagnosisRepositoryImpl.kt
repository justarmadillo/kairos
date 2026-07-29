package com.taha.kairos.data.repository

import androidx.room.withTransaction
import com.taha.kairos.core.model.Diagnosis
import com.taha.kairos.core.model.DiagnosisSortMode
import com.taha.kairos.core.repository.DataSafetyCoordinator
import com.taha.kairos.core.repository.DiagnosisRepository
import com.taha.kairos.core.repository.DiagnosisRenameResult
import com.taha.kairos.data.db.KairosDatabase
import com.taha.kairos.data.db.dao.DiagnosisDao
import com.taha.kairos.data.db.entities.DiagnosisEntity
import com.taha.kairos.data.mapper.toDomain
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

    override suspend fun rename(id: Long, newName: String): DiagnosisRenameResult =
        dataSafetyCoordinator.withDataLock {
            val trimmed = newName.trim()
            if (trimmed.isEmpty()) {
                return@withDataLock DiagnosisRenameResult.BLANK_NAME
            }

            db.withTransaction {
                val current = dao.getById(id)
                    ?: return@withTransaction DiagnosisRenameResult.NOT_FOUND
                val matchingDiagnosis = dao.findByNameCi(trimmed)

                when {
                    matchingDiagnosis != null && matchingDiagnosis.id != id ->
                        DiagnosisRenameResult.ALREADY_EXISTS

                    current.name == trimmed ->
                        DiagnosisRenameResult.UNCHANGED

                    dao.rename(id, trimmed) == 1 ->
                        DiagnosisRenameResult.RENAMED

                    else ->
                        DiagnosisRenameResult.NOT_FOUND
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
