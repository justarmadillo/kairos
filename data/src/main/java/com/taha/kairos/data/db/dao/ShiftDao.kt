package com.taha.kairos.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.taha.kairos.data.db.entities.ShiftEntity
import com.taha.kairos.data.db.relations.ShiftWithCount
import kotlinx.coroutines.flow.Flow

@Dao
interface ShiftDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(shift: ShiftEntity): Long

    @Update
    suspend fun update(shift: ShiftEntity)

    @Query("SELECT * FROM shifts WHERE id = :id")
    suspend fun getById(id: Long): ShiftEntity?

    @Query("SELECT * FROM shifts WHERE id = :id")
    fun observeById(id: Long): Flow<ShiftEntity?>

    @Query(
        """
        SELECT s.id AS id, s.label AS label, s.date AS date, s.created_at AS created_at,
               (SELECT COUNT(*) FROM shift_cases sc
                INNER JOIN cases c ON c.id = sc.case_id
                WHERE sc.shift_id = s.id AND c.is_deleted = 0) AS case_count
        FROM shifts s
        WHERE s.is_deleted = 0
        ORDER BY s.date DESC
        """
    )
    fun observeAll(): Flow<List<ShiftWithCount>>

    @Query(
        """
        UPDATE shifts
        SET is_deleted = 1, deleted_at = :now, sync_state = 'DELETED'
        WHERE id = :id
        """
    )
    suspend fun softDelete(id: Long, now: Long)

    @Query(
        """
        UPDATE shifts
        SET is_deleted = 0, deleted_at = NULL, sync_state = 'MODIFIED'
        WHERE id = :id
        """
    )
    suspend fun restore(id: Long)

    @Query("SELECT * FROM shifts WHERE is_deleted = 1 ORDER BY deleted_at DESC")
    fun observeTrashed(): Flow<List<ShiftEntity>>

    @Query("DELETE FROM shifts WHERE is_deleted = 1 AND deleted_at < :threshold")
    suspend fun purgeOlderThan(threshold: Long): Int

    @Query("SELECT COUNT(*) FROM shifts WHERE is_deleted = 0")
    fun observeTotalShifts(): Flow<Int>
}
