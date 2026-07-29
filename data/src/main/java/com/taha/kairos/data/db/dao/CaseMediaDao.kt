package com.taha.kairos.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taha.kairos.data.db.entities.CaseMediaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CaseMediaDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(item: CaseMediaEntity): Long

    @Query("DELETE FROM case_media WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT * FROM case_media WHERE id = :id")
    suspend fun getById(id: Long): CaseMediaEntity?

    @Query("SELECT * FROM case_media WHERE case_id = :caseId ORDER BY created_at ASC")
    fun observeForCase(caseId: Long): Flow<List<CaseMediaEntity>>

    @Query("SELECT * FROM case_media WHERE case_id = :caseId")
    suspend fun listForCase(caseId: Long): List<CaseMediaEntity>

    @Query("SELECT file_path FROM case_media")
    suspend fun listAllFilePaths(): List<String>

    @Query("UPDATE case_media SET is_primary = 0 WHERE case_id = :caseId")
    suspend fun clearPrimary(caseId: Long)

    @Query(
        "UPDATE case_media SET is_primary = 1 " +
            "WHERE id = :mediaId AND case_id = :caseId",
    )
    suspend fun setPrimary(caseId: Long, mediaId: Long): Int
}
