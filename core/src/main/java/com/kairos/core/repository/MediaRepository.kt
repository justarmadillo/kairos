package com.kairos.core.repository

import com.kairos.core.model.MediaItem
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    /** Insert media item; expects file already saved at [item.filePath] under media root. */
    suspend fun add(item: MediaItem): Long

    suspend fun delete(id: Long)

    suspend fun setPrimary(caseId: Long, mediaId: Long)

    fun observeForCase(caseId: Long): Flow<List<MediaItem>>
}
