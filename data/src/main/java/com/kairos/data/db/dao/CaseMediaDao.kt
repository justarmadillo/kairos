package com.kairos.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kairos.data.db.entities.CaseMediaEntity
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

    @Query("UPDATE case_media SET is_primary = 0 WHERE case_id = :caseId")
    suspend fun clearPrimary(caseId: Long)

    @Query("UPDATE case_media SET is_primary = 1 WHERE id = :mediaId")
    suspend fun setPrimary(mediaId: Long)
}
