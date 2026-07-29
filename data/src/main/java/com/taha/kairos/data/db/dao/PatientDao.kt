package com.taha.kairos.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.taha.kairos.data.db.entities.PatientEntity
import com.taha.kairos.data.db.entities.PatientPhoneEntity
import com.taha.kairos.data.db.relations.PatientWithPhones
import kotlinx.coroutines.flow.Flow

@Dao
interface PatientDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(patient: PatientEntity): Long

    @Update
    suspend fun update(patient: PatientEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhones(phones: List<PatientPhoneEntity>)

    @Query("DELETE FROM patient_phones WHERE patient_id = :patientId")
    suspend fun deletePhonesFor(patientId: Long)

    @Transaction
    @Query("SELECT * FROM patients WHERE id = :id")
    suspend fun getById(id: Long): PatientWithPhones?

    @Transaction
    @Query("SELECT * FROM patients WHERE id = :id")
    fun observeById(id: Long): Flow<PatientWithPhones?>

    @Transaction
    @Query(
        """
        SELECT * FROM patients
        WHERE is_deleted = 0
          AND name LIKE '%' || :query || '%' COLLATE NOCASE
        ORDER BY name COLLATE NOCASE ASC
        LIMIT 50
        """
    )
    fun search(query: String): Flow<List<PatientWithPhones>>

    @Query(
        """
        UPDATE patients
        SET is_deleted = 1, deleted_at = :now, sync_state = 'DELETED', updated_at = :now
        WHERE id = :id
        """
    )
    suspend fun softDelete(id: Long, now: Long)

    @Query(
        """
        UPDATE patients
        SET is_deleted = 0, deleted_at = NULL, sync_state = 'MODIFIED', updated_at = :now
        WHERE id = :id
        """
    )
    suspend fun restore(id: Long, now: Long)

    @Transaction
    @Query("SELECT * FROM patients WHERE is_deleted = 1 ORDER BY deleted_at DESC")
    fun observeTrashed(): Flow<List<PatientWithPhones>>

    @Query("""
        DELETE FROM patients
        WHERE is_deleted = 1 AND deleted_at < :threshold
          AND id NOT IN (SELECT DISTINCT patient_id FROM cases WHERE is_deleted = 0)
    """)
    suspend fun purgeOlderThan(threshold: Long): Int

    @Query("SELECT COUNT(*) FROM patients WHERE is_deleted = 0")
    fun observeTotalPatients(): Flow<Int>
}
