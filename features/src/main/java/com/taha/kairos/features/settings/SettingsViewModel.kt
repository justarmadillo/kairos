package com.taha.kairos.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taha.kairos.core.model.AppSettings
import com.taha.kairos.core.model.BackupSchedule
import com.taha.kairos.core.model.DiagnosisSortMode
import com.taha.kairos.core.model.ThemeMode
import com.taha.kairos.core.repository.BackupRepository
import com.taha.kairos.core.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import javax.inject.Inject

data class BackupUiState(
    val isExporting: Boolean = false,
    val isRestoring: Boolean = false,
    val isVacuuming: Boolean = false,
    val lastMessage: String? = null,
    val restoreCompleted: Boolean = false,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repo: SettingsRepository,
    private val backupEngine: BackupRepository,
) : ViewModel() {

    val settings: StateFlow<AppSettings> = repo.observeSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppSettings())

    private val _backupUi = MutableStateFlow(BackupUiState())
    val backupUi: StateFlow<BackupUiState> = _backupUi.asStateFlow()

    fun setConsultationDay(day: DayOfWeek) =
        viewModelScope.launch { repo.setConsultationDayOfWeek(day) }

    fun setTheme(mode: ThemeMode) =
        viewModelScope.launch { repo.setThemeMode(mode) }

    fun setDiagnosisSort(mode: DiagnosisSortMode) =
        viewModelScope.launch { repo.setDiagnosisSortMode(mode) }

    fun setBackupFolder(uri: String?) =
        viewModelScope.launch { repo.setBackupFolderUri(uri) }

    fun setBackupSchedule(schedule: BackupSchedule) =
        viewModelScope.launch { repo.setBackupSchedule(schedule) }

    fun exportNow() {
        val folderUri = settings.value.backupFolderUri ?: run {
            _backupUi.update { it.copy(lastMessage = "No backup folder selected") }
            return
        }
        viewModelScope.launch {
            _backupUi.update { it.copy(isExporting = true, lastMessage = null) }
            val result = backupEngine.export(folderUri)
            repo.recordBackupRun(result.timestampMs, result.success)
            _backupUi.update {
                it.copy(
                    isExporting = false,
                    lastMessage = if (result.success) "Backup saved" else "Backup failed: ${result.error}",
                )
            }
        }
    }

    fun restoreBackup(zipUri: String) {
        viewModelScope.launch {
            _backupUi.update { it.copy(isRestoring = true, lastMessage = null) }
            val result = backupEngine.restore(zipUri)
            _backupUi.update {
                it.copy(
                    isRestoring = false,
                    restoreCompleted = result.success,
                    lastMessage = if (!result.success) "Restore failed: ${result.error}" else null,
                )
            }
        }
    }

    fun vacuumDatabase() {
        viewModelScope.launch {
            _backupUi.update { it.copy(isVacuuming = true, lastMessage = null) }
            try {
                backupEngine.vacuumDatabase()
                _backupUi.update { it.copy(isVacuuming = false, lastMessage = "Database optimized") }
            } catch (e: Exception) {
                _backupUi.update { it.copy(isVacuuming = false, lastMessage = "Optimization failed: ${e.message}") }
            }
        }
    }

    fun clearBackupMessage() = _backupUi.update { it.copy(lastMessage = null) }

    fun clearRestoreCompleted() = _backupUi.update { it.copy(restoreCompleted = false) }
}
