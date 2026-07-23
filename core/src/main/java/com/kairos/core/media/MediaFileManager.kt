package com.kairos.core.media

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.kairos.core.model.MediaType
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Resolves and manages files under app's external media root:
 *   getExternalFilesDir(Pictures)/kairos/cases/{caseId}/{file}
 *
 * Database stores RELATIVE paths (e.g. "cases/42/1727384910_abc.jpg"). This class joins them.
 */
@Singleton
class MediaFileManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /** Root directory: <externalFilesDir>/Pictures/kairos */
    private val mediaRoot: File by lazy {
        val pictures = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            ?: context.filesDir
        File(pictures, "kairos").apply { if (!exists()) mkdirs() }
    }

    fun resolve(relativePath: String): File = File(mediaRoot, relativePath)

    /** Reserve a fresh file path under cases/{caseId}/ for new media of [type]. */
    fun newCaseMediaFile(caseId: Long, type: MediaType, originalExtension: String? = null): File {
        val dir = File(mediaRoot, "cases/$caseId").apply { if (!exists()) mkdirs() }
        val ext = when (type) {
            MediaType.IMAGE -> "jpg"
            MediaType.VIDEO -> "mp4"
            MediaType.AUDIO -> "m4a"
            MediaType.FILE -> originalExtension ?: "bin"
        }
        val name = "${System.currentTimeMillis()}_${UUID.randomUUID()}.$ext"
        return File(dir, name)
    }

    /** Convert absolute file under mediaRoot to relative path stored in DB. */
    fun toRelative(file: File): String {
        val rootPath = mediaRoot.absolutePath
        val absPath = file.absolutePath
        if (!absPath.startsWith(rootPath)) {
            // File is outside media root (e.g. after storage path change). Return basename as fallback.
            android.util.Log.w("MediaFileManager", "File outside media root: $absPath")
            return file.name
        }
        return absPath.substring(rootPath.length + 1).replace(File.separatorChar, '/')
    }

    fun delete(relativePath: String): Boolean = resolve(relativePath).takeIf { it.exists() }?.delete() ?: false

    /** Delete all media for a case (call after hard-purging case). */
    fun deleteCaseDir(caseId: Long): Boolean {
        val dir = File(mediaRoot, "cases/$caseId")
        return dir.deleteRecursively()
    }

    /** Wrap file as content:// URI via FileProvider for camera intent or sharing. */
    fun contentUriFor(file: File): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

    fun rootDir(): File = mediaRoot
}
