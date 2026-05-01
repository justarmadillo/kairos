package com.kairos.data.backup

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.kairos.core.media.MediaFileManager
import com.kairos.core.repository.BackupRepository
import com.kairos.core.repository.BackupResult
import com.kairos.core.repository.RestoreResult
import com.kairos.data.db.KairosDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BackupEngine @Inject constructor(
    @ApplicationContext private val context: Context,
    private val db: KairosDatabase,
    private val mediaFileManager: MediaFileManager,
) : BackupRepository {
    /**
     * Export DB + media to a zip file in [folderUri].
     * Returns BackupResult with success flag and timestamp.
     */
    override suspend fun export(folderUri: String): BackupResult = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        try {
            // 1. Checkpoint WAL — log warning if not fully flushed but continue
            db.openHelper.writableDatabase.query("PRAGMA wal_checkpoint(FULL)").use { cursor ->
                if (cursor.moveToFirst()) {
                    val rc   = cursor.getInt(0)  // 0=OK, 1=SQLITE_BUSY
                    val log  = cursor.getInt(1)  // frames in WAL
                    val ckpt = cursor.getInt(2)  // frames checkpointed
                    if (rc != 0 || log != ckpt) {
                        android.util.Log.w("BackupEngine",
                            "WAL partially checkpointed rc=$rc log=$log ckpt=$ckpt — backup may miss recent writes")
                    }
                }
            }

            // 2. Resolve destination folder via SAF
            val folder = DocumentFile.fromTreeUri(context, Uri.parse(folderUri))
                ?: return@withContext BackupResult(false, now, "Backup folder not accessible")

            val dateStamp = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault()).format(Date(now))
            val zipFile = folder.createFile("application/zip", "kairos-backup-$dateStamp.zip")
                ?: return@withContext BackupResult(false, now, "Cannot create zip file in backup folder")

            context.contentResolver.openOutputStream(zipFile.uri)?.use { out ->
                ZipOutputStream(out.buffered()).use { zip ->

                    // 3. Add DB file
                    val dbFile = context.getDatabasePath("kairos.db")
                    if (dbFile.exists()) {
                        zip.putNextEntry(ZipEntry("kairos.db"))
                        dbFile.inputStream().use { it.copyTo(zip) }
                        zip.closeEntry()
                    }

                    // Also include WAL/SHM if present
                    listOf("kairos.db-wal", "kairos.db-shm").forEach { name ->
                        val f = File(dbFile.parent, name)
                        if (f.exists()) {
                            zip.putNextEntry(ZipEntry(name))
                            f.inputStream().use { it.copyTo(zip) }
                            zip.closeEntry()
                        }
                    }

                    // 4. Add media files
                    val mediaRoot = mediaFileManager.rootDir()
                    if (mediaRoot.exists()) {
                        mediaRoot.walkTopDown().filter { it.isFile }.forEach { file ->
                            val relative = "media/" + file.relativeTo(mediaRoot).path.replace(File.separatorChar, '/')
                            zip.putNextEntry(ZipEntry(relative))
                            file.inputStream().use { it.copyTo(zip) }
                            zip.closeEntry()
                        }
                    }

                    // 5. Add manifest
                    val manifest = JSONObject().apply {
                        put("app_version", appVersionName())
                        put("db_schema_version", db.openHelper.readableDatabase.version)
                        put("export_timestamp", now)
                        put("platform", "android")
                    }.toString(2)
                    zip.putNextEntry(ZipEntry("manifest.json"))
                    zip.write(manifest.toByteArray())
                    zip.closeEntry()
                }
            } ?: return@withContext BackupResult(false, now, "Cannot open output stream")

            BackupResult(success = true, timestampMs = now)
        } catch (e: Exception) {
            BackupResult(success = false, timestampMs = now, error = e.message)
        }
    }

    override suspend fun restore(zipUri: String): RestoreResult = withContext(Dispatchers.IO) {
        val tempDir = File(context.cacheDir, "restore_${System.currentTimeMillis()}")
        try {
            // 1. Open and extract zip to temp dir
            val inputStream = context.contentResolver.openInputStream(Uri.parse(zipUri))
                ?: return@withContext RestoreResult(false, "Cannot open backup file")

            tempDir.mkdirs()

            ZipInputStream(inputStream.buffered()).use { zip ->
                var entry = zip.nextEntry
                while (entry != null) {
                    val outFile = File(tempDir, entry.name.replace('\\', '/'))
                    // Zip-slip guard: reject paths escaping tempDir
                    if (!outFile.canonicalPath.startsWith(tempDir.canonicalPath + File.separator)) {
                        zip.closeEntry()
                        entry = zip.nextEntry
                        continue
                    }
                    if (entry.isDirectory) {
                        outFile.mkdirs()
                    } else {
                        outFile.parentFile?.mkdirs()
                        outFile.outputStream().use { zip.copyTo(it) }
                    }
                    zip.closeEntry()
                    entry = zip.nextEntry
                }
            }

            // 2. Validate manifest
            val manifestFile = File(tempDir, "manifest.json")
            if (!manifestFile.exists())
                return@withContext RestoreResult(false, "Invalid backup: missing manifest.json")
            val manifest = JSONObject(manifestFile.readText())
            if (manifest.optString("platform") != "android")
                return@withContext RestoreResult(false, "Incompatible backup (platform=${manifest.optString("platform")})")

            // 3. Validate DB is present
            val backupDb = File(tempDir, "kairos.db")
            if (!backupDb.exists())
                return@withContext RestoreResult(false, "Invalid backup: missing kairos.db")

            // 4. Close DB to release file handles before overwriting
            db.close()

            // 5. Replace DB file
            val dbFile = context.getDatabasePath("kairos.db")
            dbFile.parentFile?.mkdirs()
            backupDb.copyTo(dbFile, overwrite = true)

            // 6. Remove stale WAL/SHM so restored DB opens cleanly
            File(dbFile.parent, "kairos.db-wal").delete()
            File(dbFile.parent, "kairos.db-shm").delete()

            // 7. Replace media files
            val mediaRoot = mediaFileManager.rootDir()
            val tempMedia = File(tempDir, "media")
            if (tempMedia.exists()) {
                mediaRoot.deleteRecursively()
                tempMedia.copyRecursively(mediaRoot, overwrite = true)
            }

            RestoreResult(success = true)
        } catch (e: Exception) {
            RestoreResult(false, e.message)
        } finally {
            try { tempDir.deleteRecursively() } catch (_: Exception) {}
        }
    }

    private fun appVersionName(): String = try {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "unknown"
    } catch (_: Exception) {
        "unknown"
    }
}
