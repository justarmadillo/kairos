package com.kairos.data.repository

import com.kairos.core.model.AppSettings
import com.kairos.core.model.BackupSchedule
import com.kairos.core.model.DiagnosisSortMode
import com.kairos.core.model.ThemeMode
import com.kairos.core.repository.SettingsRepository
import com.kairos.data.settings.PreferencesStore
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val store: PreferencesStore,
) : SettingsRepository {

    override fun observeSettings(): Flow<AppSettings> = store.settings

    override suspend fun setConsultationDayOfWeek(day: DayOfWeek) =
        store.setConsultationDayOfWeek(day)

    override suspend fun setThemeMode(mode: ThemeMode) =
        store.setThemeMode(mode)

    override suspend fun setDiagnosisSortMode(mode: DiagnosisSortMode) =
        store.setDiagnosisSortMode(mode)

    override suspend fun setBackupFolderUri(uri: String?) =
        store.setBackupFolderUri(uri)

    override suspend fun setBackupSchedule(schedule: BackupSchedule) =
        store.setBackupSchedule(schedule)

    override suspend fun recordBackupRun(timestampMs: Long, success: Boolean) =
        store.recordBackupRun(timestampMs, success)
}
