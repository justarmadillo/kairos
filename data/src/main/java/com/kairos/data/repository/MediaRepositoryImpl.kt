package com.kairos.data.repository

import androidx.room.withTransaction
import com.kairos.core.media.MediaFileManager
import com.kairos.core.model.MediaItem
import com.kairos.core.repository.MediaRepository
import com.kairos.data.db.KairosDatabase
import com.kairos.data.db.dao.CaseMediaDao
import com.kairos.data.db.entities.CaseMediaEntity
import com.kairos.data.mapper.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepositoryImpl @Inject constructor(
    private val dao: CaseMediaDao,
    private val db: KairosDatabase,
    private val mediaFileManager: MediaFileManager,
) : MediaRepository {

    override suspend fun add(item: MediaItem): Long =
        dao.insert(
            CaseMediaEntity(
                caseId = item.caseId,
                filePath = item.filePath,
                mediaType = item.mediaType.name,
                durationMs = item.durationMs,
                isPrimary = item.isPrimary,
                createdAt = item.createdAt.takeIf { it != 0L } ?: System.currentTimeMillis(),
            )
        )

    override suspend fun delete(id: Long) {
        val entity = dao.getById(id)
        dao.deleteById(id)
        entity?.let { mediaFileManager.delete(it.filePath) }
    }

    override suspend fun setPrimary(caseId: Long, mediaId: Long) {
        db.withTransaction {
            dao.clearPrimary(caseId)
            dao.setPrimary(mediaId)
        }
    }

    override fun observeForCase(caseId: Long): Flow<List<MediaItem>> =
        dao.observeForCase(caseId).map { list -> list.map { it.toDomain() } }
}
