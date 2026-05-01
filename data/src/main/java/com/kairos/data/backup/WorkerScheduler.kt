package com.kairos.data.backup

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.kairos.core.model.BackupSchedule
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkerScheduler @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val wm get() = WorkManager.getInstance(context)

    /** Call on app start and whenever backup schedule setting changes. */
    fun scheduleBackup(schedule: BackupSchedule) {
        if (schedule == BackupSchedule.OFF) {
            wm.cancelUniqueWork(ScheduledBackupWorker.WORK_NAME)
            return
        }
        val interval = when (schedule) {
            BackupSchedule.DAILY -> 1L to TimeUnit.DAYS
            BackupSchedule.WEEKLY -> 7L to TimeUnit.DAYS
            BackupSchedule.MONTHLY -> 30L to TimeUnit.DAYS
            BackupSchedule.OFF -> return
        }
        val request = PeriodicWorkRequestBuilder<ScheduledBackupWorker>(
            interval.first, interval.second,
        ).setConstraints(
            Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()
        ).build()

        wm.enqueueUniquePeriodicWork(
            ScheduledBackupWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request,
        )
    }

    /** Enqueue daily trash purge (idempotent — UPDATE policy). */
    fun scheduleTrashPurge() {
        val request = PeriodicWorkRequestBuilder<TrashPurgeWorker>(1, TimeUnit.DAYS)
            .setConstraints(Constraints.Builder().setRequiresBatteryNotLow(true).build())
            .build()
        wm.enqueueUniquePeriodicWork(
            ScheduledBackupWorker.PURGE_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request,
        )
    }
}
