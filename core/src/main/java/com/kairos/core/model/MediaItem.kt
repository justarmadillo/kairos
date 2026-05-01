package com.kairos.core.model

enum class MediaType { IMAGE, VIDEO, AUDIO }

data class MediaItem(
    val id: Long = 0,
    val caseId: Long,
    val filePath: String,        // relative path under app media root
    val mediaType: MediaType,
    val durationMs: Long? = null,
    val isPrimary: Boolean = false,
    val createdAt: Long = 0
)
