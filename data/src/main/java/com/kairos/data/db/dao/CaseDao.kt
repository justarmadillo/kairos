package com.kairos.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.kairos.data.db.entities.CaseDiagnosisCrossRef
import com.kairos.data.db.entities.CaseEntity
import com.kairos.data.db.entities.ConsultationCaseCrossRef
import com.kairos.data.db.entities.ShiftCaseCrossRef
import com.kairos.data.db.relations.CaseWithRelations
import kotlinx.coroutines.flow.Flow

data class RecentCaseRow(
    val caseId: Long,
    val patientName: String,
    val diagnosisName: String?,
    val caseDate: Long,
)

@Dao
interface CaseDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(case: CaseEntity): Long

    @Update
    suspend fun update(case: CaseEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDiagnosisLinks(refs: List<CaseDiagnosisCrossRef>)

    @Query("DELETE FROM case_diagnoses WHERE case_id = :caseId")
    suspend fun clearDiagnosisLinks(caseId: Long)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun linkToShift(ref: ShiftCaseCrossRef)

    @Query("DELETE FROM shift_cases WHERE shift_id = :shiftId AND case_id = :caseId")
    suspend fun unlinkFromShift(shiftId: Long, caseId: Long)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun linkToSession(ref: ConsultationCaseCrossRef)

    @Query("DELETE FROM consultation_cases WHERE session_id = :sessionId AND case_id = :caseId")
    suspend fun unlinkFromSession(sessionId: Long, caseId: Long)

    @Transaction
    @Query("SELECT * FROM cases WHERE id = :id")
    suspend fun getById(id: Long): CaseWithRelations?

    @Transaction
    @Query("SELECT * FROM cases WHERE id = :id")
    fun observeById(id: Long): Flow<CaseWithRelations?>

    @Transaction
    @Query(
        """
        SELECT c.* FROM cases c
        INNER JOIN case_diagnoses cd ON cd.case_id = c.id
        WHERE cd.diagnosis_id = :diagnosisId AND c.is_deleted = 0
        ORDER BY c.case_date DESC
        """
    )
    fun observeByDiagnosis(diagnosisId: Long): Flow<List<CaseWithRelations>>

    @Transaction
    @Query(
        """
        SELECT c.* FROM cases c
        INNER JOIN shift_cases sc ON sc.case_id = c.id
        WHERE sc.shift_id = :shiftId AND c.is_deleted = 0
        ORDER BY c.case_date DESC
        """
    )
    fun observeByShift(shiftId: Long): Flow<List<CaseWithRelations>>

    @Transaction
    @Query(
        """
        SELECT c.* FROM cases c
        INNER JOIN consultation_cases cc ON cc.case_id = c.id
        WHERE cc.session_id = :sessionId AND c.is_deleted = 0
        ORDER BY c.case_date DESC
        """
    )
    fun observeBySession(sessionId: Long): Flow<List<CaseWithRelations>>

    @Query(
        """
        UPDATE cases
        SET is_deleted = 1, deleted_at = :now, sync_state = 'DELETED', updated_at = :now
        WHERE id = :id
        """
    )
    suspend fun softDelete(id: Long, now: Long)

    @Query(
        """
        UPDATE cases
        SET is_deleted = 0, deleted_at = NULL, sync_state = 'MODIFIED', updated_at = :now
        WHERE id = :id
        """
    )
    suspend fun restore(id: Long, now: Long)

    @Transaction
    @Query("SELECT * FROM cases WHERE is_deleted = 1 ORDER BY deleted_at DESC")
    fun observeTrashed(): Flow<List<CaseWithRelations>>

    @Query("SELECT id FROM cases WHERE is_deleted = 1 AND deleted_at < :threshold")
    suspend fun listExpiredTrash(threshold: Long): List<Long>

    @Query("DELETE FROM cases WHERE id IN (:ids)")
    suspend fun hardDelete(ids: List<Long>)

    @Query("SELECT COUNT(*) FROM cases WHERE is_deleted = 0")
    fun observeTotalCases(): Flow<Int>

    @Query(
        """
        SELECT COUNT(*) FROM cases
        WHERE is_deleted = 0 AND created_at >= :startMs AND created_at < :endMs
        """
    )
    suspend fun countCasesInRange(startMs: Long, endMs: Long): Int

    @Query(
        """
        SELECT c.id AS caseId, p.name AS patientName, c.case_date AS caseDate,
               (SELECT d.name FROM case_diagnoses cd
                INNER JOIN diagnoses d ON d.id = cd.diagnosis_id
                WHERE cd.case_id = c.id LIMIT 1) AS diagnosisName
        FROM cases c
        INNER JOIN patients p ON p.id = c.patient_id
        WHERE c.is_deleted = 0 AND p.is_deleted = 0
        ORDER BY c.created_at DESC
        LIMIT 5
        """
    )
    fun observeRecentCases(): Flow<List<RecentCaseRow>>
}
