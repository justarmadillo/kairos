package com.taha.kairos.features.cases

import android.content.Context
import com.taha.kairos.core.media.MediaFileManager
import com.taha.kairos.core.model.Case
import com.taha.kairos.core.model.MediaItem
import com.taha.kairos.core.model.MediaType
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext

data class CaseZipExportResult(
    val file: File,
    val skippedAttachmentCount: Int,
)

internal data class CaseArchiveAttachment(
    val source: File,
    val requestedName: String,
    val mediaType: MediaType,
)

class CaseZipExporter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val pdfExporter: CasePdfExporter,
    private val mediaFileManager: MediaFileManager,
) {
    suspend fun export(case: Case): CaseZipExportResult = withContext(Dispatchers.IO) {
        val exportContext = currentCoroutineContext()
        val report = pdfExporter.export(case, includePhotos = false)
        val outDir = File(context.cacheDir, "case_shares")
        pruneExpiredShareFiles(outDir)
        val output = uniqueShareFile(outDir, case.shareFileStem(), "zip")
        val partial = File(outDir, ".${output.name}.partial")
        val unavailableNames = mutableListOf<String>()
        val attachments = mutableListOf<CaseArchiveAttachment>()

        try {
            case.media
                .sortedWith(compareBy<MediaItem> { it.createdAt }.thenBy { it.id })
                .forEach { media ->
                    val source = File(media.filePath)
                    val displayName = media.originalFileName
                        ?.takeIf { it.isNotBlank() }
                        ?: source.name
                    val readableSource = canonicalReadableFileWithinRoot(
                        mediaFileManager.rootDir(),
                        source,
                    )
                    if (readableSource != null) {
                        attachments += CaseArchiveAttachment(
                            source = readableSource,
                            requestedName = displayName,
                            mediaType = media.mediaType,
                        )
                    } else {
                        unavailableNames += sanitizeArchiveFileName(displayName)
                    }
                }

            CaseArchiveWriter.write(
                destination = partial,
                reportFile = report,
                reportEntryName = "${case.shareFileStem()}_report.pdf",
                attachments = attachments,
                unavailableAttachmentNames = unavailableNames,
                onChunk = { exportContext.ensureActive() },
            )
            exportContext.ensureActive()
            publishPartialFile(partial, output)
            exportContext.ensureActive()

            CaseZipExportResult(
                file = output,
                skippedAttachmentCount = unavailableNames.size,
            )
        } catch (error: Exception) {
            output.delete()
            throw error
        } finally {
            partial.delete()
            report.delete()
        }
    }
}

internal object CaseArchiveWriter {
    private const val COPY_BUFFER_SIZE = 64 * 1024

    fun write(
        destination: File,
        reportFile: File,
        reportEntryName: String,
        attachments: List<CaseArchiveAttachment>,
        unavailableAttachmentNames: List<String> = emptyList(),
        onChunk: () -> Unit = {},
    ) {
        require(reportFile.isFile && reportFile.canRead()) {
            "The case report could not be created."
        }
        require(attachments.all { it.source.isFile && it.source.canRead() }) {
            "An attachment became unavailable."
        }

        destination.parentFile?.mkdirs()
        try {
            ZipOutputStream(
                BufferedOutputStream(FileOutputStream(destination)),
            ).use { zip ->
                zip.writeFileEntry(
                    entryName = ensureExtension(
                        sanitizeArchiveFileName(reportEntryName, fallback = "case_report"),
                        "pdf",
                    ),
                    source = reportFile,
                    onChunk = onChunk,
                )

                attachments.forEachIndexed { index, attachment ->
                    val safeName = ensureExtension(
                        sanitizeArchiveFileName(attachment.requestedName),
                        attachment.source.extension,
                    )
                    val sequence = (index + 1).toString().padStart(3, '0')
                    val entryName =
                        "attachments/${attachment.mediaType.archiveFolder}/" +
                            "${sequence}_$safeName"
                    zip.writeFileEntry(entryName, attachment.source, onChunk)
                }

                if (unavailableAttachmentNames.isNotEmpty()) {
                    val text = buildString {
                        appendLine("The following attachments were unavailable and were not included:")
                        unavailableAttachmentNames.forEach { name ->
                            append("- ")
                            appendLine(sanitizeArchiveFileName(name))
                        }
                    }
                    zip.putNextEntry(ZipEntry("unavailable_attachments.txt"))
                    try {
                        zip.write(text.toByteArray(Charsets.UTF_8))
                    } finally {
                        zip.closeEntry()
                    }
                }
            }
        } catch (error: Exception) {
            destination.delete()
            throw error
        }
    }

    private fun ZipOutputStream.writeFileEntry(
        entryName: String,
        source: File,
        onChunk: () -> Unit,
    ) {
        putNextEntry(ZipEntry(entryName).apply { time = source.lastModified() })
        try {
            BufferedInputStream(source.inputStream()).use { input ->
                val buffer = ByteArray(COPY_BUFFER_SIZE)
                while (true) {
                    onChunk()
                    val count = input.read(buffer)
                    if (count < 0) break
                    write(buffer, 0, count)
                }
            }
        } finally {
            closeEntry()
        }
    }
}

internal fun canonicalReadableFileWithinRoot(root: File, candidate: File): File? =
    runCatching {
        val canonicalRoot = root.canonicalFile
        val canonicalCandidate = candidate.canonicalFile
        canonicalCandidate.takeIf {
            canonicalCandidate != canonicalRoot &&
            canonicalCandidate.toPath().startsWith(canonicalRoot.toPath()) &&
            canonicalCandidate.isFile &&
            canonicalCandidate.canRead()
        }
    }.getOrNull()

internal fun isReadableFileWithinRoot(root: File, candidate: File): Boolean =
    canonicalReadableFileWithinRoot(root, candidate) != null

internal fun sanitizeArchiveFileName(
    rawName: String,
    fallback: String = "attachment",
): String {
    val basename = rawName
        .substringAfterLast('/')
        .substringAfterLast('\\')
    var safeName = basename
        .replace(Regex("[\\u0000-\\u001F\\u007F<>:\"|?*]"), "_")
        .trim()
        .trim('.')
        .ifBlank { fallback }

    val stem = safeName.substringBeforeLast('.', safeName).uppercase(Locale.ROOT)
    if (
        stem in setOf("CON", "PRN", "AUX", "NUL") ||
        stem.matches(Regex("COM[1-9]|LPT[1-9]"))
    ) {
        safeName = "_$safeName"
    }

    if (safeName.toByteArray(Charsets.UTF_8).size > MAX_ARCHIVE_FILE_NAME_BYTES) {
        val dotIndex = safeName.lastIndexOf('.')
        val extension = if (
            dotIndex in 1 until safeName.lastIndex &&
            safeName.substring(dotIndex).toByteArray(Charsets.UTF_8).size <=
                MAX_ARCHIVE_EXTENSION_BYTES
        ) {
            safeName.substring(dotIndex)
        } else {
            ""
        }
        val availableStemBytes = MAX_ARCHIVE_FILE_NAME_BYTES -
            extension.toByteArray(Charsets.UTF_8).size
        safeName = safeName
            .removeSuffix(extension)
            .truncateUtf8(availableStemBytes)
            .trimEnd('.', ' ') + extension
    }

    return safeName.ifBlank { fallback }
}

private fun ensureExtension(fileName: String, extension: String): String {
    if (fileName.substringAfterLast('.', "").isNotBlank()) return fileName
    val safeExtension = extension
        .lowercase(Locale.ROOT)
        .filter(Char::isLetterOrDigit)
        .truncateUtf8(MAX_ARCHIVE_EXTENSION_BYTES - 1)
    return if (safeExtension.isBlank()) {
        fileName
    } else {
        sanitizeArchiveFileName("$fileName.$safeExtension")
    }
}

private val MediaType.archiveFolder: String
    get() = when (this) {
        MediaType.IMAGE -> "images"
        MediaType.VIDEO -> "videos"
        MediaType.AUDIO -> "audio"
        MediaType.FILE -> "files"
    }

private const val MAX_ARCHIVE_FILE_NAME_BYTES = 120
private const val MAX_ARCHIVE_EXTENSION_BYTES = 16
