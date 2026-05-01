package com.kairos.`data`.db.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.kairos.`data`.db.entities.CaseMediaEntity
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
public class CaseMediaDao_Impl(
  __db: RoomDatabase,
) : CaseMediaDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfCaseMediaEntity: EntityInsertAdapter<CaseMediaEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfCaseMediaEntity = object : EntityInsertAdapter<CaseMediaEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `case_media` (`id`,`case_id`,`file_path`,`media_type`,`duration_ms`,`is_primary`,`created_at`) VALUES (nullif(?, 0),?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: CaseMediaEntity) {
        statement.bindLong(1, entity.id)
        statement.bindLong(2, entity.caseId)
        statement.bindText(3, entity.filePath)
        statement.bindText(4, entity.mediaType)
        val _tmpDurationMs: Long? = entity.durationMs
        if (_tmpDurationMs == null) {
          statement.bindNull(5)
        } else {
          statement.bindLong(5, _tmpDurationMs)
        }
        val _tmp: Int = if (entity.isPrimary) 1 else 0
        statement.bindLong(6, _tmp.toLong())
        statement.bindLong(7, entity.createdAt)
      }
    }
  }

  public override suspend fun insert(item: CaseMediaEntity): Long = performSuspending(__db, false,
      true) { _connection ->
    val _result: Long = __insertAdapterOfCaseMediaEntity.insertAndReturnId(_connection, item)
    _result
  }

  public override suspend fun getById(id: Long): CaseMediaEntity? {
    val _sql: String = "SELECT * FROM case_media WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCaseId: Int = getColumnIndexOrThrow(_stmt, "case_id")
        val _columnIndexOfFilePath: Int = getColumnIndexOrThrow(_stmt, "file_path")
        val _columnIndexOfMediaType: Int = getColumnIndexOrThrow(_stmt, "media_type")
        val _columnIndexOfDurationMs: Int = getColumnIndexOrThrow(_stmt, "duration_ms")
        val _columnIndexOfIsPrimary: Int = getColumnIndexOrThrow(_stmt, "is_primary")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _result: CaseMediaEntity?
        if (_stmt.step()) {
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
          _result =
              CaseMediaEntity(_tmpId,_tmpCaseId,_tmpFilePath,_tmpMediaType,_tmpDurationMs,_tmpIsPrimary,_tmpCreatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeForCase(caseId: Long): Flow<List<CaseMediaEntity>> {
    val _sql: String = "SELECT * FROM case_media WHERE case_id = ? ORDER BY created_at ASC"
    return createFlow(__db, false, arrayOf("case_media")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, caseId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCaseId: Int = getColumnIndexOrThrow(_stmt, "case_id")
        val _columnIndexOfFilePath: Int = getColumnIndexOrThrow(_stmt, "file_path")
        val _columnIndexOfMediaType: Int = getColumnIndexOrThrow(_stmt, "media_type")
        val _columnIndexOfDurationMs: Int = getColumnIndexOrThrow(_stmt, "duration_ms")
        val _columnIndexOfIsPrimary: Int = getColumnIndexOrThrow(_stmt, "is_primary")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _result: MutableList<CaseMediaEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: CaseMediaEntity
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
          _item =
              CaseMediaEntity(_tmpId,_tmpCaseId,_tmpFilePath,_tmpMediaType,_tmpDurationMs,_tmpIsPrimary,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun listForCase(caseId: Long): List<CaseMediaEntity> {
    val _sql: String = "SELECT * FROM case_media WHERE case_id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, caseId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCaseId: Int = getColumnIndexOrThrow(_stmt, "case_id")
        val _columnIndexOfFilePath: Int = getColumnIndexOrThrow(_stmt, "file_path")
        val _columnIndexOfMediaType: Int = getColumnIndexOrThrow(_stmt, "media_type")
        val _columnIndexOfDurationMs: Int = getColumnIndexOrThrow(_stmt, "duration_ms")
        val _columnIndexOfIsPrimary: Int = getColumnIndexOrThrow(_stmt, "is_primary")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "created_at")
        val _result: MutableList<CaseMediaEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: CaseMediaEntity
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
          _item =
              CaseMediaEntity(_tmpId,_tmpCaseId,_tmpFilePath,_tmpMediaType,_tmpDurationMs,_tmpIsPrimary,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteById(id: Long) {
    val _sql: String = "DELETE FROM case_media WHERE id = ?"
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

  public override suspend fun clearPrimary(caseId: Long) {
    val _sql: String = "UPDATE case_media SET is_primary = 0 WHERE case_id = ?"
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

  public override suspend fun setPrimary(mediaId: Long) {
    val _sql: String = "UPDATE case_media SET is_primary = 1 WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, mediaId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
