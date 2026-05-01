package com.kairos.core.model

data class ConsultationSession(
    val id: Long = 0,
    val date: Long,
    val caseCount: Int = 0,
    val deletedAt: Long? = null,
)
