package com.kairos.data.db.relations

import androidx.room.ColumnInfo

data class ShiftWithCount(
    val id: Long,
    val label: String?,
    val date: Long,
    @ColumnInfo("created_at") val createdAt: Long,
    @ColumnInfo("case_count") val caseCount: Int
)

data class SessionWithCount(
    val id: Long,
    val date: Long,
    @ColumnInfo("created_at") val createdAt: Long,
    @ColumnInfo("case_count") val caseCount: Int
)

data class DiagnosisWithCount(
    val id: Long,
    val name: String,
    @ColumnInfo("case_count") val caseCount: Int
)
