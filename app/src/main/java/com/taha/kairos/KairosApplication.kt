package com.taha.kairos

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.taha.kairos.core.repository.SettingsRepository
import com.taha.kairos.data.backup.WorkerScheduler
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class KairosApplication : Application(), Configuration.Provider {

    @Inject lateinit var workerFactory: HiltWorkerFactory
    @Inject lateinit var workerScheduler: WorkerScheduler
    @Inject lateinit var settingsRepository: SettingsRepository

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        FirebaseAppCheckInitializer.initialize(this)
        // Daily trash purge — register once, KEEP policy so it never duplicates
        workerScheduler.scheduleTrashPurge()
        // Observe backup schedule setting and re-register whenever it changes
        appScope.launch {
            settingsRepository.observeSettings()
                .map { it.backupSchedule }
                .distinctUntilChanged()
                .collect { schedule -> workerScheduler.scheduleBackup(schedule) }
        }
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
