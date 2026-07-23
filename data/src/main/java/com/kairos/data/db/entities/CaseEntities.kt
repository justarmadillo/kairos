package com.kairos.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cases",
    foreignKeys = [
        ForeignKey(
            entity = PatientEntity::class,
            parentColumns = ["id"],
            childColumns = ["patient_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("patient_id"), Index("case_date"), Index("is_deleted")]
)
data class CaseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo("patient_id") val patientId: Long,
    @ColumnInfo("case_date") val caseDate: Long,
    val mechanism: String? = null,
    @ColumnInfo("notes_html") val notesHtml: String? = null,
    @ColumnInfo("created_at") val createdAt: Long,
    @ColumnInfo("updated_at") val updatedAt: Long,
    @ColumnInfo("is_deleted") val isDeleted: Boolean = false,
    @ColumnInfo("deleted_at") val deletedAt: Long? = null,
    @ColumnInfo("remote_id") val remoteId: String? = null,
    @ColumnInfo("sync_state") val syncState: String = "LOCAL_ONLY",
    @ColumnInfo("last_synced_at") val lastSyncedAt: Long? = null
)

@Entity(
    tableName = "case_diagnoses",
    primaryKeys = ["case_id", "diagnosis_id"],
    foreignKeys = [
        ForeignKey(
            entity = CaseEntity::class,
            parentColumns = ["id"],
            childColumns = ["case_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = DiagnosisEntity::class,
            parentColumns = ["id"],
            childColumns = ["diagnosis_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("diagnosis_id")]
)
data class CaseDiagnosisCrossRef(
    @ColumnInfo("case_id") val caseId: Long,
    @ColumnInfo("diagnosis_id") val diagnosisId: Long
)

@Entity(
    tableName = "case_media",
    foreignKeys = [
        ForeignKey(
            entity = CaseEntity::class,
            parentColumns = ["id"],
            childColumns = ["case_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("case_id")]
)
data class CaseMediaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo("case_id") val caseId: Long,
    @ColumnInfo("file_path") val filePath: String,
    @ColumnInfo("media_type") val mediaType: String,           // IMAGE | VIDEO | AUDIO | FILE
    @ColumnInfo("duration_ms") val durationMs: Long? = null,
    @ColumnInfo("is_primary") val isPrimary: Boolean = false,
    @ColumnInfo("original_file_name") val originalFileName: String? = null,
    @ColumnInfo("created_at") val createdAt: Long
)
