package com.example.muse.core.database

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.getTotalChangedRows
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.example.muse.core.database.entity.TrackEntity
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
public class TrackDao_Impl(
  __db: RoomDatabase,
) : TrackDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfTrackEntity: EntityInsertAdapter<TrackEntity>

  private val __insertAdapterOfTrackEntity_1: EntityInsertAdapter<TrackEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfTrackEntity = object : EntityInsertAdapter<TrackEntity>() {
      protected override fun createQuery(): String = "INSERT OR IGNORE INTO `tracks` (`id`,`title`,`artist`,`album`,`durationMs`,`uri`,`artworkUri`,`dateAdded`,`stableKey`,`vectorVersion`,`analyzedAt`,`museCopyPath`,`copyStatus`,`originalAvailable`,`nextAttemptAt`,`claimEpoch`,`claimedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

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
        val _tmpMuseCopyPath: String? = entity.museCopyPath
        if (_tmpMuseCopyPath == null) {
          statement.bindNull(12)
        } else {
          statement.bindText(12, _tmpMuseCopyPath)
        }
        statement.bindText(13, entity.copyStatus)
        val _tmp: Int = if (entity.originalAvailable) 1 else 0
        statement.bindLong(14, _tmp.toLong())
        statement.bindLong(15, entity.nextAttemptAt)
        statement.bindLong(16, entity.claimEpoch)
        statement.bindLong(17, entity.claimedAt)
      }
    }
    this.__insertAdapterOfTrackEntity_1 = object : EntityInsertAdapter<TrackEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `tracks` (`id`,`title`,`artist`,`album`,`durationMs`,`uri`,`artworkUri`,`dateAdded`,`stableKey`,`vectorVersion`,`analyzedAt`,`museCopyPath`,`copyStatus`,`originalAvailable`,`nextAttemptAt`,`claimEpoch`,`claimedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

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
        val _tmpMuseCopyPath: String? = entity.museCopyPath
        if (_tmpMuseCopyPath == null) {
          statement.bindNull(12)
        } else {
          statement.bindText(12, _tmpMuseCopyPath)
        }
        statement.bindText(13, entity.copyStatus)
        val _tmp: Int = if (entity.originalAvailable) 1 else 0
        statement.bindLong(14, _tmp.toLong())
        statement.bindLong(15, entity.nextAttemptAt)
        statement.bindLong(16, entity.claimEpoch)
        statement.bindLong(17, entity.claimedAt)
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
        val _columnIndexOfMuseCopyPath: Int = getColumnIndexOrThrow(_stmt, "museCopyPath")
        val _columnIndexOfCopyStatus: Int = getColumnIndexOrThrow(_stmt, "copyStatus")
        val _columnIndexOfOriginalAvailable: Int = getColumnIndexOrThrow(_stmt, "originalAvailable")
        val _columnIndexOfNextAttemptAt: Int = getColumnIndexOrThrow(_stmt, "nextAttemptAt")
        val _columnIndexOfClaimEpoch: Int = getColumnIndexOrThrow(_stmt, "claimEpoch")
        val _columnIndexOfClaimedAt: Int = getColumnIndexOrThrow(_stmt, "claimedAt")
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
          val _tmpMuseCopyPath: String?
          if (_stmt.isNull(_columnIndexOfMuseCopyPath)) {
            _tmpMuseCopyPath = null
          } else {
            _tmpMuseCopyPath = _stmt.getText(_columnIndexOfMuseCopyPath)
          }
          val _tmpCopyStatus: String
          _tmpCopyStatus = _stmt.getText(_columnIndexOfCopyStatus)
          val _tmpOriginalAvailable: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfOriginalAvailable).toInt()
          _tmpOriginalAvailable = _tmp != 0
          val _tmpNextAttemptAt: Long
          _tmpNextAttemptAt = _stmt.getLong(_columnIndexOfNextAttemptAt)
          val _tmpClaimEpoch: Long
          _tmpClaimEpoch = _stmt.getLong(_columnIndexOfClaimEpoch)
          val _tmpClaimedAt: Long
          _tmpClaimedAt = _stmt.getLong(_columnIndexOfClaimedAt)
          _item = TrackEntity(_tmpId,_tmpTitle,_tmpArtist,_tmpAlbum,_tmpDurationMs,_tmpUri,_tmpArtworkUri,_tmpDateAdded,_tmpStableKey,_tmpVectorVersion,_tmpAnalyzedAt,_tmpMuseCopyPath,_tmpCopyStatus,_tmpOriginalAvailable,_tmpNextAttemptAt,_tmpClaimEpoch,_tmpClaimedAt)
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
        val _columnIndexOfMuseCopyPath: Int = getColumnIndexOrThrow(_stmt, "museCopyPath")
        val _columnIndexOfCopyStatus: Int = getColumnIndexOrThrow(_stmt, "copyStatus")
        val _columnIndexOfOriginalAvailable: Int = getColumnIndexOrThrow(_stmt, "originalAvailable")
        val _columnIndexOfNextAttemptAt: Int = getColumnIndexOrThrow(_stmt, "nextAttemptAt")
        val _columnIndexOfClaimEpoch: Int = getColumnIndexOrThrow(_stmt, "claimEpoch")
        val _columnIndexOfClaimedAt: Int = getColumnIndexOrThrow(_stmt, "claimedAt")
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
          val _tmpMuseCopyPath: String?
          if (_stmt.isNull(_columnIndexOfMuseCopyPath)) {
            _tmpMuseCopyPath = null
          } else {
            _tmpMuseCopyPath = _stmt.getText(_columnIndexOfMuseCopyPath)
          }
          val _tmpCopyStatus: String
          _tmpCopyStatus = _stmt.getText(_columnIndexOfCopyStatus)
          val _tmpOriginalAvailable: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfOriginalAvailable).toInt()
          _tmpOriginalAvailable = _tmp != 0
          val _tmpNextAttemptAt: Long
          _tmpNextAttemptAt = _stmt.getLong(_columnIndexOfNextAttemptAt)
          val _tmpClaimEpoch: Long
          _tmpClaimEpoch = _stmt.getLong(_columnIndexOfClaimEpoch)
          val _tmpClaimedAt: Long
          _tmpClaimedAt = _stmt.getLong(_columnIndexOfClaimedAt)
          _result = TrackEntity(_tmpId,_tmpTitle,_tmpArtist,_tmpAlbum,_tmpDurationMs,_tmpUri,_tmpArtworkUri,_tmpDateAdded,_tmpStableKey,_tmpVectorVersion,_tmpAnalyzedAt,_tmpMuseCopyPath,_tmpCopyStatus,_tmpOriginalAvailable,_tmpNextAttemptAt,_tmpClaimEpoch,_tmpClaimedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun findById(id: String): TrackEntity? {
    val _sql: String = "SELECT * FROM tracks WHERE id = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
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
        val _columnIndexOfMuseCopyPath: Int = getColumnIndexOrThrow(_stmt, "museCopyPath")
        val _columnIndexOfCopyStatus: Int = getColumnIndexOrThrow(_stmt, "copyStatus")
        val _columnIndexOfOriginalAvailable: Int = getColumnIndexOrThrow(_stmt, "originalAvailable")
        val _columnIndexOfNextAttemptAt: Int = getColumnIndexOrThrow(_stmt, "nextAttemptAt")
        val _columnIndexOfClaimEpoch: Int = getColumnIndexOrThrow(_stmt, "claimEpoch")
        val _columnIndexOfClaimedAt: Int = getColumnIndexOrThrow(_stmt, "claimedAt")
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
          val _tmpMuseCopyPath: String?
          if (_stmt.isNull(_columnIndexOfMuseCopyPath)) {
            _tmpMuseCopyPath = null
          } else {
            _tmpMuseCopyPath = _stmt.getText(_columnIndexOfMuseCopyPath)
          }
          val _tmpCopyStatus: String
          _tmpCopyStatus = _stmt.getText(_columnIndexOfCopyStatus)
          val _tmpOriginalAvailable: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfOriginalAvailable).toInt()
          _tmpOriginalAvailable = _tmp != 0
          val _tmpNextAttemptAt: Long
          _tmpNextAttemptAt = _stmt.getLong(_columnIndexOfNextAttemptAt)
          val _tmpClaimEpoch: Long
          _tmpClaimEpoch = _stmt.getLong(_columnIndexOfClaimEpoch)
          val _tmpClaimedAt: Long
          _tmpClaimedAt = _stmt.getLong(_columnIndexOfClaimedAt)
          _result = TrackEntity(_tmpId,_tmpTitle,_tmpArtist,_tmpAlbum,_tmpDurationMs,_tmpUri,_tmpArtworkUri,_tmpDateAdded,_tmpStableKey,_tmpVectorVersion,_tmpAnalyzedAt,_tmpMuseCopyPath,_tmpCopyStatus,_tmpOriginalAvailable,_tmpNextAttemptAt,_tmpClaimEpoch,_tmpClaimedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAll(): List<TrackEntity> {
    val _sql: String = "SELECT * FROM tracks"
    return performSuspending(__db, true, false) { _connection ->
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
        val _columnIndexOfMuseCopyPath: Int = getColumnIndexOrThrow(_stmt, "museCopyPath")
        val _columnIndexOfCopyStatus: Int = getColumnIndexOrThrow(_stmt, "copyStatus")
        val _columnIndexOfOriginalAvailable: Int = getColumnIndexOrThrow(_stmt, "originalAvailable")
        val _columnIndexOfNextAttemptAt: Int = getColumnIndexOrThrow(_stmt, "nextAttemptAt")
        val _columnIndexOfClaimEpoch: Int = getColumnIndexOrThrow(_stmt, "claimEpoch")
        val _columnIndexOfClaimedAt: Int = getColumnIndexOrThrow(_stmt, "claimedAt")
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
          val _tmpMuseCopyPath: String?
          if (_stmt.isNull(_columnIndexOfMuseCopyPath)) {
            _tmpMuseCopyPath = null
          } else {
            _tmpMuseCopyPath = _stmt.getText(_columnIndexOfMuseCopyPath)
          }
          val _tmpCopyStatus: String
          _tmpCopyStatus = _stmt.getText(_columnIndexOfCopyStatus)
          val _tmpOriginalAvailable: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfOriginalAvailable).toInt()
          _tmpOriginalAvailable = _tmp != 0
          val _tmpNextAttemptAt: Long
          _tmpNextAttemptAt = _stmt.getLong(_columnIndexOfNextAttemptAt)
          val _tmpClaimEpoch: Long
          _tmpClaimEpoch = _stmt.getLong(_columnIndexOfClaimEpoch)
          val _tmpClaimedAt: Long
          _tmpClaimedAt = _stmt.getLong(_columnIndexOfClaimedAt)
          _item = TrackEntity(_tmpId,_tmpTitle,_tmpArtist,_tmpAlbum,_tmpDurationMs,_tmpUri,_tmpArtworkUri,_tmpDateAdded,_tmpStableKey,_tmpVectorVersion,_tmpAnalyzedAt,_tmpMuseCopyPath,_tmpCopyStatus,_tmpOriginalAvailable,_tmpNextAttemptAt,_tmpClaimEpoch,_tmpClaimedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getCurrentClaimEpoch(trackId: String): Long? {
    val _sql: String = "SELECT claimEpoch FROM tracks WHERE id = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, trackId)
        val _result: Long?
        if (_stmt.step()) {
          if (_stmt.isNull(0)) {
            _result = null
          } else {
            _result = _stmt.getLong(0)
          }
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun findNextPendingCopy(
    now: Long,
    permanentThreshold: Long,
    staleThreshold: Long,
    excludeIds: List<String>,
  ): TrackEntity? {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("        SELECT * FROM tracks")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("        WHERE originalAvailable = 1")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("          AND copyStatus != 'IN_PROGRESS'")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("          AND (copyStatus != 'COMPLETED' AND copyStatus != 'COMPLETED_UNVERIFIED' OR museCopyPath IS NULL)")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("          AND nextAttemptAt < ")
    _stringBuilder.append("?")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("          AND nextAttemptAt <= ")
    _stringBuilder.append("?")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("          AND (claimEpoch = 0 OR claimedAt < ")
    _stringBuilder.append("?")
    _stringBuilder.append(")")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("          AND id NOT IN (")
    val _inputSize: Int = excludeIds.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("        ORDER BY dateAdded DESC")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("        LIMIT 1")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("    ")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, permanentThreshold)
        _argIndex = 2
        _stmt.bindLong(_argIndex, now)
        _argIndex = 3
        _stmt.bindLong(_argIndex, staleThreshold)
        _argIndex = 4
        for (_item: String in excludeIds) {
          _stmt.bindText(_argIndex, _item)
          _argIndex++
        }
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
        val _columnIndexOfMuseCopyPath: Int = getColumnIndexOrThrow(_stmt, "museCopyPath")
        val _columnIndexOfCopyStatus: Int = getColumnIndexOrThrow(_stmt, "copyStatus")
        val _columnIndexOfOriginalAvailable: Int = getColumnIndexOrThrow(_stmt, "originalAvailable")
        val _columnIndexOfNextAttemptAt: Int = getColumnIndexOrThrow(_stmt, "nextAttemptAt")
        val _columnIndexOfClaimEpoch: Int = getColumnIndexOrThrow(_stmt, "claimEpoch")
        val _columnIndexOfClaimedAt: Int = getColumnIndexOrThrow(_stmt, "claimedAt")
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
          val _tmpMuseCopyPath: String?
          if (_stmt.isNull(_columnIndexOfMuseCopyPath)) {
            _tmpMuseCopyPath = null
          } else {
            _tmpMuseCopyPath = _stmt.getText(_columnIndexOfMuseCopyPath)
          }
          val _tmpCopyStatus: String
          _tmpCopyStatus = _stmt.getText(_columnIndexOfCopyStatus)
          val _tmpOriginalAvailable: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfOriginalAvailable).toInt()
          _tmpOriginalAvailable = _tmp != 0
          val _tmpNextAttemptAt: Long
          _tmpNextAttemptAt = _stmt.getLong(_columnIndexOfNextAttemptAt)
          val _tmpClaimEpoch: Long
          _tmpClaimEpoch = _stmt.getLong(_columnIndexOfClaimEpoch)
          val _tmpClaimedAt: Long
          _tmpClaimedAt = _stmt.getLong(_columnIndexOfClaimedAt)
          _result = TrackEntity(_tmpId,_tmpTitle,_tmpArtist,_tmpAlbum,_tmpDurationMs,_tmpUri,_tmpArtworkUri,_tmpDateAdded,_tmpStableKey,_tmpVectorVersion,_tmpAnalyzedAt,_tmpMuseCopyPath,_tmpCopyStatus,_tmpOriginalAvailable,_tmpNextAttemptAt,_tmpClaimEpoch,_tmpClaimedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun findNearestRetryableAttemptAt(now: Long, permanentThreshold: Long): Long? {
    val _sql: String = """
        |
        |        SELECT MIN(nextAttemptAt) FROM tracks
        |        WHERE originalAvailable = 1
        |          AND copyStatus = 'FAILED'
        |          AND nextAttemptAt > ?
        |          AND nextAttemptAt < ?
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, now)
        _argIndex = 2
        _stmt.bindLong(_argIndex, permanentThreshold)
        val _result: Long?
        if (_stmt.step()) {
          val _tmp: Long?
          if (_stmt.isNull(0)) {
            _tmp = null
          } else {
            _tmp = _stmt.getLong(0)
          }
          _result = _tmp
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

  public override suspend fun tryIncrementClaimEpoch(
    trackId: String,
    now: Long,
    staleThreshold: Long,
  ): Int {
    val _sql: String = """
        |
        |        UPDATE tracks
        |        SET claimEpoch = claimEpoch + 1, claimedAt = ?
        |        WHERE id = ?
        |          AND (claimEpoch = 0 OR claimedAt < ?)
        |    
        """.trimMargin()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, now)
        _argIndex = 2
        _stmt.bindText(_argIndex, trackId)
        _argIndex = 3
        _stmt.bindLong(_argIndex, staleThreshold)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateCopyStateIfOwner(
    trackId: String,
    epoch: Long,
    status: String,
    path: String?,
    nextAttemptAt: Long,
  ): Int {
    val _sql: String = """
        |
        |        UPDATE tracks
        |        SET copyStatus = ?, museCopyPath = ?, nextAttemptAt = ?
        |        WHERE id = ? AND claimEpoch = ?
        |    
        """.trimMargin()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        _argIndex = 2
        if (path == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, path)
        }
        _argIndex = 3
        _stmt.bindLong(_argIndex, nextAttemptAt)
        _argIndex = 4
        _stmt.bindText(_argIndex, trackId)
        _argIndex = 5
        _stmt.bindLong(_argIndex, epoch)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun releaseClaim(trackId: String, epoch: Long): Int {
    val _sql: String = "UPDATE tracks SET claimEpoch = 0, claimedAt = 0 WHERE id = ? AND claimEpoch = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, trackId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, epoch)
        _stmt.step()
        getTotalChangedRows(_connection)
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun setOriginalAvailable(id: String, available: Boolean) {
    val _sql: String = "UPDATE tracks SET originalAvailable = ? WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        val _tmp: Int = if (available) 1 else 0
        _stmt.bindLong(_argIndex, _tmp.toLong())
        _argIndex = 2
        _stmt.bindText(_argIndex, id)
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
