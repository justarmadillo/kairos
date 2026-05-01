package com.kairos.core.model

data class Case(
    val id: Long = 0,
    val patientId: Long,
    val patient: Patient? = null,
    val caseDate: Long,
    val mechanism: String? = null,
    val notesHtml: String? = null,
    val diagnoses: List<Diagnosis> = emptyList(),
    val media: List<MediaItem> = emptyList(),
    val createdAt: Long = 0,
    val updatedAt: Long = 0,
    val deletedAt: Long? = null,
)
