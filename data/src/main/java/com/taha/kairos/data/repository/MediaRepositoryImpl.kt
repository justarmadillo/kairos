package com.taha.kairos.data.repository

import androidx.room.withTransaction
import com.taha.kairos.core.media.MediaFileManager
import com.taha.kairos.core.model.MediaItem
import com.taha.kairos.core.repository.DataSafetyCoordinator
import com.taha.kairos.core.repository.MediaRepository
import com.taha.kairos.data.db.KairosDatabase
import com.taha.kairos.data.db.dao.CaseMediaDao
import com.taha.kairos.data.db.entities.CaseMediaEntity
import com.taha.kairos.data.mapper.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaRepositoryImpl @Inject constructor(
    private val dao: CaseMediaDao,
    private val db: KairosDatabase,
    private val mediaFileManager: MediaFileManager,
    private val dataSafetyCoordinator: DataSafetyCoordinator,
) : MediaRepository {

    override suspend fun add(item: MediaItem): Long = dataSafetyCoordinator.withDataLock {
        dao.insert(
            CaseMediaEntity(
                caseId = item.caseId,
                filePath = item.filePath,
                mediaType = item.mediaType.name,
                durationMs = item.durationMs,
                isPrimary = item.isPrimary,
                originalFileName = item.originalFileName,
                createdAt = item.createdAt.takeIf { it != 0L } ?: System.currentTimeMillis(),
            )
        )
    }

    override suspend fun delete(id: Long) = dataSafetyCoordinator.withDataLock {
        val entity = dao.getById(id)
        dao.deleteById(id)
        entity?.let { mediaFileManager.delete(it.filePath) }
        Unit
    }

    override suspend fun setPrimary(caseId: Long, mediaId: Long) = dataSafetyCoordinator.withDataLock {
        db.withTransaction {
            dao.clearPrimary(caseId)
            check(dao.setPrimary(caseId, mediaId) == 1) {
                "The selected attachment does not belong to this case."
            }
        }
    }

    override suspend fun applyCaseEdits(
        caseId: Long,
        additions: List<MediaItem>,
        removedIds: Set<Long>,
        existingPrimaryId: Long?,
    ) = dataSafetyCoordinator.withDataLock {
        require(additions.all { it.caseId == caseId }) {
            "Every new attachment must belong to the edited case."
        }
        val newPrimaryCount = additions.count(MediaItem::isPrimary)
        require(newPrimaryCount <= 1) {
            "Only one new attachment can be primary."
        }
        require(existingPrimaryId == null || newPrimaryCount == 0) {
            "The primary attachment must be either retained or newly added."
        }
        require(existingPrimaryId !in removedIds) {
            "A removed attachment cannot remain primary."
        }

        val removedItems = db.withTransaction {
            val ownedRemovedItems = removedIds.mapNotNull { id ->
                dao.getById(id)?.takeIf { it.caseId == caseId }
            }

            dao.clearPrimary(caseId)
            additions.forEach { item ->
                dao.insert(item.toEntity())
            }
            if (existingPrimaryId != null) {
                check(dao.setPrimary(caseId, existingPrimaryId) == 1) {
                    "The selected attachment does not belong to this case."
                }
            }
            ownedRemovedItems.forEach { item ->
                dao.deleteById(item.id)
            }
            ownedRemovedItems
        }

        // The database is already committed. A missing file is harmless and
        // must not turn a successful edit into a retry that duplicates rows.
        removedItems.forEach { item ->
            runCatching { mediaFileManager.delete(item.filePath) }
        }
    }

    override fun observeForCase(caseId: Long): Flow<List<MediaItem>> =
        dao.observeForCase(caseId).map { list -> list.map { it.toDomain() } }

    private fun MediaItem.toEntity() = CaseMediaEntity(
        caseId = caseId,
        filePath = filePath,
        mediaType = mediaType.name,
        durationMs = durationMs,
        isPrimary = isPrimary,
        originalFileName = originalFileName,
        createdAt = createdAt.takeIf { it != 0L } ?: System.currentTimeMillis(),
    )
}
