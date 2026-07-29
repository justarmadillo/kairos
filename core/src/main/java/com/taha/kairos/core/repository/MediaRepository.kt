package com.taha.kairos.core.repository

import com.taha.kairos.core.model.MediaItem
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    /** Insert media item; expects file already saved at [item.filePath] under media root. */
    suspend fun add(item: MediaItem): Long

    suspend fun delete(id: Long)

    suspend fun setPrimary(caseId: Long, mediaId: Long)

    /**
     * Atomically applies the database portion of an attachment edit.
     *
     * [additions] must already point to files under the media root. Exactly one
     * new item may be primary, or [existingPrimaryId] may identify a retained
     * item. Removed files are deleted only after the database transaction
     * commits.
     */
    suspend fun applyCaseEdits(
        caseId: Long,
        additions: List<MediaItem>,
        removedIds: Set<Long>,
        existingPrimaryId: Long?,
    )

    fun observeForCase(caseId: Long): Flow<List<MediaItem>>
}
