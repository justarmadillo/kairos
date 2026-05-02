package com.kairos.data.backup

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.kairos.core.repository.SettingsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class ScheduledBackupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val backupEngine: BackupEngine,
    private val settingsRepo: SettingsRepository,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // Collect settings once (not a Flow subscription — just a one-shot read via first())
            val settings = settingsRepo.observeSettings().first()
            val folderUri = settings.backupFolderUri
                ?: return Result.failure() // URI missing — won't fix itself; let next scheduled run check again

            val result = backupEngine.export(folderUri)
            settingsRepo.recordBackupRun(result.timestampMs, result.success)

            notify(result.success, result.error)
            // Never retry on PeriodicWorkRequest — the next scheduled run will retry naturally
            if (result.success) Result.success() else Result.failure()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun notify(success: Boolean, error: String?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, "Backup", NotificationManager.IMPORTANCE_LOW)
            )
        }
        val title = if (success) "Backup complete" else "Backup failed"
        val text = if (success) "Kairos backup saved successfully"
        else "Backup error: ${error ?: "unknown"}"

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_save)
            .setContentTitle(title)
            .setContentText(text)
            .setAutoCancel(true)
            .build()

        manager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "kairos_backup"
        const val NOTIFICATION_ID = 1001
        const val WORK_NAME = "kairos_scheduled_backup"
        const val PURGE_WORK_NAME = "kairos_trash_purge"
    }
}
