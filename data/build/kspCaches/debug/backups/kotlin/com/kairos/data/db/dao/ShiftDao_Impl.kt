package com.kairos.`data`.db.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.getTotalChangedRows
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.kairos.`data`.db.entities.ShiftEntity
import com.kairos.`data`.db.relations.ShiftWithCount
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
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class ShiftDao_Impl(
  __db: RoomDatabase,
) : ShiftDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfShiftEntity: EntityInsertAdapter<ShiftEntity>

  private val __updateAdapterOfShiftEntity: EntityDeleteOrUpdateAdapter<ShiftEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfShiftEntity = object : EntityInsertAdapter<ShiftEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `shifts` (`id`,`label`,`date`,`created_at`,`is_deleted`,`deleted_at`,`remote_id`,`sync_state`,`last_synced_at`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ShiftEntity) {
        statement.bindLong(1, entity.id)
        val _tmpLabel: String? = entity.label
        if (_tmpLabel == null) {
          statement.bindNull(2)
        } else {
          statement.bindText(2, _tmpLabel)
        }
        statement.bindLong(3, entity.date)
        statement.bindLong(4, entity.createdAt)
        val _tmp: Int = if (entity.isDeleted) 1 else 0
        statement.bindLong(5, _tmp.toLong())
        val _tmpDeletedAt: Long? = entity.deletedAt
        if (_tmpDeletedAt == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpDeletedAt)
        }
        val _tmpRemoteId: String? = entity.remoteId
        if (_tmpRemoteId == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpRemoteId)
        }
        statement.bindText(8, entity.syncState)
        val _tmpLastSyncedAt: Long? = entity.lastSyncedAt
        if (_tmpLastSyncedAt == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpLastSyncedAt)
        }
      }
    }
    this.__updateAdapterOfShiftEntity = object : EntityDeleteOrUpdateAdapter<ShiftEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `shifts` SET `id` = ?,`label` = ?,`date` = ?,`created_at` = ?,`is_deleted` = ?,`deleted_at` = ?,`remote_id` = ?,`sync_state` = ?,`last_synced_at` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: ShiftEntity) {
        statement.bindLong(1, entity.id)
        val _tmpLabel: String? = entity.label
        if (_tmpLabel == null) {
          statement.bindNull(2)
        } else {
          statement.bindText(2, _tmpLabel)
        }
        statement.bindLong(3, entity.date)
        statement.bindLong(4, entity.createdAt)
        val _tmp: Int = if (entity.isDeleted) 1 else 0
        statement.bindLong(5, _tmp.toLong())
        val _tmpDeletedAt: Long? = entity.deletedAt
        if (_tmpDeletedAt == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpDeletedAt)
        }
        val _tmpRemoteId: String? = entity.remoteId
        if (_tmpRemoteId == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpRemoteId)
        }
        statement.bindText(8, entity.syncState)
        val _tmpLastSyncedAt: Long? = entity.lastSyncedAt
        if (_tmpLastSyncedAt == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpLastSyncedAt)
        }
        statement.bindLong(10, entity.id)
      }
    }
  }

  public override suspend fun insert(shift: ShiftEntity): Long = performSuspending(__db, false,
      true) { _connection ->
    val _result: Long = __insertAdapterOfShiftEntity.insertAndReturnId(_connection, shift)
    _result
  }

  public override suspend fun update(shift: ShiftEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __updateAdapterOfShiftEntity.handle(_connection, shift)
  }

  public override suspend fun getById(id: Long): ShiftEntity? {
    val _sql: String = "SELECT * FROM shifts WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfLabel: Int = getColumnIndexOrThrow(_stmt, "label")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "is_deleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deleted_at")
        val _columnIndexOfRemoteId: Int = getColumnIndexOrThrow(_stmt, "remote_id")
        val _columnIndexOfSyncState: Int = getColumnIndexOrThrow(_stmt, "sync_state")
        val _columnIndexOfLastSyncedAt: Int = getColumnIndexOrThrow(_stmt, "last_synced_at")
        val _result: ShiftEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpLabel: String?
          if (_stmt.isNull(_columnIndexOfLabel)) {
            _tmpLabel = null
          } else {
            _tmpLabel = _stmt.getText(_columnIndexOfLabel)
          }
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
              ShiftEntity(_tmpId,_tmpLabel,_tmpDate,_tmpCreatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpRemoteId,_tmpSyncState,_tmpLastSyncedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeById(id: Long): Flow<ShiftEntity?> {
    val _sql: String = "SELECT * FROM shifts WHERE id = ?"
    return createFlow(__db, false, arrayOf("shifts")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfLabel: Int = getColumnIndexOrThrow(_stmt, "label")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "is_deleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deleted_at")
        val _columnIndexOfRemoteId: Int = getColumnIndexOrThrow(_stmt, "remote_id")
        val _columnIndexOfSyncState: Int = getColumnIndexOrThrow(_stmt, "sync_state")
        val _columnIndexOfLastSyncedAt: Int = getColumnIndexOrThrow(_stmt, "last_synced_at")
        val _result: ShiftEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpLabel: String?
          if (_stmt.isNull(_columnIndexOfLabel)) {
            _tmpLabel = null
          } else {
            _tmpLabel = _stmt.getText(_columnIndexOfLabel)
          }
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
              ShiftEntity(_tmpId,_tmpLabel,_tmpDate,_tmpCreatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpRemoteId,_tmpSyncState,_tmpLastSyncedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeAll(): Flow<List<ShiftWithCount>> {
    val _sql: String = """
        |
        |        SELECT s.id AS id, s.label AS label, s.date AS date, s.created_at AS created_at,
        |               (SELECT COUNT(*) FROM shift_cases sc
        |                INNER JOIN cases c ON c.id = sc.case_id
        |                WHERE sc.shift_id = s.id AND c.is_deleted = 0) AS case_count
        |        FROM shifts s
        |        WHERE s.is_deleted = 0
        |        ORDER BY s.date DESC
        |        
        """.trimMargin()
    return createFlow(__db, false, arrayOf("shift_cases", "cases", "shifts")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = 0
        val _columnIndexOfLabel: Int = 1
        val _columnIndexOfDate: Int = 2
        val _columnIndexOfCreatedAt: Int = 3
        val _columnIndexOfCaseCount: Int = 4
        val _result: MutableList<ShiftWithCount> = mutableListOf()
        while (_stmt.step()) {
          val _item: ShiftWithCount
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpLabel: String?
          if (_stmt.isNull(_columnIndexOfLabel)) {
            _tmpLabel = null
          } else {
            _tmpLabel = _stmt.getText(_columnIndexOfLabel)
          }
          val _tmpDate: Long
          _tmpDate = _stmt.getLong(_columnIndexOfDate)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpCaseCount: Int
          _tmpCaseCount = _stmt.getLong(_columnIndexOfCaseCount).toInt()
          _item = ShiftWithCount(_tmpId,_tmpLabel,_tmpDate,_tmpCreatedAt,_tmpCaseCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeTrashed(): Flow<List<ShiftEntity>> {
    val _sql: String = "SELECT * FROM shifts WHERE is_deleted = 1 ORDER BY deleted_at DESC"
    return createFlow(__db, false, arrayOf("shifts")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfLabel: Int = getColumnIndexOrThrow(_stmt, "label")
        val _columnIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "is_deleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deleted_at")
        val _columnIndexOfRemoteId: Int = getColumnIndexOrThrow(_stmt, "remote_id")
        val _columnIndexOfSyncState: Int = getColumnIndexOrThrow(_stmt, "sync_state")
        val _columnIndexOfLastSyncedAt: Int = getColumnIndexOrThrow(_stmt, "last_synced_at")
        val _result: MutableList<ShiftEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ShiftEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpLabel: String?
          if (_stmt.isNull(_columnIndexOfLabel)) {
            _tmpLabel = null
          } else {
            _tmpLabel = _stmt.getText(_columnIndexOfLabel)
          }
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
              ShiftEntity(_tmpId,_tmpLabel,_tmpDate,_tmpCreatedAt,_tmpIsDeleted,_tmpDeletedAt,_tmpRemoteId,_tmpSyncState,_tmpLastSyncedAt)
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
        |        UPDATE shifts
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
        |        UPDATE shifts
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
    val _sql: String = "DELETE FROM shifts WHERE is_deleted = 1 AND deleted_at < ?"
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
