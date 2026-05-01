package com.kairos.`data`.db.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.getTotalChangedRows
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.kairos.`data`.db.entities.ConsultationSessionEntity
import com.kairos.`data`.db.relations.SessionWithCount
import javax.`annotation`.processing.Generated
import kotlin.Boolean
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
public class ConsultationSessionDao_Impl(
  __db: RoomDatabase,
) : ConsultationSessionDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfConsultationSessionEntity:
      EntityInsertAdapter<ConsultationSessionEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfConsultationSessionEntity = object :
        EntityInsertAdapter<ConsultationSessionEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR IGNORE INTO `consultation_sessions` (`id`,`date`,`created_at`,`is_deleted`,`deleted_at`,`remote_id`,`sync_state`,`last_synced_at`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ConsultationSessionEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.date)
        statement.bindLong(3, entity.createdAt)
        val _tmp: Int = if (entity.isDeleted) 1 else 0
        statement.bindLong(4, _tmp.toLong())
        val _tmpDeletedAt: Long? = entity.deletedAt
        if (_tmpDeletedAt == null) {
          statement.bindNull(5)
        } else {
          statement.bindLong(5, _tmpDeletedAt)
        }
        val _tmpRemoteId: String? = entity.remoteId
        if (_tmpRemoteId == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpRemoteId)
        }
        statement.bindText(7, entity.syncState)
        val _tmpLastSyncedAt: Long? = entity.lastSyncedAt
        if (_tmpLastSyncedAt == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmpLastSyncedAt)
        }
      }
    }
  }

  public override suspend fun insert(session: ConsultationSessionEntity): Long =
      performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfConsultationSessionEntity.insertAndReturnId(_connection,
        session)
    _result
  }

  public override suspend fun findByDate(dateMillis: Long): ConsultationSessionEntity? {
    val _sql: String = "SELECT * FROM consultation_sessions WHERE date = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, dateMillis)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "is_deleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deleted_at")
        val _columnIndexOfRemoteId: Int = getColumnIndexOrThrow(_stmt, "remote_id")
        val _columnIndexOfSyncState: Int = getColumnIndexOrThrow(_stmt, "sync_state")
        val _columnIndexOfLastSyncedAt: Int = getColumnIndexOrThrow(_stmt, "last_synced_at")
        val _result: ConsultationSessionEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpDate: Long
          _tmpDate = _stmt.getLong(_columnIndexOfDate)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
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
          _result =
              ConsultationSessionEntity(_tmpId,_tmpDate,_tmpCreatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpRemoteId,_tmpSyncState,_tmpLastSyncedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getById(id: Long): ConsultationSessionEntity? {
    val _sql: String = "SELECT * FROM consultation_sessions WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "is_deleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deleted_at")
        val _columnIndexOfRemoteId: Int = getColumnIndexOrThrow(_stmt, "remote_id")
        val _columnIndexOfSyncState: Int = getColumnIndexOrThrow(_stmt, "sync_state")
        val _columnIndexOfLastSyncedAt: Int = getColumnIndexOrThrow(_stmt, "last_synced_at")
        val _result: ConsultationSessionEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpDate: Long
          _tmpDate = _stmt.getLong(_columnIndexOfDate)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
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
          _result =
              ConsultationSessionEntity(_tmpId,_tmpDate,_tmpCreatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpRemoteId,_tmpSyncState,_tmpLastSyncedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeForRange(start: Long, end: Long): Flow<List<SessionWithCount>> {
    val _sql: String = """
        |
        |        SELECT s.id AS id, s.date AS date, s.created_at AS created_at,
        |               (SELECT COUNT(*) FROM consultation_cases cc
        |                INNER JOIN cases c ON c.id = cc.case_id
        |                WHERE cc.session_id = s.id AND c.is_deleted = 0) AS case_count
        |        FROM consultation_sessions s
        |        WHERE s.is_deleted = 0 AND s.date BETWEEN ? AND ?
        |        ORDER BY s.date ASC
        |        
        """.trimMargin()
    return createFlow(__db, false, arrayOf("consultation_cases", "cases", "consultation_sessions"))
        { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, start)
        _argIndex = 2
        _stmt.bindLong(_argIndex, end)
        val _columnIndexOfId: Int = 0
        val _columnIndexOfDate: Int = 1
        val _columnIndexOfCreatedAt: Int = 2
        val _columnIndexOfCaseCount: Int = 3
        val _result: MutableList<SessionWithCount> = mutableListOf()
        while (_stmt.step()) {
          val _item: SessionWithCount
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpDate: Long
          _tmpDate = _stmt.getLong(_columnIndexOfDate)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpCaseCount: Int
          _tmpCaseCount = _stmt.getLong(_columnIndexOfCaseCount).toInt()
          _item = SessionWithCount(_tmpId,_tmpDate,_tmpCreatedAt,_tmpCaseCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeTrashed(): Flow<List<ConsultationSessionEntity>> {
    val _sql: String =
        "SELECT * FROM consultation_sessions WHERE is_deleted = 1 ORDER BY deleted_at DESC"
    return createFlow(__db, false, arrayOf("consultation_sessions")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "is_deleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deleted_at")
        val _columnIndexOfRemoteId: Int = getColumnIndexOrThrow(_stmt, "remote_id")
        val _columnIndexOfSyncState: Int = getColumnIndexOrThrow(_stmt, "sync_state")
        val _columnIndexOfLastSyncedAt: Int = getColumnIndexOrThrow(_stmt, "last_synced_at")
        val _result: MutableList<ConsultationSessionEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ConsultationSessionEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpDate: Long
          _tmpDate = _stmt.getLong(_columnIndexOfDate)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
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
          _item =
              ConsultationSessionEntity(_tmpId,_tmpDate,_tmpCreatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpRemoteId,_tmpSyncState,_tmpLastSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun softDelete(id: Long, now: Long) {
    val _sql: String = """
        |
        |        UPDATE consultation_sessions
        |        SET is_deleted = 1, deleted_at = ?, sync_state = 'DELETED'
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

  public override suspend fun restore(id: Long) {
    val _sql: String = """
        |
        |        UPDATE consultation_sessions
        |        SET is_deleted = 0, deleted_at = NULL, sync_state = 'MODIFIED'
        |        WHERE id = ?
        |        
        """.trimMargin()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun purgeOlderThan(threshold: Long): Int {
    val _sql: String = "DELETE FROM consultation_sessions WHERE is_deleted = 1 AND deleted_at < ?"
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

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
