package com.taha.kairos.features.cases

import com.taha.kairos.core.model.MediaType
import java.io.File
import java.io.IOException
import java.util.Locale
import java.util.zip.ZipFile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class CaseArchiveWriterTest {

    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun writesReportAndEveryAttachmentTypeWithSafeNames() {
        val mediaRoot = temporaryFolder.newFolder("media")
        val report = temporaryFolder.newFile("generated.pdf").apply {
            writeText("case text")
        }
        val image = mediaRoot.fileWithBytes("image.jpg", "image")
        val video = mediaRoot.fileWithBytes("video.mp4", "video")
        val audio = mediaRoot.fileWithBytes("audio.m4a", "audio")
        val document = mediaRoot.fileWithBytes("document.pdf", "document")
        val archive = File(temporaryFolder.root, "case.zip")

        CaseArchiveWriter.write(
            destination = archive,
            reportFile = report,
            reportEntryName = "../../case_report.pdf",
            attachments = listOf(
                CaseArchiveAttachment(image, "../../scan", MediaType.IMAGE),
                CaseArchiveAttachment(video, "scan.JPG", MediaType.VIDEO),
                CaseArchiveAttachment(audio, "folder\\voice.m4a", MediaType.AUDIO),
                CaseArchiveAttachment(document, "report?.pdf", MediaType.FILE),
            ),
        )

        val contents = archive.readZipContents()
        assertEquals(
            setOf(
                "case_report.pdf",
                "attachments/images/001_scan.jpg",
                "attachments/videos/002_scan.JPG",
                "attachments/audio/003_voice.m4a",
                "attachments/files/004_report_.pdf",
            ),
            contents.keys,
        )
        assertEquals("case text", contents.getValue("case_report.pdf").decodeToString())
        assertEquals("image", contents.getValue("attachments/images/001_scan.jpg").decodeToString())
        assertEquals("video", contents.getValue("attachments/videos/002_scan.JPG").decodeToString())
        assertEquals("audio", contents.getValue("attachments/audio/003_voice.m4a").decodeToString())
        assertEquals("document", contents.getValue("attachments/files/004_report_.pdf").decodeToString())
        assertTrue(contents.keys.none { it.startsWith("/") || ".." in it })
    }

    @Test
    fun numericPrefixesKeepCaseOnlyDuplicateNamesUnique() {
        val mediaRoot = temporaryFolder.newFolder("duplicates")
        val report = temporaryFolder.newFile("report.pdf")
        val first = mediaRoot.fileWithBytes("first.pdf", "first")
        val second = mediaRoot.fileWithBytes("second.pdf", "second")
        val archive = File(temporaryFolder.root, "duplicates.zip")

        CaseArchiveWriter.write(
            destination = archive,
            reportFile = report,
            reportEntryName = "report.pdf",
            attachments = listOf(
                CaseArchiveAttachment(first, "Report.pdf", MediaType.FILE),
                CaseArchiveAttachment(second, "report.PDF", MediaType.FILE),
            ),
        )

        val attachmentEntries = archive.readZipContents().keys
            .filter { it.startsWith("attachments/") }
        assertEquals(2, attachmentEntries.size)
        assertEquals(
            attachmentEntries.size,
            attachmentEntries.map { it.lowercase(Locale.ROOT) }.toSet().size,
        )
    }

    @Test
    fun recordsUnavailableAttachmentsWithoutLeakingPaths() {
        val report = temporaryFolder.newFile("report.pdf")
        val archive = File(temporaryFolder.root, "missing.zip")

        CaseArchiveWriter.write(
            destination = archive,
            reportFile = report,
            reportEntryName = "report.pdf",
            attachments = emptyList(),
            unavailableAttachmentNames = listOf("../../private.pdf", "folder\\missing.docx"),
        )

        val contents = archive.readZipContents()
        val notice = contents.getValue("unavailable_attachments.txt").decodeToString()
        assertTrue("private.pdf" in notice)
        assertTrue("missing.docx" in notice)
        assertFalse("../" in notice)
        assertFalse("folder\\" in notice)
    }

    @Test
    fun sanitizesReservedBlankUnicodeAndLongNames() {
        assertEquals("x.pdf", sanitizeArchiveFileName("../../x.pdf"))
        assertEquals("b.pdf", sanitizeArchiveFileName("a\\b.pdf"))
        assertEquals("attachment", sanitizeArchiveFileName(".."))
        assertEquals("_CON.txt", sanitizeArchiveFileName("CON.txt"))
        assertEquals("صورة_طبية.pdf", sanitizeArchiveFileName("صورة_طبية.pdf"))

        val longName = sanitizeArchiveFileName("${"界".repeat(180)}.pdf")
        assertTrue(longName.toByteArray(Charsets.UTF_8).size <= 120)
        assertTrue(longName.endsWith(".pdf"))
        assertFalse('\uFFFD' in longName)
    }

    @Test
    fun acceptsOnlyReadableFilesInsideTheMediaRoot() {
        val mediaRoot = temporaryFolder.newFolder("safe-root")
        val inside = mediaRoot.fileWithBytes("inside.txt", "inside")
        val outside = temporaryFolder.newFile("outside.txt")
        val directory = File(mediaRoot, "directory").apply { mkdirs() }

        assertTrue(isReadableFileWithinRoot(mediaRoot, inside))
        assertFalse(isReadableFileWithinRoot(mediaRoot, outside))
        assertFalse(isReadableFileWithinRoot(mediaRoot, directory))
        assertFalse(isReadableFileWithinRoot(mediaRoot, File(mediaRoot, "../outside.txt")))
    }

    @Test
    fun failedWriteDeletesPartialArchive() {
        val report = temporaryFolder.newFile("report.pdf").apply {
            writeText("report")
        }
        val archive = File(temporaryFolder.root, "partial.zip")
        var chunkChecks = 0

        try {
            CaseArchiveWriter.write(
                destination = archive,
                reportFile = report,
                reportEntryName = "report.pdf",
                attachments = emptyList(),
                onChunk = {
                    chunkChecks += 1
                    if (chunkChecks == 2) throw IOException("simulated failure")
                },
            )
        } catch (_: IOException) {
            // Expected.
        }

        assertTrue(chunkChecks >= 2)
        assertFalse(archive.exists())
    }

    @Test
    fun prunesOnlyShareFilesOlderThanRetentionWindow() {
        val shares = temporaryFolder.newFolder("case_shares")
        val now = 2_000_000_000_000L
        val expired = shares.fileWithBytes("expired.zip", "old").apply {
            setLastModified(now - 25L * 60L * 60L * 1_000L)
        }
        val recent = shares.fileWithBytes("recent.zip", "new").apply {
            setLastModified(now - 23L * 60L * 60L * 1_000L)
        }
        val nestedDirectory = File(shares, "keep-directory").apply { mkdirs() }

        pruneExpiredShareFiles(shares, nowMillis = now)

        assertFalse(expired.exists())
        assertTrue(recent.exists())
        assertTrue(nestedDirectory.exists())
    }

    private fun File.fileWithBytes(name: String, contents: String): File =
        File(this, name).apply { writeText(contents) }

    private fun File.readZipContents(): Map<String, ByteArray> =
        ZipFile(this).use { zip ->
            zip.entries().asSequence().associate { entry ->
                entry.name to zip.getInputStream(entry).use { it.readBytes() }
            }
        }
}
