package com.kairos.core.repository

data class BackupResult(
    val success: Boolean,
    val timestampMs: Long,
    val error: String? = null,
)

data class RestoreResult(
    val success: Boolean,
    val error: String? = null,
)

interface BackupRepository {
    suspend fun export(folderUri: String): BackupResult
    suspend fun restore(zipUri: String): RestoreResult
}
