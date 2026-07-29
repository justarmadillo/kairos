package com.taha.kairos.data.backup

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.taha.kairos.core.media.MediaFileManager
import com.taha.kairos.core.repository.DataSafetyCoordinator
import com.taha.kairos.data.authorization.CachedAuthorizationGuard
import com.taha.kairos.data.db.dao.CaseDao
import com.taha.kairos.data.db.dao.CaseMediaDao
import com.taha.kairos.data.db.dao.ConsultationSessionDao
import com.taha.kairos.data.db.dao.DiagnosisDao
import com.taha.kairos.data.db.dao.PatientDao
import com.taha.kairos.data.db.dao.ShiftDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.io.File
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
    private val authorizationGuard: CachedAuthorizationGuard,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // A locked app may export its data, but must not mutate or purge it.
        if (!authorizationGuard.hasCachedAccess()) return Result.success()

        return try {
            dataSafetyCoordinator.withDataLock {
                withContext(Dispatchers.IO) {
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

                    purgeUnreferencedMediaFiles()
                }
            }

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    /**
     * Retries best-effort file deletion from earlier interrupted edits/purges.
     * Only positive case directories are considered; legacy cases/0 temporary
     * files are ignored so an older in-progress editor cannot be disrupted.
     */
    private suspend fun purgeUnreferencedMediaFiles() {
        val referencedPaths = caseMediaDao.listAllFilePaths()
            .map { it.replace('\\', '/') }
            .toHashSet()
        val mediaRoot = mediaFileManager.rootDir()
        if (!mediaRoot.exists()) return

        mediaRoot.walkBottomUp().forEach { entry ->
            when {
                entry.isFile -> {
                    val relative = entry.relativeTo(mediaRoot).path
                        .replace(File.separatorChar, '/')
                    if (!relative.startsWith(LEGACY_PENDING_MEDIA_PREFIX) &&
                        relative !in referencedPaths
                    ) {
                        runCatching { entry.delete() }
                    }
                }

                entry.isDirectory && entry != mediaRoot ->
                    runCatching { entry.delete() }
            }
        }
    }

    private companion object {
        const val LEGACY_PENDING_MEDIA_PREFIX = "cases/0/"
    }
}
