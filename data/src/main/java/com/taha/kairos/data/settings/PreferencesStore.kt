package com.taha.kairos.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.taha.kairos.core.model.AppSettings
import com.taha.kairos.core.model.BackupSchedule
import com.taha.kairos.core.model.DiagnosisSortMode
import com.taha.kairos.core.model.ThemeMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.DayOfWeek
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "kairos_prefs")

@Singleton
class PreferencesStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private object Keys {
        val CONSULTATION_DOW = intPreferencesKey("consultation_day_of_week")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DIAGNOSIS_SORT = stringPreferencesKey("diagnosis_sort_mode")
        val BACKUP_FOLDER_URI = stringPreferencesKey("backup_folder_uri")
        val BACKUP_SCHEDULE = stringPreferencesKey("backup_schedule")
        val BACKUP_LAST_RUN_AT = longPreferencesKey("backup_last_run_at")
        val BACKUP_LAST_SUCCESS = booleanPreferencesKey("backup_last_success")
    }

    val settings: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            consultationDayOfWeek = DayOfWeek.of(
                prefs[Keys.CONSULTATION_DOW] ?: DayOfWeek.THURSDAY.value
            ),
            themeMode = prefs[Keys.THEME_MODE]
                ?.let { v -> ThemeMode.entries.find { it.name == v } }
                ?: ThemeMode.SYSTEM,
            diagnosisSortMode = prefs[Keys.DIAGNOSIS_SORT]
                ?.let { v -> DiagnosisSortMode.entries.find { it.name == v } }
                ?: DiagnosisSortMode.ALPHABETICAL,
            backupFolderUri = prefs[Keys.BACKUP_FOLDER_URI],
            backupSchedule = prefs[Keys.BACKUP_SCHEDULE]
                ?.let { v -> BackupSchedule.entries.find { it.name == v } }
                ?: BackupSchedule.OFF,
            backupLastRunAt = prefs[Keys.BACKUP_LAST_RUN_AT],
            backupLastRunSuccess = prefs[Keys.BACKUP_LAST_SUCCESS],
        )
    }

    suspend fun setConsultationDayOfWeek(day: DayOfWeek) {
        context.dataStore.edit { it[Keys.CONSULTATION_DOW] = day.value }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    suspend fun setDiagnosisSortMode(mode: DiagnosisSortMode) {
        context.dataStore.edit { it[Keys.DIAGNOSIS_SORT] = mode.name }
    }

    suspend fun setBackupFolderUri(uri: String?) {
        context.dataStore.edit { prefs ->
            if (uri != null) prefs[Keys.BACKUP_FOLDER_URI] = uri
            else prefs.remove(Keys.BACKUP_FOLDER_URI)
        }
    }

    suspend fun setBackupSchedule(schedule: BackupSchedule) {
        context.dataStore.edit { it[Keys.BACKUP_SCHEDULE] = schedule.name }
    }

    suspend fun recordBackupRun(timestampMs: Long, success: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.BACKUP_LAST_RUN_AT] = timestampMs
            prefs[Keys.BACKUP_LAST_SUCCESS] = success
        }
    }
}
