package com.kairos.core.model

enum class MediaType { IMAGE, VIDEO, AUDIO, FILE }

data class MediaItem(
    val id: Long = 0,
    val caseId: Long,
    val filePath: String,        // relative path under app media root
    val mediaType: MediaType,
    val durationMs: Long? = null,
    val isPrimary: Boolean = false,
    val originalFileName: String? = null,
    val createdAt: Long = 0
)
