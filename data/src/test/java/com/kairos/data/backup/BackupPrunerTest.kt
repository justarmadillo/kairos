package com.kairos.data.backup

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BackupPrunerTest {

    private fun name(date: String, time: String = "120000") = "kairos-backup-$date-$time.zip"

    @Test
    fun `keeps everything when under recent limit`() {
        val names = listOf(name("20260715"), name("20260716"), name("20260717"))
        assertTrue(BackupPruner.selectBackupsToDelete(names).isEmpty())
    }

    @Test
    fun `deletes beyond recent limit within one month`() {
        val names = (1..9).map { name("2026070$it") }
        val toDelete = BackupPruner.selectBackupsToDelete(names)
        // newest 5 kept; newest-of-month is already among them; oldest 4 deleted
        assertEquals(
            setOf(name("20260701"), name("20260702"), name("20260703"), name("20260704")),
            toDelete,
        )
    }

    @Test
    fun `keeps newest backup of older months`() {
        val names = listOf(
            name("20260401"), name("20260415"), // April — keep 0415
            name("20260510"),                   // May — keep
            name("20260701"), name("20260702"), name("20260703"),
            name("20260704"), name("20260705"), // July — newest 5
        )
        val toDelete = BackupPruner.selectBackupsToDelete(names)
        assertEquals(setOf(name("20260401")), toDelete)
    }

    @Test
    fun `drops monthly archives beyond month limit`() {
        // 14 months, one backup each; newest 5 kept as recent, 12 newest months kept as monthly
        val names = (1..12).map { name("2025${"%02d".format(it)}15") } +
            listOf(name("20260115"), name("20260215"))
        val toDelete = BackupPruner.selectBackupsToDelete(names)
        assertEquals(setOf(name("20250115"), name("20250215")), toDelete)
    }

    @Test
    fun `never deletes foreign file names`() {
        val names = listOf(
            "notes.txt",
            "kairos-backup-garbage.zip",
            "kairos-backup-20260101-120000.zip.bak",
        ) + (1..9).map { name("2026070$it") }
        val toDelete = BackupPruner.selectBackupsToDelete(names)
        assertTrue(toDelete.none { !it.matches(Regex("""kairos-backup-\d{8}-\d{6}\.zip""")) })
    }
}
