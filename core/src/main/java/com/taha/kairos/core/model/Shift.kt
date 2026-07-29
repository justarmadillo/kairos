package com.taha.kairos.core.model

data class Shift(
    val id: Long = 0,
    val label: String? = null,
    val date: Long,
    val caseCount: Int = 0,
    val createdAt: Long = 0,
    val deletedAt: Long? = null,
)
