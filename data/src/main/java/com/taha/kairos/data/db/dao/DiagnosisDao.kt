package com.taha.kairos.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.taha.kairos.data.db.entities.DiagnosisEntity
import com.taha.kairos.data.db.relations.DiagnosisWithCount
import kotlinx.coroutines.flow.Flow

@Dao
interface DiagnosisDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(diagnosis: DiagnosisEntity): Long

    @Query("SELECT * FROM diagnoses WHERE name = :name COLLATE NOCASE LIMIT 1")
    suspend fun findByNameCi(name: String): DiagnosisEntity?

    @Query("SELECT * FROM diagnoses WHERE id = :id")
    suspend fun getById(id: Long): DiagnosisEntity?

    @Query("UPDATE diagnoses SET name = :name, sync_state = 'LOCAL_ONLY' WHERE id = :id")
    suspend fun rename(id: Long, name: String): Int

    @Query(
        """
        SELECT d.id AS id, d.name AS name,
               (SELECT COUNT(*) FROM case_diagnoses cd
                INNER JOIN cases c ON c.id = cd.case_id
                WHERE cd.diagnosis_id = d.id AND c.is_deleted = 0) AS case_count
        FROM diagnoses d
        ORDER BY d.name COLLATE NOCASE ASC
        """
    )
    fun observeAllAlphabetical(): Flow<List<DiagnosisWithCount>>

    @Query(
        """
        SELECT d.id AS id, d.name AS name,
               (SELECT COUNT(*) FROM case_diagnoses cd
                INNER JOIN cases c ON c.id = cd.case_id
                WHERE cd.diagnosis_id = d.id AND c.is_deleted = 0) AS case_count
        FROM diagnoses d
        ORDER BY case_count DESC, d.name COLLATE NOCASE ASC
        """
    )
    fun observeAllByMostUsed(): Flow<List<DiagnosisWithCount>>

    @Query(
        """
        SELECT d.id AS id, d.name AS name,
               (SELECT COUNT(*) FROM case_diagnoses cd
                INNER JOIN cases c ON c.id = cd.case_id
                WHERE cd.diagnosis_id = d.id AND c.is_deleted = 0) AS case_count
        FROM diagnoses d
        ORDER BY d.created_at DESC
        """
    )
    fun observeAllByRecent(): Flow<List<DiagnosisWithCount>>

    @Query(
        """
        SELECT * FROM diagnoses
        WHERE name LIKE :prefix || '%' COLLATE NOCASE
        ORDER BY name COLLATE NOCASE ASC
        LIMIT :limit
        """
    )
    suspend fun searchByPrefix(prefix: String, limit: Int): List<DiagnosisEntity>

    /** Delete diagnoses that no case references (orphaned after case hard-delete). */
    @Query("DELETE FROM diagnoses WHERE id NOT IN (SELECT DISTINCT diagnosis_id FROM case_diagnoses)")
    suspend fun deleteOrphaned(): Int
}
