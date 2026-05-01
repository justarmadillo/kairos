package com.kairos.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "diagnoses",
    indices = [Index(value = ["name"], unique = true)]
)
data class DiagnosisEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    @ColumnInfo("created_at") val createdAt: Long,
    @ColumnInfo("remote_id") val remoteId: String? = null,
    @ColumnInfo("sync_state") val syncState: String = "LOCAL_ONLY"
)
