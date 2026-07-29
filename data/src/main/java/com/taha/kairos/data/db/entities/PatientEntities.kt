package com.taha.kairos.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "patients")
data class PatientEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val age: Int? = null,
    @ColumnInfo("created_at") val createdAt: Long,
    @ColumnInfo("updated_at") val updatedAt: Long,
    @ColumnInfo("is_deleted") val isDeleted: Boolean = false,
    @ColumnInfo("deleted_at") val deletedAt: Long? = null,
    @ColumnInfo("remote_id") val remoteId: String? = null,
    @ColumnInfo("sync_state") val syncState: String = "LOCAL_ONLY",
    @ColumnInfo("last_synced_at") val lastSyncedAt: Long? = null
)

@Entity(
    tableName = "patient_phones",
    foreignKeys = [
        ForeignKey(
            entity = PatientEntity::class,
            parentColumns = ["id"],
            childColumns = ["patient_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("patient_id")]
)
data class PatientPhoneEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo("patient_id") val patientId: Long,
    val number: String,
    val label: String? = null
)
