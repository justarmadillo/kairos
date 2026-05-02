package com.kairos.data.backup

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import com.kairos.core.media.MediaFileManager
import com.kairos.core.repository.BackupRepository
import com.kairos.core.repository.BackupResult
import com.kairos.core.repository.DataSafetyCoordinator
import com.kairos.core.repository.RestoreResult
import com.kairos.data.db.KairosDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.security.MessageDigest
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
    private val dataSafetyCoordinator: DataSafetyCoordinator,
) : BackupRepository {

    override suspend fun export(folderUri: String): BackupResult =
        dataSafetyCoordinator.withDataLock {
            withContext(Dispatchers.IO) {
                exportLocked(folderUri)
            }
        }

    override suspend fun vacuumDatabase() =
        dataSafetyCoordinator.withDataLock {
            withContext(Dispatchers.IO) {
                db.openHelper.writableDatabase.execSQL("VACUUM")
            }
        }

    override suspend fun restore(zipUri: String): RestoreResult =
        dataSafetyCoordinator.withDataLock {
            withContext(Dispatchers.IO) {
                restoreLocked(zipUri)
            }
        }

    private fun exportLocked(folderUri: String): BackupResult {
        val now = System.currentTimeMillis()
        return try {
            val folder = DocumentFile.fromTreeUri(context, Uri.parse(folderUri))
                ?: return BackupResult(false, now, "Backup folder not accessible")
            if (!folder.canWrite()) {
                return BackupResult(false, now, "Backup folder is not writable")
            }

            ensureWalCheckpointed()

            val dbFile = context.getDatabasePath(DB_NAME)
            if (!dbFile.exists()) {
                return BackupResult(false, now, "Database file not found")
            }
            ensureNoPendingWalFrames(dbFile)

            val dateStamp = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.US).format(Date(now))
            val zipFile = folder.createFile("application/zip", "kairos-backup-$dateStamp.zip")
                ?: return BackupResult(false, now, "Cannot create zip file in backup folder")

            val fileManifest = JSONObject()
            var totalBytes = 0L
            context.contentResolver.openOutputStream(zipFile.uri, "w")?.use { out ->
                ZipOutputStream(out.buffered()).use { zip ->
                    totalBytes += addFileEntry(zip, dbFile, DB_NAME, fileManifest)

                    val mediaRoot = mediaFileManager.rootDir()
                    if (mediaRoot.exists()) {
                        mediaRoot.walkTopDown()
                            .filter { it.isFile }
                            .forEach { file ->
                                val relative = file.relativeTo(mediaRoot).path
                                    .replace(File.separatorChar, '/')
                                if (!relative.startsWith(TEMP_CASE_MEDIA_PREFIX)) {
                                    totalBytes += addFileEntry(
                                        zip = zip,
                                        file = file,
                                        entryName = "media/$relative",
                                        fileManifest = fileManifest,
                                    )
                                }
                            }
                    }

                    // Include DataStore preferences so settings survive restore
                    val prefsFile = File(context.filesDir, "datastore/$PREFS_NAME")
                    if (prefsFile.exists()) {
                        totalBytes += addFileEntry(zip, prefsFile, PREFS_NAME, fileManifest)
                    }

                    val manifest = JSONObject().apply {
                        put("backup_format_version", BACKUP_FORMAT_VERSION)
                        put("app_version", appVersionName())
                        put("db_schema_version", currentDbSchemaVersion())
                        put("export_timestamp", now)
                        put("platform", "android")
                        put("database", DB_NAME)
                        put("total_payload_bytes", totalBytes)
                        put("files", fileManifest)
                    }.toString(2)
                    zip.putNextEntry(ZipEntry(MANIFEST_NAME))
                    zip.write(manifest.toByteArray(Charsets.UTF_8))
                    zip.closeEntry()
                }
            } ?: return BackupResult(false, now, "Cannot open output stream")

            BackupResult(success = true, timestampMs = now)
        } catch (e: Exception) {
            BackupResult(success = false, timestampMs = now, error = e.message ?: "Backup failed")
        }
    }

    private fun restoreLocked(zipUri: String): RestoreResult {
        val now = System.currentTimeMillis()
        val tempDir = File(context.cacheDir, "restore_$now")
        val extractDir = File(tempDir, "extracted")
        var dbBackups: List<Pair<File, File>> = emptyList()
        var oldMediaBackup: File? = null
        var oldPrefsBackup: File? = null
        var liveFilesTouched = false

        return try {
            extractDir.mkdirs()
            val extracted = extractBackup(Uri.parse(zipUri), extractDir)
            validateManifest(extracted.manifest)
            validateExtractedFiles(extractDir, extracted)

            val backupDb = File(extractDir, DB_NAME)
            if (!backupDb.exists()) {
                return RestoreResult(false, "Invalid backup: missing $DB_NAME")
            }
            validateBackupDatabase(backupDb, extracted.manifest.optInt("db_schema_version", -1))

            val dbFile = context.getDatabasePath(DB_NAME)
            val dbDir = dbFile.parentFile ?: return RestoreResult(false, "Database folder unavailable")
            dbDir.mkdirs()

            val newDb = File(dbDir, "$DB_NAME.restore_tmp_$now")
            backupDb.copyTo(newDb, overwrite = true)
            if (newDb.length() != backupDb.length()) {
                newDb.delete()
                return RestoreResult(false, "Restored database copy was incomplete")
            }

            val mediaRoot = mediaFileManager.rootDir()
            val mediaParent = mediaRoot.parentFile
                ?: return RestoreResult(false, "Media folder unavailable")
            val tempMedia = File(extractDir, "media")
            val newMediaRoot = File(mediaParent, "${mediaRoot.name}.restore_new_$now")
            if (newMediaRoot.exists()) newMediaRoot.deleteRecursively()
            if (tempMedia.exists()) {
                tempMedia.copyRecursively(newMediaRoot, overwrite = true)
            } else {
                newMediaRoot.mkdirs()
            }

            db.close()

            dbBackups = moveCurrentDatabaseAside(dbFile, now)
            liveFilesTouched = true
            moveReplacing(newDb, dbFile)
            File(dbDir, "$DB_NAME-wal").delete()
            File(dbDir, "$DB_NAME-shm").delete()

            if (mediaRoot.exists()) {
                oldMediaBackup = File(mediaParent, "${mediaRoot.name}.restore_old_$now")
                moveReplacing(mediaRoot, oldMediaBackup)
            }
            moveReplacing(newMediaRoot, mediaRoot)

            // Restore DataStore preferences if present in backup
            val extractedPrefs = File(extractDir, PREFS_NAME)
            if (extractedPrefs.exists()) {
                val prefsDir = File(context.filesDir, "datastore")
                prefsDir.mkdirs()
                val prefsTarget = File(prefsDir, PREFS_NAME)
                if (prefsTarget.exists()) {
                    oldPrefsBackup = File(prefsDir, "$PREFS_NAME.restore_old_$now")
                    prefsTarget.copyTo(oldPrefsBackup!!, overwrite = true)
                }
                extractedPrefs.copyTo(prefsTarget, overwrite = true)
            }

            dbBackups.forEach { (_, backup) -> backup.delete() }
            oldMediaBackup?.deleteRecursively()
            oldPrefsBackup?.delete()
            RestoreResult(success = true)
        } catch (e: Exception) {
            if (liveFilesTouched) {
                rollbackRestore(dbBackups, oldMediaBackup, oldPrefsBackup)
            }
            RestoreResult(false, e.message ?: "Restore failed")
        } finally {
            try {
                tempDir.deleteRecursively()
            } catch (_: Exception) {
            }
        }
    }

    private fun ensureWalCheckpointed() {
        db.openHelper.writableDatabase.query("PRAGMA wal_checkpoint(TRUNCATE)").use { cursor ->
            if (cursor.moveToFirst()) {
                val rc = cursor.getInt(0)
                val log = cursor.getInt(1)
                val checkpointed = cursor.getInt(2)
                if (rc != 0 || log != checkpointed) {
                    error("Database is busy; try backup again in a moment")
                }
            }
        }
    }

    private fun ensureNoPendingWalFrames(dbFile: File) {
        val wal = File(dbFile.parentFile, "$DB_NAME-wal")
        if (wal.exists() && wal.length() > 0L) {
            error("Database still has pending WAL data; try backup again")
        }
    }

    private fun addFileEntry(
        zip: ZipOutputStream,
        file: File,
        entryName: String,
        fileManifest: JSONObject,
    ): Long {
        val digest = MessageDigest.getInstance("SHA-256")
        var bytes = 0L
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)

        zip.putNextEntry(ZipEntry(entryName).apply { time = file.lastModified() })
        file.inputStream().buffered().use { input ->
            while (true) {
                val read = input.read(buffer)
                if (read == -1) break
                zip.write(buffer, 0, read)
                digest.update(buffer, 0, read)
                bytes += read
            }
        }
        zip.closeEntry()

        fileManifest.put(
            entryName,
            JSONObject().apply {
                put("size", bytes)
                put("sha256", digest.digest().toHex())
            },
        )
        return bytes
    }

    private fun extractBackup(zipUri: Uri, extractDir: File): ExtractedBackup {
        val inputStream = context.contentResolver.openInputStream(zipUri)
            ?: error("Cannot open backup file")
        val entries = linkedSetOf<String>()
        var totalBytes = 0L

        ZipInputStream(inputStream.buffered()).use { zip ->
            while (true) {
                val entry = zip.nextEntry ?: break
                val entryName = normalizeZipEntryName(entry)
                if (!entries.add(entryName)) error("Invalid backup: duplicate entry $entryName")
                if (entries.size > MAX_BACKUP_ENTRIES) error("Invalid backup: too many files")

                val outFile = File(extractDir, entryName)
                ensureInsideDirectory(extractDir, outFile)
                if (entry.isDirectory) {
                    outFile.mkdirs()
                } else {
                    outFile.parentFile?.mkdirs()
                    totalBytes += copyZipEntry(zip, outFile, entryName, totalBytes)
                    if (totalBytes > MAX_BACKUP_BYTES) {
                        error("Invalid backup: backup is too large")
                    }
                }
                zip.closeEntry()
            }
        }

        val manifestFile = File(extractDir, MANIFEST_NAME)
        if (!manifestFile.exists()) {
            error("Invalid backup: missing $MANIFEST_NAME")
        }
        return ExtractedBackup(JSONObject(manifestFile.readText()), entries)
    }

    private fun normalizeZipEntryName(entry: ZipEntry): String {
        val name = entry.name.replace('\\', '/')
        if (name.isBlank() || name.startsWith("/") || name.contains("//")) {
            error("Invalid backup entry: ${entry.name}")
        }
        if (name.split('/').any { it == ".." }) {
            error("Invalid backup entry: ${entry.name}")
        }
        return name
    }

    private fun ensureInsideDirectory(root: File, candidate: File) {
        val rootPath = root.canonicalFile.toPath()
        val candidatePath = candidate.canonicalFile.toPath()
        if (!candidatePath.startsWith(rootPath)) {
            error("Invalid backup entry outside restore folder")
        }
    }

    private fun copyZipEntry(
        zip: ZipInputStream,
        outFile: File,
        entryName: String,
        alreadyExtractedBytes: Long,
    ): Long {
        var entryBytes = 0L
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        outFile.outputStream().buffered().use { output ->
            while (true) {
                val read = zip.read(buffer)
                if (read == -1) break
                entryBytes += read
                if (entryBytes > MAX_ENTRY_BYTES) {
                    error("Invalid backup: $entryName is too large")
                }
                if (alreadyExtractedBytes + entryBytes > MAX_BACKUP_BYTES) {
                    error("Invalid backup: backup is too large")
                }
                output.write(buffer, 0, read)
            }
        }
        return entryBytes
    }

    private fun validateManifest(manifest: JSONObject) {
        val formatVersion = manifest.optInt("backup_format_version", -1)
        if (formatVersion <= 0) {
            error("Invalid backup: missing backup format version")
        }
        if (formatVersion > BACKUP_FORMAT_VERSION) {
            error("Backup requires a newer app version before it can be restored")
        }
        if (formatVersion != BACKUP_FORMAT_VERSION) {
            error("Unsupported backup format version: $formatVersion")
        }
        if (manifest.optString("platform") != "android") {
            error("Incompatible backup (platform=${manifest.optString("platform")})")
        }
        if (manifest.optJSONObject("files") == null) {
            error("Invalid backup: missing file checksum manifest")
        }
        val schemaVersion = manifest.optInt("db_schema_version", -1)
        if (schemaVersion <= 0) {
            error("Invalid backup: missing database schema version")
        }
        val currentSchemaVersion = currentDbSchemaVersion()
        if (schemaVersion > currentSchemaVersion) {
            error("Backup requires a newer app version before it can be restored")
        }
    }

    private fun validateExtractedFiles(extractDir: File, extracted: ExtractedBackup) {
        val files = extracted.manifest.optJSONObject("files")
            ?: error("Invalid backup: missing file checksum manifest")
        val declaredNames = mutableSetOf<String>()
        val keys = files.keys()
        while (keys.hasNext()) {
            val entryName = keys.next()
            declaredNames += entryName
            val expected = files.getJSONObject(entryName)
            val file = File(extractDir, entryName)
            ensureInsideDirectory(extractDir, file)
            if (!file.exists() || !file.isFile) {
                error("Invalid backup: missing $entryName")
            }
            val expectedSize = expected.optLong("size", -1L)
            if (expectedSize < 0 || file.length() != expectedSize) {
                error("Invalid backup: size mismatch for $entryName")
            }
            val expectedHash = expected.optString("sha256")
            if (expectedHash.isBlank() || sha256(file) != expectedHash) {
                error("Invalid backup: checksum mismatch for $entryName")
            }
        }

        val actualPayloadEntries = extracted.entries.filter { it != MANIFEST_NAME }.toSet()
        if (actualPayloadEntries != declaredNames) {
            error("Invalid backup: manifest does not match backup contents")
        }
    }

    private fun validateBackupDatabase(backupDb: File, expectedSchemaVersion: Int) {
        val sqlite = SQLiteDatabase.openDatabase(
            backupDb.absolutePath,
            null,
            SQLiteDatabase.OPEN_READWRITE,
        )
        sqlite.use { database ->
            database.rawQuery("PRAGMA wal_checkpoint(TRUNCATE)", null).use { cursor ->
                if (cursor.moveToFirst() && cursor.getInt(0) != 0) {
                    error("Invalid backup: database WAL could not be checkpointed")
                }
            }
            database.rawQuery("PRAGMA quick_check", null).use { cursor ->
                if (!cursor.moveToFirst() || !cursor.getString(0).equals("ok", ignoreCase = true)) {
                    error("Invalid backup: database integrity check failed")
                }
            }
            database.rawQuery("PRAGMA foreign_key_check", null).use { cursor ->
                if (cursor.moveToFirst()) {
                    error("Invalid backup: database contains broken references")
                }
            }
            database.rawQuery("PRAGMA user_version", null).use { cursor ->
                if (!cursor.moveToFirst()) error("Invalid backup: missing database version")
                val dbVersion = cursor.getInt(0)
                if (expectedSchemaVersion > 0 && dbVersion != expectedSchemaVersion) {
                    error("Invalid backup: manifest/database schema mismatch")
                }
                if (dbVersion > currentDbSchemaVersion()) {
                    error("Backup requires a newer app version before it can be restored")
                }
            }
        }
    }

    private fun moveCurrentDatabaseAside(dbFile: File, timestamp: Long): List<Pair<File, File>> {
        val dbDir = dbFile.parentFile ?: return emptyList()
        return listOf(
            dbFile,
            File(dbDir, "$DB_NAME-wal"),
            File(dbDir, "$DB_NAME-shm"),
        ).mapNotNull { current ->
            if (!current.exists()) return@mapNotNull null
            val backup = File(dbDir, "${current.name}.restore_old_$timestamp")
            moveReplacing(current, backup)
            current to backup
        }
    }

    private fun rollbackRestore(
        dbBackups: List<Pair<File, File>>,
        oldMediaBackup: File?,
        oldPrefsBackup: File?,
    ) {
        try {
            if (dbBackups.isNotEmpty()) {
                val dbDir = context.getDatabasePath(DB_NAME).parentFile
                listOf(DB_NAME, "$DB_NAME-wal", "$DB_NAME-shm").forEach { name ->
                    dbDir?.let { File(it, name).delete() }
                }
                dbBackups.forEach { (original, backup) ->
                    if (backup.exists()) moveReplacing(backup, original)
                }
            }

            val mediaRoot = mediaFileManager.rootDir()
            if (oldMediaBackup != null && oldMediaBackup.exists()) {
                if (mediaRoot.exists()) mediaRoot.deleteRecursively()
                moveReplacing(oldMediaBackup, mediaRoot)
            }

            if (oldPrefsBackup != null && oldPrefsBackup.exists()) {
                val prefsTarget = File(context.filesDir, "datastore/$PREFS_NAME")
                oldPrefsBackup.copyTo(prefsTarget, overwrite = true)
                oldPrefsBackup.delete()
            }
        } catch (_: Exception) {
        }
    }

    private fun moveReplacing(source: File, target: File) {
        target.parentFile?.mkdirs()
        try {
            Files.move(
                source.toPath(),
                target.toPath(),
                StandardCopyOption.REPLACE_EXISTING,
                StandardCopyOption.ATOMIC_MOVE,
            )
        } catch (_: AtomicMoveNotSupportedException) {
            Files.move(
                source.toPath(),
                target.toPath(),
                StandardCopyOption.REPLACE_EXISTING,
            )
        }
    }

    private fun currentDbSchemaVersion(): Int =
        db.openHelper.readableDatabase.version

    private fun appVersionName(): String = try {
        context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "unknown"
    } catch (_: Exception) {
        "unknown"
    }

    private fun sha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        file.inputStream().buffered().use { input ->
            while (true) {
                val read = input.read(buffer)
                if (read == -1) break
                digest.update(buffer, 0, read)
            }
        }
        return digest.digest().toHex()
    }

    private fun ByteArray.toHex(): String =
        joinToString(separator = "") { byte -> "%02x".format(byte) }

    private data class ExtractedBackup(
        val manifest: JSONObject,
        val entries: Set<String>,
    )

    private companion object {
        const val DB_NAME = "kairos.db"
        const val MANIFEST_NAME = "manifest.json"
        const val BACKUP_FORMAT_VERSION = 2
        const val PREFS_NAME = "kairos_prefs.preferences_pb"
        const val TEMP_CASE_MEDIA_PREFIX = "cases/0/"
        const val MAX_BACKUP_ENTRIES = 20_000
        const val MAX_ENTRY_BYTES = 10L * 1024L * 1024L * 1024L
        const val MAX_BACKUP_BYTES = 50L * 1024L * 1024L * 1024L
    }
}
