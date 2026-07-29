package com.taha.kairos.data.backup

/**
 * Decides which backup zips to delete when pruning a backup folder.
 *
 * Retention policy: keep the [KEEP_RECENT] newest backups, plus the newest backup
 * of each calendar month for up to [KEEP_MONTHLY_MONTHS] distinct months. This
 * prevents a silently-corrupted database from rotating out every good copy the
 * way a plain "keep newest N" policy can.
 *
 * Only names matching kairos-backup-yyyyMMdd-HHmmss.zip are ever considered for
 * deletion; anything else in the folder is left untouched.
 */
object BackupPruner {

    const val KEEP_RECENT = 5
    const val KEEP_MONTHLY_MONTHS = 12

    private val BACKUP_NAME = Regex("""kairos-backup-(\d{8})-\d{6}\.zip""")

    fun selectBackupsToDelete(
        names: List<String>,
        keepRecent: Int = KEEP_RECENT,
        keepMonthly: Int = KEEP_MONTHLY_MONTHS,
    ): Set<String> {
        // Lexicographic order of the yyyyMMdd-HHmmss stamp is chronological.
        val dated = names.mapNotNull { name ->
            BACKUP_NAME.matchEntire(name)?.let { match -> name to match.groupValues[1] }
        }.sortedByDescending { it.first }

        val keep = mutableSetOf<String>()
        dated.take(keepRecent).forEach { keep += it.first }

        dated.groupBy { (_, date) -> date.substring(0, 6) } // yyyyMM
            .toSortedMap(compareByDescending { it })
            .values
            .take(keepMonthly)
            .forEach { monthFiles -> keep += monthFiles.first().first } // newest of that month

        return dated.mapTo(mutableSetOf()) { it.first } - keep
    }
}
