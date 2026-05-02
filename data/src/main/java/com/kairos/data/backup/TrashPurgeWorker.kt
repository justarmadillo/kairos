package com.kairos.data.backup

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kairos.core.media.MediaFileManager
import com.kairos.core.repository.DataSafetyCoordinator
import com.kairos.data.db.dao.CaseDao
import com.kairos.data.db.dao.CaseMediaDao
import com.kairos.data.db.dao.ConsultationSessionDao
import com.kairos.data.db.dao.DiagnosisDao
import com.kairos.data.db.dao.PatientDao
import com.kairos.data.db.dao.ShiftDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.concurrent.TimeUnit

@HiltWorker
class TrashPurgeWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val patientDao: PatientDao,
    private val caseDao: CaseDao,
    private val caseMediaDao: CaseMediaDao,
    private val diagnosisDao: DiagnosisDao,
    private val shiftDao: ShiftDao,
    private val sessionDao: ConsultationSessionDao,
    private val mediaFileManager: MediaFileManager,
    private val dataSafetyCoordinator: DataSafetyCoordinator,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            dataSafetyCoordinator.withDataLock {
                val threshold = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(30)

                // Collect media paths BEFORE deleting DB records so cascades don't remove them first
                val expiredCaseIds = caseDao.listExpiredTrash(threshold)
                val mediaPaths = expiredCaseIds.flatMap { caseId ->
                    caseMediaDao.listForCase(caseId).map { it.filePath }
                }

                // Delete DB records FIRST (cascade handles junction tables)
                if (expiredCaseIds.isNotEmpty()) {
                    caseDao.hardDelete(expiredCaseIds)
                }

                // Purge other entities
                patientDao.purgeOlderThan(threshold)
                shiftDao.purgeOlderThan(threshold)
                sessionDao.purgeOlderThan(threshold)

                // Clean up diagnoses that no case references anymore
                diagnosisDao.deleteOrphaned()

                // THEN delete files — DB is now consistent even if this is interrupted
                mediaPaths.forEach { path ->
                    try { mediaFileManager.delete(path) } catch (_: Exception) {}
                }
                expiredCaseIds.forEach { caseId ->
                    try { mediaFileManager.deleteCaseDir(caseId) } catch (_: Exception) {}
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
