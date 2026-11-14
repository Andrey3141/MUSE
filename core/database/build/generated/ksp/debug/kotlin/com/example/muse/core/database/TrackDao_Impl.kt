package com.example.muse.core.database

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.example.muse.core.database.entity.TrackEntity
import javax.`annotation`.processing.Generated
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
public class TrackDao_Impl(
  __db: RoomDatabase,
) : TrackDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfTrackEntity: EntityInsertAdapter<TrackEntity>

  private val __insertAdapterOfTrackEntity_1: EntityInsertAdapter<TrackEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfTrackEntity = object : EntityInsertAdapter<TrackEntity>() {
      protected override fun createQuery(): String = "INSERT OR IGNORE INTO `tracks` (`id`,`title`,`artist`,`album`,`durationMs`,`uri`,`artworkUri`,`dateAdded`,`stableKey`,`vectorVersion`,`analyzedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: TrackEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.title)
        statement.bindText(3, entity.artist)
        statement.bindText(4, entity.album)
        statement.bindLong(5, entity.durationMs)
        statement.bindText(6, entity.uri)
        val _tmpArtworkUri: String? = entity.artworkUri
        if (_tmpArtworkUri == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpArtworkUri)
        }
        statement.bindLong(8, entity.dateAdded)
        statement.bindText(9, entity.stableKey)
        statement.bindLong(10, entity.vectorVersion.toLong())
        val _tmpAnalyzedAt: Long? = entity.analyzedAt
        if (_tmpAnalyzedAt == null) {
          statement.bindNull(11)
        } else {
          statement.bindLong(11, _tmpAnalyzedAt)
        }
      }
    }
    this.__insertAdapterOfTrackEntity_1 = object : EntityInsertAdapter<TrackEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `tracks` (`id`,`title`,`artist`,`album`,`durationMs`,`uri`,`artworkUri`,`dateAdded`,`stableKey`,`vectorVersion`,`analyzedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: TrackEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.title)
        statement.bindText(3, entity.artist)
        statement.bindText(4, entity.album)
        statement.bindLong(5, entity.durationMs)
        statement.bindText(6, entity.uri)
        val _tmpArtworkUri: String? = entity.artworkUri
        if (_tmpArtworkUri == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpArtworkUri)
        }
        statement.bindLong(8, entity.dateAdded)
        statement.bindText(9, entity.stableKey)
        statement.bindLong(10, entity.vectorVersion.toLong())
        val _tmpAnalyzedAt: Long? = entity.analyzedAt
        if (_tmpAnalyzedAt == null) {
          statement.bindNull(11)
        } else {
          statement.bindLong(11, _tmpAnalyzedAt)
        }
      }
    }
  }

  public override suspend fun insertIgnore(track: TrackEntity): Long = performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfTrackEntity.insertAndReturnId(_connection, track)
    _result
  }

  public override suspend fun upsert(track: TrackEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfTrackEntity_1.insert(_connection, track)
  }

  public override fun observeAll(): Flow<List<TrackEntity>> {
    val _sql: String = "SELECT * FROM tracks ORDER BY dateAdded DESC"
    return createFlow(__db, false, arrayOf("tracks")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfArtist: Int = getColumnIndexOrThrow(_stmt, "artist")
        val _columnIndexOfAlbum: Int = getColumnIndexOrThrow(_stmt, "album")
        val _columnIndexOfDurationMs: Int = getColumnIndexOrThrow(_stmt, "durationMs")
        val _columnIndexOfUri: Int = getColumnIndexOrThrow(_stmt, "uri")
        val _columnIndexOfArtworkUri: Int = getColumnIndexOrThrow(_stmt, "artworkUri")
        val _columnIndexOfDateAdded: Int = getColumnIndexOrThrow(_stmt, "dateAdded")
        val _columnIndexOfStableKey: Int = getColumnIndexOrThrow(_stmt, "stableKey")
        val _columnIndexOfVectorVersion: Int = getColumnIndexOrThrow(_stmt, "vectorVersion")
        val _columnIndexOfAnalyzedAt: Int = getColumnIndexOrThrow(_stmt, "analyzedAt")
        val _result: MutableList<TrackEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TrackEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpArtist: String
          _tmpArtist = _stmt.getText(_columnIndexOfArtist)
          val _tmpAlbum: String
          _tmpAlbum = _stmt.getText(_columnIndexOfAlbum)
          val _tmpDurationMs: Long
          _tmpDurationMs = _stmt.getLong(_columnIndexOfDurationMs)
          val _tmpUri: String
          _tmpUri = _stmt.getText(_columnIndexOfUri)
          val _tmpArtworkUri: String?
          if (_stmt.isNull(_columnIndexOfArtworkUri)) {
            _tmpArtworkUri = null
          } else {
            _tmpArtworkUri = _stmt.getText(_columnIndexOfArtworkUri)
          }
          val _tmpDateAdded: Long
          _tmpDateAdded = _stmt.getLong(_columnIndexOfDateAdded)
          val _tmpStableKey: String
          _tmpStableKey = _stmt.getText(_columnIndexOfStableKey)
          val _tmpVectorVersion: Int
          _tmpVectorVersion = _stmt.getLong(_columnIndexOfVectorVersion).toInt()
          val _tmpAnalyzedAt: Long?
          if (_stmt.isNull(_columnIndexOfAnalyzedAt)) {
            _tmpAnalyzedAt = null
          } else {
            _tmpAnalyzedAt = _stmt.getLong(_columnIndexOfAnalyzedAt)
          }
          _item = TrackEntity(_tmpId,_tmpTitle,_tmpArtist,_tmpAlbum,_tmpDurationMs,_tmpUri,_tmpArtworkUri,_tmpDateAdded,_tmpStableKey,_tmpVectorVersion,_tmpAnalyzedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun findByStableKey(stableKey: String): TrackEntity? {
    val _sql: String = "SELECT * FROM tracks WHERE stableKey = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, stableKey)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfArtist: Int = getColumnIndexOrThrow(_stmt, "artist")
        val _columnIndexOfAlbum: Int = getColumnIndexOrThrow(_stmt, "album")
        val _columnIndexOfDurationMs: Int = getColumnIndexOrThrow(_stmt, "durationMs")
        val _columnIndexOfUri: Int = getColumnIndexOrThrow(_stmt, "uri")
        val _columnIndexOfArtworkUri: Int = getColumnIndexOrThrow(_stmt, "artworkUri")
        val _columnIndexOfDateAdded: Int = getColumnIndexOrThrow(_stmt, "dateAdded")
        val _columnIndexOfStableKey: Int = getColumnIndexOrThrow(_stmt, "stableKey")
        val _columnIndexOfVectorVersion: Int = getColumnIndexOrThrow(_stmt, "vectorVersion")
        val _columnIndexOfAnalyzedAt: Int = getColumnIndexOrThrow(_stmt, "analyzedAt")
        val _result: TrackEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpArtist: String
          _tmpArtist = _stmt.getText(_columnIndexOfArtist)
          val _tmpAlbum: String
          _tmpAlbum = _stmt.getText(_columnIndexOfAlbum)
          val _tmpDurationMs: Long
          _tmpDurationMs = _stmt.getLong(_columnIndexOfDurationMs)
          val _tmpUri: String
          _tmpUri = _stmt.getText(_columnIndexOfUri)
          val _tmpArtworkUri: String?
          if (_stmt.isNull(_columnIndexOfArtworkUri)) {
            _tmpArtworkUri = null
          } else {
            _tmpArtworkUri = _stmt.getText(_columnIndexOfArtworkUri)
          }
          val _tmpDateAdded: Long
          _tmpDateAdded = _stmt.getLong(_columnIndexOfDateAdded)
          val _tmpStableKey: String
          _tmpStableKey = _stmt.getText(_columnIndexOfStableKey)
          val _tmpVectorVersion: Int
          _tmpVectorVersion = _stmt.getLong(_columnIndexOfVectorVersion).toInt()
          val _tmpAnalyzedAt: Long?
          if (_stmt.isNull(_columnIndexOfAnalyzedAt)) {
            _tmpAnalyzedAt = null
          } else {
            _tmpAnalyzedAt = _stmt.getLong(_columnIndexOfAnalyzedAt)
          }
          _result = TrackEntity(_tmpId,_tmpTitle,_tmpArtist,_tmpAlbum,_tmpDurationMs,_tmpUri,_tmpArtworkUri,_tmpDateAdded,_tmpStableKey,_tmpVectorVersion,_tmpAnalyzedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun count(): Int {
    val _sql: String = "SELECT COUNT(*) FROM tracks"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
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
