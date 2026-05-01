package com.kairos.`data`.db.dao

import androidx.collection.LongSparseArray
import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndex
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.room.util.recursiveFetchLongSparseArray
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteStatement
import com.kairos.`data`.db.entities.CaseDiagnosisCrossRef
import com.kairos.`data`.db.entities.CaseEntity
import com.kairos.`data`.db.entities.CaseMediaEntity
import com.kairos.`data`.db.entities.ConsultationCaseCrossRef
import com.kairos.`data`.db.entities.DiagnosisEntity
import com.kairos.`data`.db.entities.PatientEntity
import com.kairos.`data`.db.entities.PatientPhoneEntity
import com.kairos.`data`.db.entities.ShiftCaseCrossRef
import com.kairos.`data`.db.relations.CaseWithRelations
import com.kairos.`data`.db.relations.PatientWithPhones
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlin.text.StringBuilder
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class CaseDao_Impl(
  __db: RoomDatabase,
) : CaseDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfCaseEntity: EntityInsertAdapter<CaseEntity>

  private val __insertAdapterOfCaseDiagnosisCrossRef: EntityInsertAdapter<CaseDiagnosisCrossRef>

  private val __insertAdapterOfShiftCaseCrossRef: EntityInsertAdapter<ShiftCaseCrossRef>

  private val __insertAdapterOfConsultationCaseCrossRef:
      EntityInsertAdapter<ConsultationCaseCrossRef>

  private val __updateAdapterOfCaseEntity: EntityDeleteOrUpdateAdapter<CaseEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfCaseEntity = object : EntityInsertAdapter<CaseEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `cases` (`id`,`patient_id`,`case_date`,`mechanism`,`notes_html`,`created_at`,`updated_at`,`is_deleted`,`deleted_at`,`remote_id`,`sync_state`,`last_synced_at`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: CaseEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.patientId)
        statement.bindLong(3, entity.caseDate)
        val _tmpMechanism: String? = entity.mechanism
        if (_tmpMechanism == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpMechanism)
        }
        val _tmpNotesHtml: String? = entity.notesHtml
        if (_tmpNotesHtml == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpNotesHtml)
        }
        statement.bindLong(6, entity.createdAt)
        statement.bindLong(7, entity.updatedAt)
        val _tmp: Int = if (entity.isDeleted) 1 else 0
        statement.bindLong(8, _tmp.toLong())
        val _tmpDeletedAt: Long? = entity.deletedAt
        if (_tmpDeletedAt == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpDeletedAt)
        }
        val _tmpRemoteId: String? = entity.remoteId
        if (_tmpRemoteId == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpRemoteId)
        }
        statement.bindText(11, entity.syncState)
        val _tmpLastSyncedAt: Long? = entity.lastSyncedAt
        if (_tmpLastSyncedAt == null) {
          statement.bindNull(12)
        } else {
          statement.bindLong(12, _tmpLastSyncedAt)
        }
      }
    }
    this.__insertAdapterOfCaseDiagnosisCrossRef = object :
        EntityInsertAdapter<CaseDiagnosisCrossRef>() {
      protected override fun createQuery(): String =
          "INSERT OR IGNORE INTO `case_diagnoses` (`case_id`,`diagnosis_id`) VALUES (?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: CaseDiagnosisCrossRef) {
        statement.bindLong(1, entity.caseId)
        statement.bindLong(2, entity.diagnosisId)
      }
    }
    this.__insertAdapterOfShiftCaseCrossRef = object : EntityInsertAdapter<ShiftCaseCrossRef>() {
      protected override fun createQuery(): String =
          "INSERT OR IGNORE INTO `shift_cases` (`shift_id`,`case_id`) VALUES (?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ShiftCaseCrossRef) {
        statement.bindLong(1, entity.shiftId)
        statement.bindLong(2, entity.caseId)
      }
    }
    this.__insertAdapterOfConsultationCaseCrossRef = object :
        EntityInsertAdapter<ConsultationCaseCrossRef>() {
      protected override fun createQuery(): String =
          "INSERT OR IGNORE INTO `consultation_cases` (`session_id`,`case_id`) VALUES (?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ConsultationCaseCrossRef) {
        statement.bindLong(1, entity.sessionId)
        statement.bindLong(2, entity.caseId)
      }
    }
    this.__updateAdapterOfCaseEntity = object : EntityDeleteOrUpdateAdapter<CaseEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `cases` SET `id` = ?,`patient_id` = ?,`case_date` = ?,`mechanism` = ?,`notes_html` = ?,`created_at` = ?,`updated_at` = ?,`is_deleted` = ?,`deleted_at` = ?,`remote_id` = ?,`sync_state` = ?,`last_synced_at` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: CaseEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.patientId)
        statement.bindLong(3, entity.caseDate)
        val _tmpMechanism: String? = entity.mechanism
        if (_tmpMechanism == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpMechanism)
        }
        val _tmpNotesHtml: String? = entity.notesHtml
        if (_tmpNotesHtml == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpNotesHtml)
        }
        statement.bindLong(6, entity.createdAt)
        statement.bindLong(7, entity.updatedAt)
        val _tmp: Int = if (entity.isDeleted) 1 else 0
        statement.bindLong(8, _tmp.toLong())
        val _tmpDeletedAt: Long? = entity.deletedAt
        if (_tmpDeletedAt == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpDeletedAt)
        }
        val _tmpRemoteId: String? = entity.remoteId
        if (_tmpRemoteId == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpRemoteId)
        }
        statement.bindText(11, entity.syncState)
        val _tmpLastSyncedAt: Long? = entity.lastSyncedAt
        if (_tmpLastSyncedAt == null) {
          statement.bindNull(12)
        } else {
          statement.bindLong(12, _tmpLastSyncedAt)
        }
        statement.bindLong(13, entity.id)
      }
    }
  }

  public override suspend fun insert(case: CaseEntity): Long = performSuspending(__db, false, true)
      { _connection ->
    val _result: Long = __insertAdapterOfCaseEntity.insertAndReturnId(_connection, case)
    _result
  }

  public override suspend fun insertDiagnosisLinks(refs: List<CaseDiagnosisCrossRef>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfCaseDiagnosisCrossRef.insert(_connection, refs)
  }

  public override suspend fun linkToShift(ref: ShiftCaseCrossRef): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfShiftCaseCrossRef.insert(_connection, ref)
  }

  public override suspend fun linkToSession(ref: ConsultationCaseCrossRef): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfConsultationCaseCrossRef.insert(_connection, ref)
  }

  public override suspend fun update(case: CaseEntity): Unit = performSuspending(__db, false, true)
      { _connection ->
    __updateAdapterOfCaseEntity.handle(_connection, case)
  }

  public override suspend fun getById(id: Long): CaseWithRelations? {
    val _sql: String = "SELECT * FROM cases WHERE id = ?"
    return performSuspending(__db, true, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfPatientId: Int = getColumnIndexOrThrow(_stmt, "patient_id")
        val _columnIndexOfCaseDate: Int = getColumnIndexOrThrow(_stmt, "case_date")
        val _columnIndexOfMechanism: Int = getColumnIndexOrThrow(_stmt, "mechanism")
        val _columnIndexOfNotesHtml: Int = getColumnIndexOrThrow(_stmt, "notes_html")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updated_at")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "is_deleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deleted_at")
        val _columnIndexOfRemoteId: Int = getColumnIndexOrThrow(_stmt, "remote_id")
        val _columnIndexOfSyncState: Int = getColumnIndexOrThrow(_stmt, "sync_state")
        val _columnIndexOfLastSyncedAt: Int = getColumnIndexOrThrow(_stmt, "last_synced_at")
        val _collectionPatient: LongSparseArray<PatientWithPhones?> =
            LongSparseArray<PatientWithPhones?>()
        val _collectionDiagnoses: LongSparseArray<MutableList<DiagnosisEntity>> =
            LongSparseArray<MutableList<DiagnosisEntity>>()
        val _collectionMedia: LongSparseArray<MutableList<CaseMediaEntity>> =
            LongSparseArray<MutableList<CaseMediaEntity>>()
        while (_stmt.step()) {
          val _tmpKey: Long
          _tmpKey = _stmt.getLong(_columnIndexOfPatientId)
          _collectionPatient.put(_tmpKey, null)
          val _tmpKey_1: Long
          _tmpKey_1 = _stmt.getLong(_columnIndexOfId)
          if (!_collectionDiagnoses.containsKey(_tmpKey_1)) {
            _collectionDiagnoses.put(_tmpKey_1, mutableListOf())
          }
          val _tmpKey_2: Long
          _tmpKey_2 = _stmt.getLong(_columnIndexOfId)
          if (!_collectionMedia.containsKey(_tmpKey_2)) {
            _collectionMedia.put(_tmpKey_2, mutableListOf())
          }
        }
        _stmt.reset()
        __fetchRelationshippatientsAscomKairosDataDbRelationsPatientWithPhones(_connection,
            _collectionPatient)
        __fetchRelationshipdiagnosesAscomKairosDataDbEntitiesDiagnosisEntity(_connection,
            _collectionDiagnoses)
        __fetchRelationshipcaseMediaAscomKairosDataDbEntitiesCaseMediaEntity(_connection,
            _collectionMedia)
        val _result: CaseWithRelations?
        if (_stmt.step()) {
          val _tmpCase: CaseEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpPatientId: Long
          _tmpPatientId = _stmt.getLong(_columnIndexOfPatientId)
          val _tmpCaseDate: Long
          _tmpCaseDate = _stmt.getLong(_columnIndexOfCaseDate)
          val _tmpMechanism: String?
          if (_stmt.isNull(_columnIndexOfMechanism)) {
            _tmpMechanism = null
          } else {
            _tmpMechanism = _stmt.getText(_columnIndexOfMechanism)
          }
          val _tmpNotesHtml: String?
          if (_stmt.isNull(_columnIndexOfNotesHtml)) {
            _tmpNotesHtml = null
          } else {
            _tmpNotesHtml = _stmt.getText(_columnIndexOfNotesHtml)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpRemoteId: String?
          if (_stmt.isNull(_columnIndexOfRemoteId)) {
            _tmpRemoteId = null
          } else {
            _tmpRemoteId = _stmt.getText(_columnIndexOfRemoteId)
          }
          val _tmpSyncState: String
          _tmpSyncState = _stmt.getText(_columnIndexOfSyncState)
          val _tmpLastSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfLastSyncedAt)) {
            _tmpLastSyncedAt = null
          } else {
            _tmpLastSyncedAt = _stmt.getLong(_columnIndexOfLastSyncedAt)
          }
          _tmpCase =
              CaseEntity(_tmpId,_tmpPatientId,_tmpCaseDate,_tmpMechanism,_tmpNotesHtml,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpRemoteId,_tmpSyncState,_tmpLastSyncedAt)
          val _tmpPatient: PatientWithPhones?
          val _tmpKey_3: Long
          _tmpKey_3 = _stmt.getLong(_columnIndexOfPatientId)
          _tmpPatient = _collectionPatient.get(_tmpKey_3)
          val _tmpDiagnosesCollection: MutableList<DiagnosisEntity>
          val _tmpKey_4: Long
          _tmpKey_4 = _stmt.getLong(_columnIndexOfId)
          _tmpDiagnosesCollection = checkNotNull(_collectionDiagnoses.get(_tmpKey_4))
          val _tmpMediaCollection: MutableList<CaseMediaEntity>
          val _tmpKey_5: Long
          _tmpKey_5 = _stmt.getLong(_columnIndexOfId)
          _tmpMediaCollection = checkNotNull(_collectionMedia.get(_tmpKey_5))
          _result =
              CaseWithRelations(_tmpCase,_tmpPatient,_tmpDiagnosesCollection,_tmpMediaCollection)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeById(id: Long): Flow<CaseWithRelations?> {
    val _sql: String = "SELECT * FROM cases WHERE id = ?"
    return createFlow(__db, true, arrayOf("patient_phones", "patients", "case_diagnoses",
        "diagnoses", "case_media", "cases")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfPatientId: Int = getColumnIndexOrThrow(_stmt, "patient_id")
        val _columnIndexOfCaseDate: Int = getColumnIndexOrThrow(_stmt, "case_date")
        val _columnIndexOfMechanism: Int = getColumnIndexOrThrow(_stmt, "mechanism")
        val _columnIndexOfNotesHtml: Int = getColumnIndexOrThrow(_stmt, "notes_html")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updated_at")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "is_deleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deleted_at")
        val _columnIndexOfRemoteId: Int = getColumnIndexOrThrow(_stmt, "remote_id")
        val _columnIndexOfSyncState: Int = getColumnIndexOrThrow(_stmt, "sync_state")
        val _columnIndexOfLastSyncedAt: Int = getColumnIndexOrThrow(_stmt, "last_synced_at")
        val _collectionPatient: LongSparseArray<PatientWithPhones?> =
            LongSparseArray<PatientWithPhones?>()
        val _collectionDiagnoses: LongSparseArray<MutableList<DiagnosisEntity>> =
            LongSparseArray<MutableList<DiagnosisEntity>>()
        val _collectionMedia: LongSparseArray<MutableList<CaseMediaEntity>> =
            LongSparseArray<MutableList<CaseMediaEntity>>()
        while (_stmt.step()) {
          val _tmpKey: Long
          _tmpKey = _stmt.getLong(_columnIndexOfPatientId)
          _collectionPatient.put(_tmpKey, null)
          val _tmpKey_1: Long
          _tmpKey_1 = _stmt.getLong(_columnIndexOfId)
          if (!_collectionDiagnoses.containsKey(_tmpKey_1)) {
            _collectionDiagnoses.put(_tmpKey_1, mutableListOf())
          }
          val _tmpKey_2: Long
          _tmpKey_2 = _stmt.getLong(_columnIndexOfId)
          if (!_collectionMedia.containsKey(_tmpKey_2)) {
            _collectionMedia.put(_tmpKey_2, mutableListOf())
          }
        }
        _stmt.reset()
        __fetchRelationshippatientsAscomKairosDataDbRelationsPatientWithPhones(_connection,
            _collectionPatient)
        __fetchRelationshipdiagnosesAscomKairosDataDbEntitiesDiagnosisEntity(_connection,
            _collectionDiagnoses)
        __fetchRelationshipcaseMediaAscomKairosDataDbEntitiesCaseMediaEntity(_connection,
            _collectionMedia)
        val _result: CaseWithRelations?
        if (_stmt.step()) {
          val _tmpCase: CaseEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpPatientId: Long
          _tmpPatientId = _stmt.getLong(_columnIndexOfPatientId)
          val _tmpCaseDate: Long
          _tmpCaseDate = _stmt.getLong(_columnIndexOfCaseDate)
          val _tmpMechanism: String?
          if (_stmt.isNull(_columnIndexOfMechanism)) {
            _tmpMechanism = null
          } else {
            _tmpMechanism = _stmt.getText(_columnIndexOfMechanism)
          }
          val _tmpNotesHtml: String?
          if (_stmt.isNull(_columnIndexOfNotesHtml)) {
            _tmpNotesHtml = null
          } else {
            _tmpNotesHtml = _stmt.getText(_columnIndexOfNotesHtml)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpRemoteId: String?
          if (_stmt.isNull(_columnIndexOfRemoteId)) {
            _tmpRemoteId = null
          } else {
            _tmpRemoteId = _stmt.getText(_columnIndexOfRemoteId)
          }
          val _tmpSyncState: String
          _tmpSyncState = _stmt.getText(_columnIndexOfSyncState)
          val _tmpLastSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfLastSyncedAt)) {
            _tmpLastSyncedAt = null
          } else {
            _tmpLastSyncedAt = _stmt.getLong(_columnIndexOfLastSyncedAt)
          }
          _tmpCase =
              CaseEntity(_tmpId,_tmpPatientId,_tmpCaseDate,_tmpMechanism,_tmpNotesHtml,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpRemoteId,_tmpSyncState,_tmpLastSyncedAt)
          val _tmpPatient: PatientWithPhones?
          val _tmpKey_3: Long
          _tmpKey_3 = _stmt.getLong(_columnIndexOfPatientId)
          _tmpPatient = _collectionPatient.get(_tmpKey_3)
          val _tmpDiagnosesCollection: MutableList<DiagnosisEntity>
          val _tmpKey_4: Long
          _tmpKey_4 = _stmt.getLong(_columnIndexOfId)
          _tmpDiagnosesCollection = checkNotNull(_collectionDiagnoses.get(_tmpKey_4))
          val _tmpMediaCollection: MutableList<CaseMediaEntity>
          val _tmpKey_5: Long
          _tmpKey_5 = _stmt.getLong(_columnIndexOfId)
          _tmpMediaCollection = checkNotNull(_collectionMedia.get(_tmpKey_5))
          _result =
              CaseWithRelations(_tmpCase,_tmpPatient,_tmpDiagnosesCollection,_tmpMediaCollection)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByDiagnosis(diagnosisId: Long): Flow<List<CaseWithRelations>> {
    val _sql: String = """
        |
        |        SELECT c.* FROM cases c
        |        INNER JOIN case_diagnoses cd ON cd.case_id = c.id
        |        WHERE cd.diagnosis_id = ? AND c.is_deleted = 0
        |        ORDER BY c.case_date DESC
        |        
        """.trimMargin()
    return createFlow(__db, true, arrayOf("patient_phones", "patients", "case_diagnoses",
        "diagnoses", "case_media", "cases")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, diagnosisId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfPatientId: Int = getColumnIndexOrThrow(_stmt, "patient_id")
        val _columnIndexOfCaseDate: Int = getColumnIndexOrThrow(_stmt, "case_date")
        val _columnIndexOfMechanism: Int = getColumnIndexOrThrow(_stmt, "mechanism")
        val _columnIndexOfNotesHtml: Int = getColumnIndexOrThrow(_stmt, "notes_html")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updated_at")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "is_deleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deleted_at")
        val _columnIndexOfRemoteId: Int = getColumnIndexOrThrow(_stmt, "remote_id")
        val _columnIndexOfSyncState: Int = getColumnIndexOrThrow(_stmt, "sync_state")
        val _columnIndexOfLastSyncedAt: Int = getColumnIndexOrThrow(_stmt, "last_synced_at")
        val _collectionPatient: LongSparseArray<PatientWithPhones?> =
            LongSparseArray<PatientWithPhones?>()
        val _collectionDiagnoses: LongSparseArray<MutableList<DiagnosisEntity>> =
            LongSparseArray<MutableList<DiagnosisEntity>>()
        val _collectionMedia: LongSparseArray<MutableList<CaseMediaEntity>> =
            LongSparseArray<MutableList<CaseMediaEntity>>()
        while (_stmt.step()) {
          val _tmpKey: Long
          _tmpKey = _stmt.getLong(_columnIndexOfPatientId)
          _collectionPatient.put(_tmpKey, null)
          val _tmpKey_1: Long
          _tmpKey_1 = _stmt.getLong(_columnIndexOfId)
          if (!_collectionDiagnoses.containsKey(_tmpKey_1)) {
            _collectionDiagnoses.put(_tmpKey_1, mutableListOf())
          }
          val _tmpKey_2: Long
          _tmpKey_2 = _stmt.getLong(_columnIndexOfId)
          if (!_collectionMedia.containsKey(_tmpKey_2)) {
            _collectionMedia.put(_tmpKey_2, mutableListOf())
          }
        }
        _stmt.reset()
        __fetchRelationshippatientsAscomKairosDataDbRelationsPatientWithPhones(_connection,
            _collectionPatient)
        __fetchRelationshipdiagnosesAscomKairosDataDbEntitiesDiagnosisEntity(_connection,
            _collectionDiagnoses)
        __fetchRelationshipcaseMediaAscomKairosDataDbEntitiesCaseMediaEntity(_connection,
            _collectionMedia)
        val _result: MutableList<CaseWithRelations> = mutableListOf()
        while (_stmt.step()) {
          val _item: CaseWithRelations
          val _tmpCase: CaseEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpPatientId: Long
          _tmpPatientId = _stmt.getLong(_columnIndexOfPatientId)
          val _tmpCaseDate: Long
          _tmpCaseDate = _stmt.getLong(_columnIndexOfCaseDate)
          val _tmpMechanism: String?
          if (_stmt.isNull(_columnIndexOfMechanism)) {
            _tmpMechanism = null
          } else {
            _tmpMechanism = _stmt.getText(_columnIndexOfMechanism)
          }
          val _tmpNotesHtml: String?
          if (_stmt.isNull(_columnIndexOfNotesHtml)) {
            _tmpNotesHtml = null
          } else {
            _tmpNotesHtml = _stmt.getText(_columnIndexOfNotesHtml)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpRemoteId: String?
          if (_stmt.isNull(_columnIndexOfRemoteId)) {
            _tmpRemoteId = null
          } else {
            _tmpRemoteId = _stmt.getText(_columnIndexOfRemoteId)
          }
          val _tmpSyncState: String
          _tmpSyncState = _stmt.getText(_columnIndexOfSyncState)
          val _tmpLastSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfLastSyncedAt)) {
            _tmpLastSyncedAt = null
          } else {
            _tmpLastSyncedAt = _stmt.getLong(_columnIndexOfLastSyncedAt)
          }
          _tmpCase =
              CaseEntity(_tmpId,_tmpPatientId,_tmpCaseDate,_tmpMechanism,_tmpNotesHtml,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpRemoteId,_tmpSyncState,_tmpLastSyncedAt)
          val _tmpPatient: PatientWithPhones?
          val _tmpKey_3: Long
          _tmpKey_3 = _stmt.getLong(_columnIndexOfPatientId)
          _tmpPatient = _collectionPatient.get(_tmpKey_3)
          val _tmpDiagnosesCollection: MutableList<DiagnosisEntity>
          val _tmpKey_4: Long
          _tmpKey_4 = _stmt.getLong(_columnIndexOfId)
          _tmpDiagnosesCollection = checkNotNull(_collectionDiagnoses.get(_tmpKey_4))
          val _tmpMediaCollection: MutableList<CaseMediaEntity>
          val _tmpKey_5: Long
          _tmpKey_5 = _stmt.getLong(_columnIndexOfId)
          _tmpMediaCollection = checkNotNull(_collectionMedia.get(_tmpKey_5))
          _item =
              CaseWithRelations(_tmpCase,_tmpPatient,_tmpDiagnosesCollection,_tmpMediaCollection)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByShift(shiftId: Long): Flow<List<CaseWithRelations>> {
    val _sql: String = """
        |
        |        SELECT c.* FROM cases c
        |        INNER JOIN shift_cases sc ON sc.case_id = c.id
        |        WHERE sc.shift_id = ? AND c.is_deleted = 0
        |        ORDER BY c.case_date DESC
        |        
        """.trimMargin()
    return createFlow(__db, true, arrayOf("patient_phones", "patients", "case_diagnoses",
        "diagnoses", "case_media", "cases", "shift_cases")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, shiftId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfPatientId: Int = getColumnIndexOrThrow(_stmt, "patient_id")
        val _columnIndexOfCaseDate: Int = getColumnIndexOrThrow(_stmt, "case_date")
        val _columnIndexOfMechanism: Int = getColumnIndexOrThrow(_stmt, "mechanism")
        val _columnIndexOfNotesHtml: Int = getColumnIndexOrThrow(_stmt, "notes_html")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updated_at")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "is_deleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deleted_at")
        val _columnIndexOfRemoteId: Int = getColumnIndexOrThrow(_stmt, "remote_id")
        val _columnIndexOfSyncState: Int = getColumnIndexOrThrow(_stmt, "sync_state")
        val _columnIndexOfLastSyncedAt: Int = getColumnIndexOrThrow(_stmt, "last_synced_at")
        val _collectionPatient: LongSparseArray<PatientWithPhones?> =
            LongSparseArray<PatientWithPhones?>()
        val _collectionDiagnoses: LongSparseArray<MutableList<DiagnosisEntity>> =
            LongSparseArray<MutableList<DiagnosisEntity>>()
        val _collectionMedia: LongSparseArray<MutableList<CaseMediaEntity>> =
            LongSparseArray<MutableList<CaseMediaEntity>>()
        while (_stmt.step()) {
          val _tmpKey: Long
          _tmpKey = _stmt.getLong(_columnIndexOfPatientId)
          _collectionPatient.put(_tmpKey, null)
          val _tmpKey_1: Long
          _tmpKey_1 = _stmt.getLong(_columnIndexOfId)
          if (!_collectionDiagnoses.containsKey(_tmpKey_1)) {
            _collectionDiagnoses.put(_tmpKey_1, mutableListOf())
          }
          val _tmpKey_2: Long
          _tmpKey_2 = _stmt.getLong(_columnIndexOfId)
          if (!_collectionMedia.containsKey(_tmpKey_2)) {
            _collectionMedia.put(_tmpKey_2, mutableListOf())
          }
        }
        _stmt.reset()
        __fetchRelationshippatientsAscomKairosDataDbRelationsPatientWithPhones(_connection,
            _collectionPatient)
        __fetchRelationshipdiagnosesAscomKairosDataDbEntitiesDiagnosisEntity(_connection,
            _collectionDiagnoses)
        __fetchRelationshipcaseMediaAscomKairosDataDbEntitiesCaseMediaEntity(_connection,
            _collectionMedia)
        val _result: MutableList<CaseWithRelations> = mutableListOf()
        while (_stmt.step()) {
          val _item: CaseWithRelations
          val _tmpCase: CaseEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpPatientId: Long
          _tmpPatientId = _stmt.getLong(_columnIndexOfPatientId)
          val _tmpCaseDate: Long
          _tmpCaseDate = _stmt.getLong(_columnIndexOfCaseDate)
          val _tmpMechanism: String?
          if (_stmt.isNull(_columnIndexOfMechanism)) {
            _tmpMechanism = null
          } else {
            _tmpMechanism = _stmt.getText(_columnIndexOfMechanism)
          }
          val _tmpNotesHtml: String?
          if (_stmt.isNull(_columnIndexOfNotesHtml)) {
            _tmpNotesHtml = null
          } else {
            _tmpNotesHtml = _stmt.getText(_columnIndexOfNotesHtml)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpRemoteId: String?
          if (_stmt.isNull(_columnIndexOfRemoteId)) {
            _tmpRemoteId = null
          } else {
            _tmpRemoteId = _stmt.getText(_columnIndexOfRemoteId)
          }
          val _tmpSyncState: String
          _tmpSyncState = _stmt.getText(_columnIndexOfSyncState)
          val _tmpLastSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfLastSyncedAt)) {
            _tmpLastSyncedAt = null
          } else {
            _tmpLastSyncedAt = _stmt.getLong(_columnIndexOfLastSyncedAt)
          }
          _tmpCase =
              CaseEntity(_tmpId,_tmpPatientId,_tmpCaseDate,_tmpMechanism,_tmpNotesHtml,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpRemoteId,_tmpSyncState,_tmpLastSyncedAt)
          val _tmpPatient: PatientWithPhones?
          val _tmpKey_3: Long
          _tmpKey_3 = _stmt.getLong(_columnIndexOfPatientId)
          _tmpPatient = _collectionPatient.get(_tmpKey_3)
          val _tmpDiagnosesCollection: MutableList<DiagnosisEntity>
          val _tmpKey_4: Long
          _tmpKey_4 = _stmt.getLong(_columnIndexOfId)
          _tmpDiagnosesCollection = checkNotNull(_collectionDiagnoses.get(_tmpKey_4))
          val _tmpMediaCollection: MutableList<CaseMediaEntity>
          val _tmpKey_5: Long
          _tmpKey_5 = _stmt.getLong(_columnIndexOfId)
          _tmpMediaCollection = checkNotNull(_collectionMedia.get(_tmpKey_5))
          _item =
              CaseWithRelations(_tmpCase,_tmpPatient,_tmpDiagnosesCollection,_tmpMediaCollection)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeBySession(sessionId: Long): Flow<List<CaseWithRelations>> {
    val _sql: String = """
        |
        |        SELECT c.* FROM cases c
        |        INNER JOIN consultation_cases cc ON cc.case_id = c.id
        |        WHERE cc.session_id = ? AND c.is_deleted = 0
        |        ORDER BY c.case_date DESC
        |        
        """.trimMargin()
    return createFlow(__db, true, arrayOf("patient_phones", "patients", "case_diagnoses",
        "diagnoses", "case_media", "cases", "consultation_cases")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sessionId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfPatientId: Int = getColumnIndexOrThrow(_stmt, "patient_id")
        val _columnIndexOfCaseDate: Int = getColumnIndexOrThrow(_stmt, "case_date")
        val _columnIndexOfMechanism: Int = getColumnIndexOrThrow(_stmt, "mechanism")
        val _columnIndexOfNotesHtml: Int = getColumnIndexOrThrow(_stmt, "notes_html")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updated_at")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "is_deleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deleted_at")
        val _columnIndexOfRemoteId: Int = getColumnIndexOrThrow(_stmt, "remote_id")
        val _columnIndexOfSyncState: Int = getColumnIndexOrThrow(_stmt, "sync_state")
        val _columnIndexOfLastSyncedAt: Int = getColumnIndexOrThrow(_stmt, "last_synced_at")
        val _collectionPatient: LongSparseArray<PatientWithPhones?> =
            LongSparseArray<PatientWithPhones?>()
        val _collectionDiagnoses: LongSparseArray<MutableList<DiagnosisEntity>> =
            LongSparseArray<MutableList<DiagnosisEntity>>()
        val _collectionMedia: LongSparseArray<MutableList<CaseMediaEntity>> =
            LongSparseArray<MutableList<CaseMediaEntity>>()
        while (_stmt.step()) {
          val _tmpKey: Long
          _tmpKey = _stmt.getLong(_columnIndexOfPatientId)
          _collectionPatient.put(_tmpKey, null)
          val _tmpKey_1: Long
          _tmpKey_1 = _stmt.getLong(_columnIndexOfId)
          if (!_collectionDiagnoses.containsKey(_tmpKey_1)) {
            _collectionDiagnoses.put(_tmpKey_1, mutableListOf())
          }
          val _tmpKey_2: Long
          _tmpKey_2 = _stmt.getLong(_columnIndexOfId)
          if (!_collectionMedia.containsKey(_tmpKey_2)) {
            _collectionMedia.put(_tmpKey_2, mutableListOf())
          }
        }
        _stmt.reset()
        __fetchRelationshippatientsAscomKairosDataDbRelationsPatientWithPhones(_connection,
            _collectionPatient)
        __fetchRelationshipdiagnosesAscomKairosDataDbEntitiesDiagnosisEntity(_connection,
            _collectionDiagnoses)
        __fetchRelationshipcaseMediaAscomKairosDataDbEntitiesCaseMediaEntity(_connection,
            _collectionMedia)
        val _result: MutableList<CaseWithRelations> = mutableListOf()
        while (_stmt.step()) {
          val _item: CaseWithRelations
          val _tmpCase: CaseEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpPatientId: Long
          _tmpPatientId = _stmt.getLong(_columnIndexOfPatientId)
          val _tmpCaseDate: Long
          _tmpCaseDate = _stmt.getLong(_columnIndexOfCaseDate)
          val _tmpMechanism: String?
          if (_stmt.isNull(_columnIndexOfMechanism)) {
            _tmpMechanism = null
          } else {
            _tmpMechanism = _stmt.getText(_columnIndexOfMechanism)
          }
          val _tmpNotesHtml: String?
          if (_stmt.isNull(_columnIndexOfNotesHtml)) {
            _tmpNotesHtml = null
          } else {
            _tmpNotesHtml = _stmt.getText(_columnIndexOfNotesHtml)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpRemoteId: String?
          if (_stmt.isNull(_columnIndexOfRemoteId)) {
            _tmpRemoteId = null
          } else {
            _tmpRemoteId = _stmt.getText(_columnIndexOfRemoteId)
          }
          val _tmpSyncState: String
          _tmpSyncState = _stmt.getText(_columnIndexOfSyncState)
          val _tmpLastSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfLastSyncedAt)) {
            _tmpLastSyncedAt = null
          } else {
            _tmpLastSyncedAt = _stmt.getLong(_columnIndexOfLastSyncedAt)
          }
          _tmpCase =
              CaseEntity(_tmpId,_tmpPatientId,_tmpCaseDate,_tmpMechanism,_tmpNotesHtml,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpRemoteId,_tmpSyncState,_tmpLastSyncedAt)
          val _tmpPatient: PatientWithPhones?
          val _tmpKey_3: Long
          _tmpKey_3 = _stmt.getLong(_columnIndexOfPatientId)
          _tmpPatient = _collectionPatient.get(_tmpKey_3)
          val _tmpDiagnosesCollection: MutableList<DiagnosisEntity>
          val _tmpKey_4: Long
          _tmpKey_4 = _stmt.getLong(_columnIndexOfId)
          _tmpDiagnosesCollection = checkNotNull(_collectionDiagnoses.get(_tmpKey_4))
          val _tmpMediaCollection: MutableList<CaseMediaEntity>
          val _tmpKey_5: Long
          _tmpKey_5 = _stmt.getLong(_columnIndexOfId)
          _tmpMediaCollection = checkNotNull(_collectionMedia.get(_tmpKey_5))
          _item =
              CaseWithRelations(_tmpCase,_tmpPatient,_tmpDiagnosesCollection,_tmpMediaCollection)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeTrashed(): Flow<List<CaseWithRelations>> {
    val _sql: String = "SELECT * FROM cases WHERE is_deleted = 1 ORDER BY deleted_at DESC"
    return createFlow(__db, true, arrayOf("patient_phones", "patients", "case_diagnoses",
        "diagnoses", "case_media", "cases")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfPatientId: Int = getColumnIndexOrThrow(_stmt, "patient_id")
        val _columnIndexOfCaseDate: Int = getColumnIndexOrThrow(_stmt, "case_date")
        val _columnIndexOfMechanism: Int = getColumnIndexOrThrow(_stmt, "mechanism")
        val _columnIndexOfNotesHtml: Int = getColumnIndexOrThrow(_stmt, "notes_html")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updated_at")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "is_deleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deleted_at")
        val _columnIndexOfRemoteId: Int = getColumnIndexOrThrow(_stmt, "remote_id")
        val _columnIndexOfSyncState: Int = getColumnIndexOrThrow(_stmt, "sync_state")
        val _columnIndexOfLastSyncedAt: Int = getColumnIndexOrThrow(_stmt, "last_synced_at")
        val _collectionPatient: LongSparseArray<PatientWithPhones?> =
            LongSparseArray<PatientWithPhones?>()
        val _collectionDiagnoses: LongSparseArray<MutableList<DiagnosisEntity>> =
            LongSparseArray<MutableList<DiagnosisEntity>>()
        val _collectionMedia: LongSparseArray<MutableList<CaseMediaEntity>> =
            LongSparseArray<MutableList<CaseMediaEntity>>()
        while (_stmt.step()) {
          val _tmpKey: Long
          _tmpKey = _stmt.getLong(_columnIndexOfPatientId)
          _collectionPatient.put(_tmpKey, null)
          val _tmpKey_1: Long
          _tmpKey_1 = _stmt.getLong(_columnIndexOfId)
          if (!_collectionDiagnoses.containsKey(_tmpKey_1)) {
            _collectionDiagnoses.put(_tmpKey_1, mutableListOf())
          }
          val _tmpKey_2: Long
          _tmpKey_2 = _stmt.getLong(_columnIndexOfId)
          if (!_collectionMedia.containsKey(_tmpKey_2)) {
            _collectionMedia.put(_tmpKey_2, mutableListOf())
          }
        }
        _stmt.reset()
        __fetchRelationshippatientsAscomKairosDataDbRelationsPatientWithPhones(_connection,
            _collectionPatient)
        __fetchRelationshipdiagnosesAscomKairosDataDbEntitiesDiagnosisEntity(_connection,
            _collectionDiagnoses)
        __fetchRelationshipcaseMediaAscomKairosDataDbEntitiesCaseMediaEntity(_connection,
            _collectionMedia)
        val _result: MutableList<CaseWithRelations> = mutableListOf()
        while (_stmt.step()) {
          val _item: CaseWithRelations
          val _tmpCase: CaseEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpPatientId: Long
          _tmpPatientId = _stmt.getLong(_columnIndexOfPatientId)
          val _tmpCaseDate: Long
          _tmpCaseDate = _stmt.getLong(_columnIndexOfCaseDate)
          val _tmpMechanism: String?
          if (_stmt.isNull(_columnIndexOfMechanism)) {
            _tmpMechanism = null
          } else {
            _tmpMechanism = _stmt.getText(_columnIndexOfMechanism)
          }
          val _tmpNotesHtml: String?
          if (_stmt.isNull(_columnIndexOfNotesHtml)) {
            _tmpNotesHtml = null
          } else {
            _tmpNotesHtml = _stmt.getText(_columnIndexOfNotesHtml)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpRemoteId: String?
          if (_stmt.isNull(_columnIndexOfRemoteId)) {
            _tmpRemoteId = null
          } else {
            _tmpRemoteId = _stmt.getText(_columnIndexOfRemoteId)
          }
          val _tmpSyncState: String
          _tmpSyncState = _stmt.getText(_columnIndexOfSyncState)
          val _tmpLastSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfLastSyncedAt)) {
            _tmpLastSyncedAt = null
          } else {
            _tmpLastSyncedAt = _stmt.getLong(_columnIndexOfLastSyncedAt)
          }
          _tmpCase =
              CaseEntity(_tmpId,_tmpPatientId,_tmpCaseDate,_tmpMechanism,_tmpNotesHtml,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpRemoteId,_tmpSyncState,_tmpLastSyncedAt)
          val _tmpPatient: PatientWithPhones?
          val _tmpKey_3: Long
          _tmpKey_3 = _stmt.getLong(_columnIndexOfPatientId)
          _tmpPatient = _collectionPatient.get(_tmpKey_3)
          val _tmpDiagnosesCollection: MutableList<DiagnosisEntity>
          val _tmpKey_4: Long
          _tmpKey_4 = _stmt.getLong(_columnIndexOfId)
          _tmpDiagnosesCollection = checkNotNull(_collectionDiagnoses.get(_tmpKey_4))
          val _tmpMediaCollection: MutableList<CaseMediaEntity>
          val _tmpKey_5: Long
          _tmpKey_5 = _stmt.getLong(_columnIndexOfId)
          _tmpMediaCollection = checkNotNull(_collectionMedia.get(_tmpKey_5))
          _item =
              CaseWithRelations(_tmpCase,_tmpPatient,_tmpDiagnosesCollection,_tmpMediaCollection)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun listExpiredTrash(threshold: Long): List<Long> {
    val _sql: String = "SELECT id FROM cases WHERE is_deleted = 1 AND deleted_at < ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, threshold)
        val _result: MutableList<Long> = mutableListOf()
        while (_stmt.step()) {
          val _item: Long
          _item = _stmt.getLong(0)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearDiagnosisLinks(caseId: Long) {
    val _sql: String = "DELETE FROM case_diagnoses WHERE case_id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, caseId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun unlinkFromShift(shiftId: Long, caseId: Long) {
    val _sql: String = "DELETE FROM shift_cases WHERE shift_id = ? AND case_id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, shiftId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, caseId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun unlinkFromSession(sessionId: Long, caseId: Long) {
    val _sql: String = "DELETE FROM consultation_cases WHERE session_id = ? AND case_id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, sessionId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, caseId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun softDelete(id: Long, now: Long) {
    val _sql: String = """
        |
        |        UPDATE cases
        |        SET is_deleted = 1, deleted_at = ?, sync_state = 'DELETED', updated_at = ?
        |        WHERE id = ?
        |        
        """.trimMargin()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, now)
        _argIndex = 2
        _stmt.bindLong(_argIndex, now)
        _argIndex = 3
        _stmt.bindLong(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun restore(id: Long, now: Long) {
    val _sql: String = """
        |
        |        UPDATE cases
        |        SET is_deleted = 0, deleted_at = NULL, sync_state = 'MODIFIED', updated_at = ?
        |        WHERE id = ?
        |        
        """.trimMargin()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, now)
        _argIndex = 2
        _stmt.bindLong(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun hardDelete(ids: List<Long>) {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("DELETE FROM cases WHERE id IN (")
    val _inputSize: Int = ids.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        for (_item: Long in ids) {
          _stmt.bindLong(_argIndex, _item)
          _argIndex++
        }
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  private
      fun __fetchRelationshippatientPhonesAscomKairosDataDbEntitiesPatientPhoneEntity(_connection: SQLiteConnection,
      _map: LongSparseArray<MutableList<PatientPhoneEntity>>) {
    if (_map.isEmpty()) {
      return
    }
    if (_map.size() > 999) {
      recursiveFetchLongSparseArray(_map, true) { _tmpMap ->
        __fetchRelationshippatientPhonesAscomKairosDataDbEntitiesPatientPhoneEntity(_connection,
            _tmpMap)
      }
      return
    }
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("SELECT `id`,`patient_id`,`number`,`label` FROM `patient_phones` WHERE `patient_id` IN (")
    val _inputSize: Int = _map.size()
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    val _stmt: SQLiteStatement = _connection.prepare(_sql)
    var _argIndex: Int = 1
    for (i in 0 until _map.size()) {
      val _item: Long = _map.keyAt(i)
      _stmt.bindLong(_argIndex, _item)
      _argIndex++
    }
    try {
      val _itemKeyIndex: Int = getColumnIndex(_stmt, "patient_id")
      if (_itemKeyIndex == -1) {
        return
      }
      val _columnIndexOfId: Int = 0
      val _columnIndexOfPatientId: Int = 1
      val _columnIndexOfNumber: Int = 2
      val _columnIndexOfLabel: Int = 3
      while (_stmt.step()) {
        val _tmpKey: Long
        _tmpKey = _stmt.getLong(_itemKeyIndex)
        val _tmpRelation: MutableList<PatientPhoneEntity>? = _map.get(_tmpKey)
        if (_tmpRelation != null) {
          val _item_1: PatientPhoneEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpPatientId: Long
          _tmpPatientId = _stmt.getLong(_columnIndexOfPatientId)
          val _tmpNumber: String
          _tmpNumber = _stmt.getText(_columnIndexOfNumber)
          val _tmpLabel: String?
          if (_stmt.isNull(_columnIndexOfLabel)) {
            _tmpLabel = null
          } else {
            _tmpLabel = _stmt.getText(_columnIndexOfLabel)
          }
          _item_1 = PatientPhoneEntity(_tmpId,_tmpPatientId,_tmpNumber,_tmpLabel)
          _tmpRelation.add(_item_1)
        }
      }
    } finally {
      _stmt.close()
    }
  }

  private
      fun __fetchRelationshippatientsAscomKairosDataDbRelationsPatientWithPhones(_connection: SQLiteConnection,
      _map: LongSparseArray<PatientWithPhones?>) {
    if (_map.isEmpty()) {
      return
    }
    if (_map.size() > 999) {
      recursiveFetchLongSparseArray(_map, false) { _tmpMap ->
        __fetchRelationshippatientsAscomKairosDataDbRelationsPatientWithPhones(_connection, _tmpMap)
      }
      return
    }
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("SELECT `id`,`name`,`age`,`created_at`,`updated_at`,`is_deleted`,`deleted_at`,`remote_id`,`sync_state`,`last_synced_at` FROM `patients` WHERE `id` IN (")
    val _inputSize: Int = _map.size()
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    val _stmt: SQLiteStatement = _connection.prepare(_sql)
    var _argIndex: Int = 1
    for (i in 0 until _map.size()) {
      val _item: Long = _map.keyAt(i)
      _stmt.bindLong(_argIndex, _item)
      _argIndex++
    }
    try {
      val _itemKeyIndex: Int = getColumnIndex(_stmt, "id")
      if (_itemKeyIndex == -1) {
        return
      }
      val _columnIndexOfId: Int = 0
      val _columnIndexOfName: Int = 1
      val _columnIndexOfAge: Int = 2
      val _columnIndexOfCreatedAt: Int = 3
      val _columnIndexOfUpdatedAt: Int = 4
      val _columnIndexOfIsDeleted: Int = 5
      val _columnIndexOfDeletedAt: Int = 6
      val _columnIndexOfRemoteId: Int = 7
      val _columnIndexOfSyncState: Int = 8
      val _columnIndexOfLastSyncedAt: Int = 9
      val _collectionPhones: LongSparseArray<MutableList<PatientPhoneEntity>> =
          LongSparseArray<MutableList<PatientPhoneEntity>>()
      while (_stmt.step()) {
        val _tmpKey: Long
        _tmpKey = _stmt.getLong(_columnIndexOfId)
        if (!_collectionPhones.containsKey(_tmpKey)) {
          _collectionPhones.put(_tmpKey, mutableListOf())
        }
      }
      _stmt.reset()
      __fetchRelationshippatientPhonesAscomKairosDataDbEntitiesPatientPhoneEntity(_connection,
          _collectionPhones)
      while (_stmt.step()) {
        val _tmpKey_1: Long
        _tmpKey_1 = _stmt.getLong(_itemKeyIndex)
        if (_map.containsKey(_tmpKey_1)) {
          val _item_1: PatientWithPhones?
          val _tmpPatient: PatientEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpAge: Int?
          if (_stmt.isNull(_columnIndexOfAge)) {
            _tmpAge = null
          } else {
            _tmpAge = _stmt.getLong(_columnIndexOfAge).toInt()
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          val _tmpRemoteId: String?
          if (_stmt.isNull(_columnIndexOfRemoteId)) {
            _tmpRemoteId = null
          } else {
            _tmpRemoteId = _stmt.getText(_columnIndexOfRemoteId)
          }
          val _tmpSyncState: String
          _tmpSyncState = _stmt.getText(_columnIndexOfSyncState)
          val _tmpLastSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfLastSyncedAt)) {
            _tmpLastSyncedAt = null
          } else {
            _tmpLastSyncedAt = _stmt.getLong(_columnIndexOfLastSyncedAt)
          }
          _tmpPatient =
              PatientEntity(_tmpId,_tmpName,_tmpAge,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpRemoteId,_tmpSyncState,_tmpLastSyncedAt)
          val _tmpPhonesCollection: MutableList<PatientPhoneEntity>
          val _tmpKey_2: Long
          _tmpKey_2 = _stmt.getLong(_columnIndexOfId)
          _tmpPhonesCollection = checkNotNull(_collectionPhones.get(_tmpKey_2))
          _item_1 = PatientWithPhones(_tmpPatient,_tmpPhonesCollection)
          _map.put(_tmpKey_1, _item_1)
        }
      }
    } finally {
      _stmt.close()
    }
  }

  private
      fun __fetchRelationshipdiagnosesAscomKairosDataDbEntitiesDiagnosisEntity(_connection: SQLiteConnection,
      _map: LongSparseArray<MutableList<DiagnosisEntity>>) {
    if (_map.isEmpty()) {
      return
    }
    if (_map.size() > 999) {
      recursiveFetchLongSparseArray(_map, true) { _tmpMap ->
        __fetchRelationshipdiagnosesAscomKairosDataDbEntitiesDiagnosisEntity(_connection, _tmpMap)
      }
      return
    }
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("SELECT `diagnoses`.`id` AS `id`,`diagnoses`.`name` AS `name`,`diagnoses`.`created_at` AS `created_at`,`diagnoses`.`remote_id` AS `remote_id`,`diagnoses`.`sync_state` AS `sync_state`,_junction.`case_id` FROM `case_diagnoses` AS _junction INNER JOIN `diagnoses` ON (_junction.`diagnosis_id` = `diagnoses`.`id`) WHERE _junction.`case_id` IN (")
    val _inputSize: Int = _map.size()
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    val _stmt: SQLiteStatement = _connection.prepare(_sql)
    var _argIndex: Int = 1
    for (i in 0 until _map.size()) {
      val _item: Long = _map.keyAt(i)
      _stmt.bindLong(_argIndex, _item)
      _argIndex++
    }
    try {
      // _junction.case_id
      val _itemKeyIndex: Int = 5
      if (_itemKeyIndex == -1) {
        return
      }
      val _columnIndexOfId: Int = 0
      val _columnIndexOfName: Int = 1
      val _columnIndexOfCreatedAt: Int = 2
      val _columnIndexOfRemoteId: Int = 3
      val _columnIndexOfSyncState: Int = 4
      while (_stmt.step()) {
        val _tmpKey: Long
        _tmpKey = _stmt.getLong(_itemKeyIndex)
        val _tmpRelation: MutableList<DiagnosisEntity>? = _map.get(_tmpKey)
        if (_tmpRelation != null) {
          val _item_1: DiagnosisEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpRemoteId: String?
          if (_stmt.isNull(_columnIndexOfRemoteId)) {
            _tmpRemoteId = null
          } else {
            _tmpRemoteId = _stmt.getText(_columnIndexOfRemoteId)
          }
          val _tmpSyncState: String
          _tmpSyncState = _stmt.getText(_columnIndexOfSyncState)
          _item_1 = DiagnosisEntity(_tmpId,_tmpName,_tmpCreatedAt,_tmpRemoteId,_tmpSyncState)
          _tmpRelation.add(_item_1)
        }
      }
    } finally {
      _stmt.close()
    }
  }

  private
      fun __fetchRelationshipcaseMediaAscomKairosDataDbEntitiesCaseMediaEntity(_connection: SQLiteConnection,
      _map: LongSparseArray<MutableList<CaseMediaEntity>>) {
    if (_map.isEmpty()) {
      return
    }
    if (_map.size() > 999) {
      recursiveFetchLongSparseArray(_map, true) { _tmpMap ->
        __fetchRelationshipcaseMediaAscomKairosDataDbEntitiesCaseMediaEntity(_connection, _tmpMap)
      }
      return
    }
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("SELECT `id`,`case_id`,`file_path`,`media_type`,`duration_ms`,`is_primary`,`created_at` FROM `case_media` WHERE `case_id` IN (")
    val _inputSize: Int = _map.size()
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    val _stmt: SQLiteStatement = _connection.prepare(_sql)
    var _argIndex: Int = 1
    for (i in 0 until _map.size()) {
      val _item: Long = _map.keyAt(i)
      _stmt.bindLong(_argIndex, _item)
      _argIndex++
    }
    try {
      val _itemKeyIndex: Int = getColumnIndex(_stmt, "case_id")
      if (_itemKeyIndex == -1) {
        return
      }
      val _columnIndexOfId: Int = 0
      val _columnIndexOfCaseId: Int = 1
      val _columnIndexOfFilePath: Int = 2
      val _columnIndexOfMediaType: Int = 3
      val _columnIndexOfDurationMs: Int = 4
      val _columnIndexOfIsPrimary: Int = 5
      val _columnIndexOfCreatedAt: Int = 6
      while (_stmt.step()) {
        val _tmpKey: Long
        _tmpKey = _stmt.getLong(_itemKeyIndex)
        val _tmpRelation: MutableList<CaseMediaEntity>? = _map.get(_tmpKey)
        if (_tmpRelation != null) {
          val _item_1: CaseMediaEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpCaseId: Long
          _tmpCaseId = _stmt.getLong(_columnIndexOfCaseId)
          val _tmpFilePath: String
          _tmpFilePath = _stmt.getText(_columnIndexOfFilePath)
          val _tmpMediaType: String
          _tmpMediaType = _stmt.getText(_columnIndexOfMediaType)
          val _tmpDurationMs: Long?
          if (_stmt.isNull(_columnIndexOfDurationMs)) {
            _tmpDurationMs = null
          } else {
            _tmpDurationMs = _stmt.getLong(_columnIndexOfDurationMs)
          }
          val _tmpIsPrimary: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsPrimary).toInt()
          _tmpIsPrimary = _tmp != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item_1 =
              CaseMediaEntity(_tmpId,_tmpCaseId,_tmpFilePath,_tmpMediaType,_tmpDurationMs,_tmpIsPrimary,_tmpCreatedAt)
          _tmpRelation.add(_item_1)
        }
      }
    } finally {
      _stmt.close()
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
