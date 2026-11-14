package com.example.muse.core.database

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.example.muse.core.database.entity.PlaylistEntity
import com.example.muse.core.database.entity.PlaylistTrackEntity
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

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class PlaylistDao_Impl(
  __db: RoomDatabase,
) : PlaylistDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfPlaylistEntity: EntityInsertAdapter<PlaylistEntity>

  private val __insertAdapterOfPlaylistTrackEntity: EntityInsertAdapter<PlaylistTrackEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfPlaylistEntity = object : EntityInsertAdapter<PlaylistEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `playlists` (`id`,`name`,`createdAt`) VALUES (?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: PlaylistEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.name)
        statement.bindLong(3, entity.createdAt)
      }
    }
    this.__insertAdapterOfPlaylistTrackEntity = object : EntityInsertAdapter<PlaylistTrackEntity>() {
      protected override fun createQuery(): String = "INSERT OR REPLACE INTO `playlist_tracks` (`playlistId`,`trackId`,`position`) VALUES (?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: PlaylistTrackEntity) {
        statement.bindText(1, entity.playlistId)
        statement.bindText(2, entity.trackId)
        statement.bindLong(3, entity.position.toLong())
      }
    }
  }

  public override suspend fun upsert(playlist: PlaylistEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfPlaylistEntity.insert(_connection, playlist)
  }

  public override suspend fun addTrack(entry: PlaylistTrackEntity): Unit = performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfPlaylistTrackEntity.insert(_connection, entry)
  }

  public override suspend fun getAll(): List<PlaylistEntity> {
    val _sql: String = "SELECT * FROM playlists ORDER BY createdAt DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<PlaylistEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: PlaylistEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item = PlaylistEntity(_tmpId,_tmpName,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getTracks(playlistId: String): List<PlaylistTrackEntity> {
    val _sql: String = "SELECT * FROM playlist_tracks WHERE playlistId = ? ORDER BY position ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, playlistId)
        val _columnIndexOfPlaylistId: Int = getColumnIndexOrThrow(_stmt, "playlistId")
        val _columnIndexOfTrackId: Int = getColumnIndexOrThrow(_stmt, "trackId")
        val _columnIndexOfPosition: Int = getColumnIndexOrThrow(_stmt, "position")
        val _result: MutableList<PlaylistTrackEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: PlaylistTrackEntity
          val _tmpPlaylistId: String
          _tmpPlaylistId = _stmt.getText(_columnIndexOfPlaylistId)
          val _tmpTrackId: String
          _tmpTrackId = _stmt.getText(_columnIndexOfTrackId)
          val _tmpPosition: Int
          _tmpPosition = _stmt.getLong(_columnIndexOfPosition).toInt()
          _item = PlaylistTrackEntity(_tmpPlaylistId,_tmpTrackId,_tmpPosition)
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
