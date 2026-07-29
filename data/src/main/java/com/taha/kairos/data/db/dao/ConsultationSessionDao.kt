package com.taha.kairos.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taha.kairos.data.db.entities.ConsultationSessionEntity
import com.taha.kairos.data.db.relations.SessionWithCount
import kotlinx.coroutines.flow.Flow

@Dao
interface ConsultationSessionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(session: ConsultationSessionEntity): Long

    @Query("SELECT * FROM consultation_sessions WHERE date = :dateMillis LIMIT 1")
    suspend fun findByDate(dateMillis: Long): ConsultationSessionEntity?

    @Query("SELECT * FROM consultation_sessions WHERE id = :id")
    suspend fun getById(id: Long): ConsultationSessionEntity?

    @Query(
        """
        SELECT s.id AS id, s.date AS date, s.created_at AS created_at,
               (SELECT COUNT(*) FROM consultation_cases cc
                INNER JOIN cases c ON c.id = cc.case_id
                WHERE cc.session_id = s.id AND c.is_deleted = 0) AS case_count
        FROM consultation_sessions s
        WHERE s.is_deleted = 0 AND s.date BETWEEN :start AND :end
        ORDER BY s.date ASC
        """
    )
    fun observeForRange(start: Long, end: Long): Flow<List<SessionWithCount>>

    @Query(
        """
        UPDATE consultation_sessions
        SET is_deleted = 1, deleted_at = :now, sync_state = 'DELETED'
        WHERE id = :id
        """
    )
    suspend fun softDelete(id: Long, now: Long)

    @Query(
        """
        UPDATE consultation_sessions
        SET is_deleted = 0, deleted_at = NULL, sync_state = 'MODIFIED'
        WHERE id = :id
        """
    )
    suspend fun restore(id: Long)

    @Query("SELECT * FROM consultation_sessions WHERE is_deleted = 1 ORDER BY deleted_at DESC")
    fun observeTrashed(): Flow<List<ConsultationSessionEntity>>

    @Query("DELETE FROM consultation_sessions WHERE is_deleted = 1 AND deleted_at < :threshold")
    suspend fun purgeOlderThan(threshold: Long): Int
}
