package com.taha.kairos.core.repository

import com.taha.kairos.core.model.AppSettings
import com.taha.kairos.core.model.BackupSchedule
import com.taha.kairos.core.model.DiagnosisSortMode
import com.taha.kairos.core.model.ThemeMode
import kotlinx.coroutines.flow.Flow
import java.time.DayOfWeek

interface SettingsRepository {
    fun observeSettings(): Flow<AppSettings>
    suspend fun setConsultationDayOfWeek(day: DayOfWeek)
    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setDiagnosisSortMode(mode: DiagnosisSortMode)
    suspend fun setBackupFolderUri(uri: String?)
    suspend fun setBackupSchedule(schedule: BackupSchedule)
    suspend fun recordBackupRun(timestampMs: Long, success: Boolean)
}
