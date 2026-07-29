package com.taha.kairos.core.model

import java.time.DayOfWeek

enum class ThemeMode { LIGHT, DARK, SYSTEM }

enum class DiagnosisSortMode { ALPHABETICAL, MOST_USED, RECENT }

enum class BackupSchedule { OFF, DAILY, WEEKLY, MONTHLY }

data class AppSettings(
    val consultationDayOfWeek: DayOfWeek = DayOfWeek.THURSDAY,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val diagnosisSortMode: DiagnosisSortMode = DiagnosisSortMode.ALPHABETICAL,
    val backupFolderUri: String? = null,
    val backupSchedule: BackupSchedule = BackupSchedule.OFF,
    val backupLastRunAt: Long? = null,
    val backupLastRunSuccess: Boolean? = null
)
