package com.kairos.data.repository

import androidx.room.withTransaction
import com.kairos.core.media.MediaFileManager
import com.kairos.core.model.Case
import com.kairos.core.repository.CaseRepository
import com.kairos.core.repository.DataSafetyCoordinator
import com.kairos.data.db.KairosDatabase
import com.kairos.data.db.dao.CaseDao
import com.kairos.data.db.dao.DiagnosisDao
import com.kairos.data.db.entities.CaseDiagnosisCrossRef
import com.kairos.data.db.entities.ConsultationCaseCrossRef
import com.kairos.data.db.entities.DiagnosisEntity
import com.kairos.data.db.entities.ShiftCaseCrossRef
import com.kairos.data.mapper.toDomain
import com.kairos.data.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CaseRepositoryImpl @Inject constructor(
    private val caseDao: CaseDao,
    private val diagnosisDao: DiagnosisDao,
    private val db: KairosDatabase,
    private val mediaFileManager: MediaFileManager,
    private val dataSafetyCoordinator: DataSafetyCoordinator,
) : CaseRepository {

    /** Replace relative media paths with absolute paths usable by UI (Coil, ExoPlayer). */
    private fun Case.resolveMediaPaths(): Case = copy(
        media = media.map { m ->
            m.copy(filePath = mediaFileManager.resolve(m.filePath).absolutePath)
        }
    )

    private suspend fun getOrCreateDiagnosis(name: String, now: Long): Long {
        val trimmed = name.trim()
        return diagnosisDao.findByNameCi(trimmed)?.id
            ?: run {
                val id = diagnosisDao.insert(DiagnosisEntity(name = trimmed, createdAt = now))
                if (id != -1L) id
                else diagnosisDao.findByNameCi(trimmed)?.id ?: -1L
            }
    }

    override suspend fun upsertCase(
        case: Case,
        diagnosisNames: List<String>,
        linkShiftId: Long?,
        linkSessionId: Long?,
    ): Long = dataSafetyCoordinator.withDataLock {
        val now = System.currentTimeMillis()
        val entity = case.toEntity(now)

        db.withTransaction {
            val caseId: Long = if (case.id == 0L) {
                caseDao.insert(entity)
            } else {
                caseDao.update(entity)
                case.id
            }

            // Replace diagnosis links
            caseDao.clearDiagnosisLinks(caseId)
            if (diagnosisNames.isNotEmpty()) {
                val refs = diagnosisNames.mapNotNull { name ->
                    val diagId = getOrCreateDiagnosis(name, now)
                    if (diagId != -1L) CaseDiagnosisCrossRef(caseId = caseId, diagnosisId = diagId)
                    else null
                }
                if (refs.isNotEmpty()) caseDao.insertDiagnosisLinks(refs)
            }

            linkShiftId?.let {
                caseDao.linkToShift(ShiftCaseCrossRef(shiftId = it, caseId = caseId))
            }
            linkSessionId?.let {
                caseDao.linkToSession(ConsultationCaseCrossRef(sessionId = it, caseId = caseId))
            }

            caseId
        }
    }

    override suspend fun getById(id: Long): Case? =
        caseDao.getById(id)?.toDomain()?.resolveMediaPaths()

    override fun observeById(id: Long): Flow<Case?> =
        caseDao.observeById(id).map { it?.toDomain()?.resolveMediaPaths() }

    override fun observeByDiagnosis(diagnosisId: Long): Flow<List<Case>> =
        caseDao.observeByDiagnosis(diagnosisId).map { list -> list.map { it.toDomain().resolveMediaPaths() } }

    override fun observeByShift(shiftId: Long): Flow<List<Case>> =
        caseDao.observeByShift(shiftId).map { list -> list.map { it.toDomain().resolveMediaPaths() } }

    override fun observeBySession(sessionId: Long): Flow<List<Case>> =
        caseDao.observeBySession(sessionId).map { list -> list.map { it.toDomain().resolveMediaPaths() } }

    override suspend fun unlinkFromShift(caseId: Long, shiftId: Long) = dataSafetyCoordinator.withDataLock {
        caseDao.unlinkFromShift(shiftId, caseId)
    }

    override suspend fun unlinkFromSession(caseId: Long, sessionId: Long) = dataSafetyCoordinator.withDataLock {
        caseDao.unlinkFromSession(sessionId, caseId)
    }

    override suspend fun softDelete(id: Long) = dataSafetyCoordinator.withDataLock {
        caseDao.softDelete(id, System.currentTimeMillis())
    }

    override suspend fun restore(id: Long) = dataSafetyCoordinator.withDataLock {
        caseDao.restore(id, System.currentTimeMillis())
    }

    override fun observeTrashed(): Flow<List<Case>> =
        caseDao.observeTrashed().map { list -> list.map { it.toDomain().resolveMediaPaths() } }
}
