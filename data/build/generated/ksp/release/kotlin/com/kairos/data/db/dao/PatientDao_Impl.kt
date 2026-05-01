package com.kairos.`data`.db.dao

import androidx.collection.LongSparseArray
import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndex
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.getTotalChangedRows
import androidx.room.util.performSuspending
import androidx.room.util.recursiveFetchLongSparseArray
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.SQLiteStatement
import com.kairos.`data`.db.entities.PatientEntity
import com.kairos.`data`.db.entities.PatientPhoneEntity
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
public class PatientDao_Impl(
  __db: RoomDatabase,
) : PatientDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfPatientEntity: EntityInsertAdapter<PatientEntity>

  private val __insertAdapterOfPatientPhoneEntity: EntityInsertAdapter<PatientPhoneEntity>

  private val __updateAdapterOfPatientEntity: EntityDeleteOrUpdateAdapter<PatientEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfPatientEntity = object : EntityInsertAdapter<PatientEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `patients` (`id`,`name`,`age`,`created_at`,`updated_at`,`is_deleted`,`deleted_at`,`remote_id`,`sync_state`,`last_synced_at`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: PatientEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.name)
        val _tmpAge: Int? = entity.age
        if (_tmpAge == null) {
          statement.bindNull(3)
        } else {
          statement.bindLong(3, _tmpAge.toLong())
        }
        statement.bindLong(4, entity.createdAt)
        statement.bindLong(5, entity.updatedAt)
        val _tmp: Int = if (entity.isDeleted) 1 else 0
        statement.bindLong(6, _tmp.toLong())
        val _tmpDeletedAt: Long? = entity.deletedAt
        if (_tmpDeletedAt == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmpDeletedAt)
        }
        val _tmpRemoteId: String? = entity.remoteId
        if (_tmpRemoteId == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpRemoteId)
        }
        statement.bindText(9, entity.syncState)
        val _tmpLastSyncedAt: Long? = entity.lastSyncedAt
        if (_tmpLastSyncedAt == null) {
          statement.bindNull(10)
        } else {
          statement.bindLong(10, _tmpLastSyncedAt)
        }
      }
    }
    this.__insertAdapterOfPatientPhoneEntity = object : EntityInsertAdapter<PatientPhoneEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `patient_phones` (`id`,`patient_id`,`number`,`label`) VALUES (nullif(?, 0),?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: PatientPhoneEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.patientId)
        statement.bindText(3, entity.number)
        val _tmpLabel: String? = entity.label
        if (_tmpLabel == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpLabel)
        }
      }
    }
    this.__updateAdapterOfPatientEntity = object : EntityDeleteOrUpdateAdapter<PatientEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `patients` SET `id` = ?,`name` = ?,`age` = ?,`created_at` = ?,`updated_at` = ?,`is_deleted` = ?,`deleted_at` = ?,`remote_id` = ?,`sync_state` = ?,`last_synced_at` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: PatientEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.name)
        val _tmpAge: Int? = entity.age
        if (_tmpAge == null) {
          statement.bindNull(3)
        } else {
          statement.bindLong(3, _tmpAge.toLong())
        }
        statement.bindLong(4, entity.createdAt)
        statement.bindLong(5, entity.updatedAt)
        val _tmp: Int = if (entity.isDeleted) 1 else 0
        statement.bindLong(6, _tmp.toLong())
        val _tmpDeletedAt: Long? = entity.deletedAt
        if (_tmpDeletedAt == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmpDeletedAt)
        }
        val _tmpRemoteId: String? = entity.remoteId
        if (_tmpRemoteId == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpRemoteId)
        }
        statement.bindText(9, entity.syncState)
        val _tmpLastSyncedAt: Long? = entity.lastSyncedAt
        if (_tmpLastSyncedAt == null) {
          statement.bindNull(10)
        } else {
          statement.bindLong(10, _tmpLastSyncedAt)
        }
        statement.bindLong(11, entity.id)
      }
    }
  }

  public override suspend fun insert(patient: PatientEntity): Long = performSuspending(__db, false,
      true) { _connection ->
    val _result: Long = __insertAdapterOfPatientEntity.insertAndReturnId(_connection, patient)
    _result
  }

  public override suspend fun insertPhones(phones: List<PatientPhoneEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfPatientPhoneEntity.insert(_connection, phones)
  }

  public override suspend fun update(patient: PatientEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __updateAdapterOfPatientEntity.handle(_connection, patient)
  }

  public override suspend fun getById(id: Long): PatientWithPhones? {
    val _sql: String = "SELECT * FROM patients WHERE id = ?"
    return performSuspending(__db, true, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfAge: Int = getColumnIndexOrThrow(_stmt, "age")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updated_at")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "is_deleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deleted_at")
        val _columnIndexOfRemoteId: Int = getColumnIndexOrThrow(_stmt, "remote_id")
        val _columnIndexOfSyncState: Int = getColumnIndexOrThrow(_stmt, "sync_state")
        val _columnIndexOfLastSyncedAt: Int = getColumnIndexOrThrow(_stmt, "last_synced_at")
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
        val _result: PatientWithPhones?
        if (_stmt.step()) {
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
          val _tmpKey_1: Long
          _tmpKey_1 = _stmt.getLong(_columnIndexOfId)
          _tmpPhonesCollection = checkNotNull(_collectionPhones.get(_tmpKey_1))
          _result = PatientWithPhones(_tmpPatient,_tmpPhonesCollection)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeById(id: Long): Flow<PatientWithPhones?> {
    val _sql: String = "SELECT * FROM patients WHERE id = ?"
    return createFlow(__db, true, arrayOf("patient_phones", "patients")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfAge: Int = getColumnIndexOrThrow(_stmt, "age")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updated_at")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "is_deleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deleted_at")
        val _columnIndexOfRemoteId: Int = getColumnIndexOrThrow(_stmt, "remote_id")
        val _columnIndexOfSyncState: Int = getColumnIndexOrThrow(_stmt, "sync_state")
        val _columnIndexOfLastSyncedAt: Int = getColumnIndexOrThrow(_stmt, "last_synced_at")
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
        val _result: PatientWithPhones?
        if (_stmt.step()) {
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
          val _tmpKey_1: Long
          _tmpKey_1 = _stmt.getLong(_columnIndexOfId)
          _tmpPhonesCollection = checkNotNull(_collectionPhones.get(_tmpKey_1))
          _result = PatientWithPhones(_tmpPatient,_tmpPhonesCollection)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun search(query: String): Flow<List<PatientWithPhones>> {
    val _sql: String = """
        |
        |        SELECT * FROM patients
        |        WHERE is_deleted = 0
        |          AND name LIKE '%' || ? || '%' COLLATE NOCASE
        |        ORDER BY name COLLATE NOCASE ASC
        |        LIMIT 50
        |        
        """.trimMargin()
    return createFlow(__db, true, arrayOf("patient_phones", "patients")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, query)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfAge: Int = getColumnIndexOrThrow(_stmt, "age")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updated_at")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "is_deleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deleted_at")
        val _columnIndexOfRemoteId: Int = getColumnIndexOrThrow(_stmt, "remote_id")
        val _columnIndexOfSyncState: Int = getColumnIndexOrThrow(_stmt, "sync_state")
        val _columnIndexOfLastSyncedAt: Int = getColumnIndexOrThrow(_stmt, "last_synced_at")
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
        val _result: MutableList<PatientWithPhones> = mutableListOf()
        while (_stmt.step()) {
          val _item: PatientWithPhones
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
          val _tmpKey_1: Long
          _tmpKey_1 = _stmt.getLong(_columnIndexOfId)
          _tmpPhonesCollection = checkNotNull(_collectionPhones.get(_tmpKey_1))
          _item = PatientWithPhones(_tmpPatient,_tmpPhonesCollection)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeTrashed(): Flow<List<PatientWithPhones>> {
    val _sql: String = "SELECT * FROM patients WHERE is_deleted = 1 ORDER BY deleted_at DESC"
    return createFlow(__db, true, arrayOf("patient_phones", "patients")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfAge: Int = getColumnIndexOrThrow(_stmt, "age")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updated_at")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "is_deleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deleted_at")
        val _columnIndexOfRemoteId: Int = getColumnIndexOrThrow(_stmt, "remote_id")
        val _columnIndexOfSyncState: Int = getColumnIndexOrThrow(_stmt, "sync_state")
        val _columnIndexOfLastSyncedAt: Int = getColumnIndexOrThrow(_stmt, "last_synced_at")
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
        val _result: MutableList<PatientWithPhones> = mutableListOf()
        while (_stmt.step()) {
          val _item: PatientWithPhones
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
          val _tmpKey_1: Long
          _tmpKey_1 = _stmt.getLong(_columnIndexOfId)
          _tmpPhonesCollection = checkNotNull(_collectionPhones.get(_tmpKey_1))
          _item = PatientWithPhones(_tmpPatient,_tmpPhonesCollection)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deletePhonesFor(patientId: Long) {
    val _sql: String = "DELETE FROM patient_phones WHERE patient_id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, patientId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun softDelete(id: Long, now: Long) {
    val _sql: String = """
        |
        |        UPDATE patients
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
        |        UPDATE patients
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

  public override suspend fun purgeOlderThan(threshold: Long): Int {
    val _sql: String = """
        |
        |        DELETE FROM patients
        |        WHERE is_deleted = 1 AND deleted_at < ?
        |          AND id NOT IN (SELECT DISTINCT patient_id FROM cases WHERE is_deleted = 0)
        |    
        """.trimMargin()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, threshold)
        _stmt.step()
        getTotalChangedRows(_connection)
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

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
