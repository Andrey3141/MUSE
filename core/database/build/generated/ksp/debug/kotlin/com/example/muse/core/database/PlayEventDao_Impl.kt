package com.example.muse.core.database

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.example.muse.core.database.entity.PlayEventEntity
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
public class PlayEventDao_Impl(
  __db: RoomDatabase,
) : PlayEventDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfPlayEventEntity: EntityInsertAdapter<PlayEventEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfPlayEventEntity = object : EntityInsertAdapter<PlayEventEntity>() {
      protected override fun createQuery(): String = "INSERT OR ABORT INTO `play_events` (`id`,`sessionId`,`playbackAttemptId`,`trackId`,`playbackItemId`,`eventType`,`positionMs`,`timestamp`,`synced`) VALUES (?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: PlayEventEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.sessionId)
        statement.bindText(3, entity.playbackAttemptId)
        statement.bindText(4, entity.trackId)
        statement.bindText(5, entity.playbackItemId)
        statement.bindText(6, entity.eventType)
        statement.bindLong(7, entity.positionMs)
        statement.bindLong(8, entity.timestamp)
        val _tmp: Int = if (entity.synced) 1 else 0
        statement.bindLong(9, _tmp.toLong())
      }
    }
  }

  public override suspend fun insert(event: PlayEventEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfPlayEventEntity.insert(_connection, event)
  }

  public override suspend fun pendingSync(): List<PlayEventEntity> {
    val _sql: String = "SELECT * FROM play_events WHERE synced = 0 ORDER BY timestamp ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSessionId: Int = getColumnIndexOrThrow(_stmt, "sessionId")
        val _columnIndexOfPlaybackAttemptId: Int = getColumnIndexOrThrow(_stmt, "playbackAttemptId")
        val _columnIndexOfTrackId: Int = getColumnIndexOrThrow(_stmt, "trackId")
        val _columnIndexOfPlaybackItemId: Int = getColumnIndexOrThrow(_stmt, "playbackItemId")
        val _columnIndexOfEventType: Int = getColumnIndexOrThrow(_stmt, "eventType")
        val _columnIndexOfPositionMs: Int = getColumnIndexOrThrow(_stmt, "positionMs")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfSynced: Int = getColumnIndexOrThrow(_stmt, "synced")
        val _result: MutableList<PlayEventEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: PlayEventEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpSessionId: String
          _tmpSessionId = _stmt.getText(_columnIndexOfSessionId)
          val _tmpPlaybackAttemptId: String
          _tmpPlaybackAttemptId = _stmt.getText(_columnIndexOfPlaybackAttemptId)
          val _tmpTrackId: String
          _tmpTrackId = _stmt.getText(_columnIndexOfTrackId)
          val _tmpPlaybackItemId: String
          _tmpPlaybackItemId = _stmt.getText(_columnIndexOfPlaybackItemId)
          val _tmpEventType: String
          _tmpEventType = _stmt.getText(_columnIndexOfEventType)
          val _tmpPositionMs: Long
          _tmpPositionMs = _stmt.getLong(_columnIndexOfPositionMs)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpSynced: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfSynced).toInt()
          _tmpSynced = _tmp != 0
          _item = PlayEventEntity(_tmpId,_tmpSessionId,_tmpPlaybackAttemptId,_tmpTrackId,_tmpPlaybackItemId,_tmpEventType,_tmpPositionMs,_tmpTimestamp,_tmpSynced)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByTrack(trackId: String): Flow<List<PlayEventEntity>> {
    val _sql: String = "SELECT * FROM play_events WHERE trackId = ? ORDER BY timestamp DESC"
    return createFlow(__db, false, arrayOf("play_events")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, trackId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfSessionId: Int = getColumnIndexOrThrow(_stmt, "sessionId")
        val _columnIndexOfPlaybackAttemptId: Int = getColumnIndexOrThrow(_stmt, "playbackAttemptId")
        val _columnIndexOfTrackId: Int = getColumnIndexOrThrow(_stmt, "trackId")
        val _columnIndexOfPlaybackItemId: Int = getColumnIndexOrThrow(_stmt, "playbackItemId")
        val _columnIndexOfEventType: Int = getColumnIndexOrThrow(_stmt, "eventType")
        val _columnIndexOfPositionMs: Int = getColumnIndexOrThrow(_stmt, "positionMs")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfSynced: Int = getColumnIndexOrThrow(_stmt, "synced")
        val _result: MutableList<PlayEventEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: PlayEventEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpSessionId: String
          _tmpSessionId = _stmt.getText(_columnIndexOfSessionId)
          val _tmpPlaybackAttemptId: String
          _tmpPlaybackAttemptId = _stmt.getText(_columnIndexOfPlaybackAttemptId)
          val _tmpTrackId: String
          _tmpTrackId = _stmt.getText(_columnIndexOfTrackId)
          val _tmpPlaybackItemId: String
          _tmpPlaybackItemId = _stmt.getText(_columnIndexOfPlaybackItemId)
          val _tmpEventType: String
          _tmpEventType = _stmt.getText(_columnIndexOfEventType)
          val _tmpPositionMs: Long
          _tmpPositionMs = _stmt.getLong(_columnIndexOfPositionMs)
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpSynced: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfSynced).toInt()
          _tmpSynced = _tmp != 0
          _item = PlayEventEntity(_tmpId,_tmpSessionId,_tmpPlaybackAttemptId,_tmpTrackId,_tmpPlaybackItemId,_tmpEventType,_tmpPositionMs,_tmpTimestamp,_tmpSynced)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markSynced(ids: List<String>) {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("UPDATE play_events SET synced = 1 WHERE id IN (")
    val _inputSize: Int = ids.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        for (_item: String in ids) {
          _stmt.bindText(_argIndex, _item)
          _argIndex++
        }
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
