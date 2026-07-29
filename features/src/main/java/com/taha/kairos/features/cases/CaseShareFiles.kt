package com.taha.kairos.features.cases

import com.taha.kairos.core.model.Case
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

internal fun Case.shareFileStem(): String {
    val patientName = patient?.name
        ?.sanitizeShareFileComponent()
        ?.ifBlank { "patient" }
        ?: "patient"
    return "${patientName}_case_$id"
}

internal fun uniqueShareFile(
    directory: File,
    stem: String,
    extension: String,
): File {
    directory.mkdirs()
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.US).format(Date())
    val uniqueSuffix = UUID.randomUUID().toString().take(8)
    return File(directory, "${stem}_${timestamp}_$uniqueSuffix.$extension")
}

internal fun pruneExpiredShareFiles(
    directory: File,
    nowMillis: Long = System.currentTimeMillis(),
) {
    val cutoff = nowMillis - SHARE_FILE_RETENTION_MILLIS
    directory.listFiles()
        ?.asSequence()
        ?.filter { it.isFile && it.lastModified() < cutoff }
        ?.forEach(File::delete)
}

internal fun publishPartialFile(partial: File, destination: File) {
    if (partial.renameTo(destination)) return

    try {
        partial.copyTo(destination, overwrite = false)
        partial.delete()
    } catch (error: Exception) {
        destination.delete()
        throw IOException("Could not finish the shared file.", error)
    }
}

private fun String.sanitizeShareFileComponent(): String =
    replace(Regex("[^\\p{L}\\p{N}._-]+"), "_")
        .trim('_', '.', ' ')
        .truncateUtf8(MAX_SHARE_STEM_COMPONENT_BYTES)

private const val SHARE_FILE_RETENTION_MILLIS = 24L * 60L * 60L * 1_000L
private const val MAX_SHARE_STEM_COMPONENT_BYTES = 80

internal fun String.truncateUtf8(maxBytes: Int): String {
    require(maxBytes >= 0)
    var byteCount = 0
    var endIndex = 0
    while (endIndex < length) {
        val codePoint = codePointAt(endIndex)
        val character = String(Character.toChars(codePoint))
        val characterBytes = character.toByteArray(Charsets.UTF_8).size
        if (byteCount + characterBytes > maxBytes) break
        byteCount += characterBytes
        endIndex += Character.charCount(codePoint)
    }
    return substring(0, endIndex)
}
