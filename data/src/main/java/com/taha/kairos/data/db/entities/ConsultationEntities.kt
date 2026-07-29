package com.taha.kairos.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "consultation_sessions",
    indices = [Index("date"), Index("is_deleted")]
)
data class ConsultationSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: Long,
    @ColumnInfo("created_at") val createdAt: Long,
    @ColumnInfo("is_deleted") val isDeleted: Boolean = false,
    @ColumnInfo("deleted_at") val deletedAt: Long? = null,
    @ColumnInfo("remote_id") val remoteId: String? = null,
    @ColumnInfo("sync_state") val syncState: String = "LOCAL_ONLY",
    @ColumnInfo("last_synced_at") val lastSyncedAt: Long? = null
)

@Entity(
    tableName = "consultation_cases",
    primaryKeys = ["session_id", "case_id"],
    foreignKeys = [
        ForeignKey(
            entity = ConsultationSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["session_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CaseEntity::class,
            parentColumns = ["id"],
            childColumns = ["case_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("case_id")]
)
data class ConsultationCaseCrossRef(
    @ColumnInfo("session_id") val sessionId: Long,
    @ColumnInfo("case_id") val caseId: Long
)
