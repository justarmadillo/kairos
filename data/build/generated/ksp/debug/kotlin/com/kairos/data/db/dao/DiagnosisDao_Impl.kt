package com.kairos.`data`.db.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.kairos.`data`.db.entities.DiagnosisEntity
import com.kairos.`data`.db.relations.DiagnosisWithCount
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class DiagnosisDao_Impl(
  __db: RoomDatabase,
) : DiagnosisDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfDiagnosisEntity: EntityInsertAdapter<DiagnosisEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfDiagnosisEntity = object : EntityInsertAdapter<DiagnosisEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR IGNORE INTO `diagnoses` (`id`,`name`,`created_at`,`remote_id`,`sync_state`) VALUES (nullif(?, 0),?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: DiagnosisEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindLong(3, entity.createdAt)
        val _tmpRemoteId: String? = entity.remoteId
        if (_tmpRemoteId == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpRemoteId)
        }
        statement.bindText(5, entity.syncState)
      }
    }
  }

  public override suspend fun insert(diagnosis: DiagnosisEntity): Long = performSuspending(__db,
      false, true) { _connection ->
    val _result: Long = __insertAdapterOfDiagnosisEntity.insertAndReturnId(_connection, diagnosis)
    _result
  }

  public override suspend fun findByNameCi(name: String): DiagnosisEntity? {
    val _sql: String = "SELECT * FROM diagnoses WHERE name = ? COLLATE NOCASE LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, name)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _columnIndexOfRemoteId: Int = getColumnIndexOrThrow(_stmt, "remote_id")
        val _columnIndexOfSyncState: Int = getColumnIndexOrThrow(_stmt, "sync_state")
        val _result: DiagnosisEntity?
        if (_stmt.step()) {
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
          _result = DiagnosisEntity(_tmpId,_tmpName,_tmpCreatedAt,_tmpRemoteId,_tmpSyncState)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getById(id: Long): DiagnosisEntity? {
    val _sql: String = "SELECT * FROM diagnoses WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _columnIndexOfRemoteId: Int = getColumnIndexOrThrow(_stmt, "remote_id")
        val _columnIndexOfSyncState: Int = getColumnIndexOrThrow(_stmt, "sync_state")
        val _result: DiagnosisEntity?
        if (_stmt.step()) {
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
          _result = DiagnosisEntity(_tmpId,_tmpName,_tmpCreatedAt,_tmpRemoteId,_tmpSyncState)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeAllAlphabetical(): Flow<List<DiagnosisWithCount>> {
    val _sql: String = """
        |
        |        SELECT d.id AS id, d.name AS name,
        |               (SELECT COUNT(*) FROM case_diagnoses cd
        |                INNER JOIN cases c ON c.id = cd.case_id
        |                WHERE cd.diagnosis_id = d.id AND c.is_deleted = 0) AS case_count
        |        FROM diagnoses d
        |        ORDER BY d.name COLLATE NOCASE ASC
        |        
        """.trimMargin()
    return createFlow(__db, false, arrayOf("case_diagnoses", "cases", "diagnoses")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = 0
        val _columnIndexOfName: Int = 1
        val _columnIndexOfCaseCount: Int = 2
        val _result: MutableList<DiagnosisWithCount> = mutableListOf()
        while (_stmt.step()) {
          val _item: DiagnosisWithCount
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpCaseCount: Int
          _tmpCaseCount = _stmt.getLong(_columnIndexOfCaseCount).toInt()
          _item = DiagnosisWithCount(_tmpId,_tmpName,_tmpCaseCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeAllByMostUsed(): Flow<List<DiagnosisWithCount>> {
    val _sql: String = """
        |
        |        SELECT d.id AS id, d.name AS name,
        |               (SELECT COUNT(*) FROM case_diagnoses cd
        |                INNER JOIN cases c ON c.id = cd.case_id
        |                WHERE cd.diagnosis_id = d.id AND c.is_deleted = 0) AS case_count
        |        FROM diagnoses d
        |        ORDER BY case_count DESC, d.name COLLATE NOCASE ASC
        |        
        """.trimMargin()
    return createFlow(__db, false, arrayOf("case_diagnoses", "cases", "diagnoses")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = 0
        val _columnIndexOfName: Int = 1
        val _columnIndexOfCaseCount: Int = 2
        val _result: MutableList<DiagnosisWithCount> = mutableListOf()
        while (_stmt.step()) {
          val _item: DiagnosisWithCount
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpCaseCount: Int
          _tmpCaseCount = _stmt.getLong(_columnIndexOfCaseCount).toInt()
          _item = DiagnosisWithCount(_tmpId,_tmpName,_tmpCaseCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeAllByRecent(): Flow<List<DiagnosisWithCount>> {
    val _sql: String = """
        |
        |        SELECT d.id AS id, d.name AS name,
        |               (SELECT COUNT(*) FROM case_diagnoses cd
        |                INNER JOIN cases c ON c.id = cd.case_id
        |                WHERE cd.diagnosis_id = d.id AND c.is_deleted = 0) AS case_count
        |        FROM diagnoses d
        |        ORDER BY d.created_at DESC
        |        
        """.trimMargin()
    return createFlow(__db, false, arrayOf("case_diagnoses", "cases", "diagnoses")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = 0
        val _columnIndexOfName: Int = 1
        val _columnIndexOfCaseCount: Int = 2
        val _result: MutableList<DiagnosisWithCount> = mutableListOf()
        while (_stmt.step()) {
          val _item: DiagnosisWithCount
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpCaseCount: Int
          _tmpCaseCount = _stmt.getLong(_columnIndexOfCaseCount).toInt()
          _item = DiagnosisWithCount(_tmpId,_tmpName,_tmpCaseCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun searchByPrefix(prefix: String, limit: Int): List<DiagnosisEntity> {
    val _sql: String = """
        |
        |        SELECT * FROM diagnoses
        |        WHERE name LIKE ? || '%' COLLATE NOCASE
        |        ORDER BY name COLLATE NOCASE ASC
        |        LIMIT ?
        |        
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, prefix)
        _argIndex = 2
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _columnIndexOfRemoteId: Int = getColumnIndexOrThrow(_stmt, "remote_id")
        val _columnIndexOfSyncState: Int = getColumnIndexOrThrow(_stmt, "sync_state")
        val _result: MutableList<DiagnosisEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DiagnosisEntity
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
          _item = DiagnosisEntity(_tmpId,_tmpName,_tmpCreatedAt,_tmpRemoteId,_tmpSyncState)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
