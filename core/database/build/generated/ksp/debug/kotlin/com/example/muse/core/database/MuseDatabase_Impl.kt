package com.example.muse.core.database

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import javax.`annotation`.processing.Generated
import kotlin.Lazy
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.Set
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf
import kotlin.collections.mutableSetOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class MuseDatabase_Impl : MuseDatabase() {
  private val _trackDao: Lazy<TrackDao> = lazy {
    TrackDao_Impl(this)
  }

  private val _playEventDao: Lazy<PlayEventDao> = lazy {
    PlayEventDao_Impl(this)
  }

  private val _playlistDao: Lazy<PlaylistDao> = lazy {
    PlaylistDao_Impl(this)
  }

  protected override fun createOpenDelegate(): RoomOpenDelegate {
    val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(2, "f1cfe4638b8c8a6558f2c94261a76b4b", "066a2c24de5bf3cb91020f60ccabd5d7") {
      public override fun createAllTables(connection: SQLiteConnection) {
        connection.execSQL("CREATE TABLE IF NOT EXISTS `tracks` (`id` TEXT NOT NULL, `title` TEXT NOT NULL, `artist` TEXT NOT NULL, `album` TEXT NOT NULL, `durationMs` INTEGER NOT NULL, `uri` TEXT NOT NULL, `artworkUri` TEXT, `dateAdded` INTEGER NOT NULL, `stableKey` TEXT NOT NULL, `vectorVersion` INTEGER NOT NULL, `analyzedAt` INTEGER, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_tracks_stableKey` ON `tracks` (`stableKey`)")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `play_events` (`id` TEXT NOT NULL, `sessionId` TEXT NOT NULL, `playbackAttemptId` TEXT NOT NULL, `trackId` TEXT NOT NULL, `playbackItemId` TEXT NOT NULL, `eventType` TEXT NOT NULL, `positionMs` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL, `synced` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `playlists` (`id` TEXT NOT NULL, `name` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS `playlist_tracks` (`playlistId` TEXT NOT NULL, `trackId` TEXT NOT NULL, `position` INTEGER NOT NULL, PRIMARY KEY(`playlistId`, `trackId`))")
        connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
        connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'f1cfe4638b8c8a6558f2c94261a76b4b')")
      }

      public override fun dropAllTables(connection: SQLiteConnection) {
        connection.execSQL("DROP TABLE IF EXISTS `tracks`")
        connection.execSQL("DROP TABLE IF EXISTS `play_events`")
        connection.execSQL("DROP TABLE IF EXISTS `playlists`")
        connection.execSQL("DROP TABLE IF EXISTS `playlist_tracks`")
      }

      public override fun onCreate(connection: SQLiteConnection) {
      }

      public override fun onOpen(connection: SQLiteConnection) {
        internalInitInvalidationTracker(connection)
      }

      public override fun onPreMigrate(connection: SQLiteConnection) {
        dropFtsSyncTriggers(connection)
      }

      public override fun onPostMigrate(connection: SQLiteConnection) {
      }

      public override fun onValidateSchema(connection: SQLiteConnection): RoomOpenDelegate.ValidationResult {
        val _columnsTracks: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsTracks.put("id", TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTracks.put("title", TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTracks.put("artist", TableInfo.Column("artist", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTracks.put("album", TableInfo.Column("album", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTracks.put("durationMs", TableInfo.Column("durationMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTracks.put("uri", TableInfo.Column("uri", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTracks.put("artworkUri", TableInfo.Column("artworkUri", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTracks.put("dateAdded", TableInfo.Column("dateAdded", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTracks.put("stableKey", TableInfo.Column("stableKey", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTracks.put("vectorVersion", TableInfo.Column("vectorVersion", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsTracks.put("analyzedAt", TableInfo.Column("analyzedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysTracks: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesTracks: MutableSet<TableInfo.Index> = mutableSetOf()
        _indicesTracks.add(TableInfo.Index("index_tracks_stableKey", true, listOf("stableKey"), listOf("ASC")))
        val _infoTracks: TableInfo = TableInfo("tracks", _columnsTracks, _foreignKeysTracks, _indicesTracks)
        val _existingTracks: TableInfo = read(connection, "tracks")
        if (!_infoTracks.equals(_existingTracks)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |tracks(com.example.muse.core.database.entity.TrackEntity).
              | Expected:
              |""".trimMargin() + _infoTracks + """
              |
              | Found:
              |""".trimMargin() + _existingTracks)
        }
        val _columnsPlayEvents: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsPlayEvents.put("id", TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlayEvents.put("sessionId", TableInfo.Column("sessionId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlayEvents.put("playbackAttemptId", TableInfo.Column("playbackAttemptId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlayEvents.put("trackId", TableInfo.Column("trackId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlayEvents.put("playbackItemId", TableInfo.Column("playbackItemId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlayEvents.put("eventType", TableInfo.Column("eventType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlayEvents.put("positionMs", TableInfo.Column("positionMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlayEvents.put("timestamp", TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlayEvents.put("synced", TableInfo.Column("synced", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysPlayEvents: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesPlayEvents: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoPlayEvents: TableInfo = TableInfo("play_events", _columnsPlayEvents, _foreignKeysPlayEvents, _indicesPlayEvents)
        val _existingPlayEvents: TableInfo = read(connection, "play_events")
        if (!_infoPlayEvents.equals(_existingPlayEvents)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |play_events(com.example.muse.core.database.entity.PlayEventEntity).
              | Expected:
              |""".trimMargin() + _infoPlayEvents + """
              |
              | Found:
              |""".trimMargin() + _existingPlayEvents)
        }
        val _columnsPlaylists: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsPlaylists.put("id", TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlaylists.put("name", TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlaylists.put("createdAt", TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysPlaylists: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesPlaylists: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoPlaylists: TableInfo = TableInfo("playlists", _columnsPlaylists, _foreignKeysPlaylists, _indicesPlaylists)
        val _existingPlaylists: TableInfo = read(connection, "playlists")
        if (!_infoPlaylists.equals(_existingPlaylists)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |playlists(com.example.muse.core.database.entity.PlaylistEntity).
              | Expected:
              |""".trimMargin() + _infoPlaylists + """
              |
              | Found:
              |""".trimMargin() + _existingPlaylists)
        }
        val _columnsPlaylistTracks: MutableMap<String, TableInfo.Column> = mutableMapOf()
        _columnsPlaylistTracks.put("playlistId", TableInfo.Column("playlistId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlaylistTracks.put("trackId", TableInfo.Column("trackId", "TEXT", true, 2, null, TableInfo.CREATED_FROM_ENTITY))
        _columnsPlaylistTracks.put("position", TableInfo.Column("position", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY))
        val _foreignKeysPlaylistTracks: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
        val _indicesPlaylistTracks: MutableSet<TableInfo.Index> = mutableSetOf()
        val _infoPlaylistTracks: TableInfo = TableInfo("playlist_tracks", _columnsPlaylistTracks, _foreignKeysPlaylistTracks, _indicesPlaylistTracks)
        val _existingPlaylistTracks: TableInfo = read(connection, "playlist_tracks")
        if (!_infoPlaylistTracks.equals(_existingPlaylistTracks)) {
          return RoomOpenDelegate.ValidationResult(false, """
              |playlist_tracks(com.example.muse.core.database.entity.PlaylistTrackEntity).
              | Expected:
              |""".trimMargin() + _infoPlaylistTracks + """
              |
              | Found:
              |""".trimMargin() + _existingPlaylistTracks)
        }
        return RoomOpenDelegate.ValidationResult(true, null)
      }
    }
    return _openDelegate
  }

  protected override fun createInvalidationTracker(): InvalidationTracker {
    val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
    val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
    return InvalidationTracker(this, _shadowTablesMap, _viewTables, "tracks", "play_events", "playlists", "playlist_tracks")
  }

  public override fun clearAllTables() {
    super.performClear(false, "tracks", "play_events", "playlists", "playlist_tracks")
  }

  protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
    val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
    _typeConvertersMap.put(TrackDao::class, TrackDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(PlayEventDao::class, PlayEventDao_Impl.getRequiredConverters())
    _typeConvertersMap.put(PlaylistDao::class, PlaylistDao_Impl.getRequiredConverters())
    return _typeConvertersMap
  }

  public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
    val _autoMigrationSpecsSet: MutableSet<KClass<out AutoMigrationSpec>> = mutableSetOf()
    return _autoMigrationSpecsSet
  }

  public override fun createAutoMigrations(autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>): List<Migration> {
    val _autoMigrations: MutableList<Migration> = mutableListOf()
    return _autoMigrations
  }

  public override fun trackDao(): TrackDao = _trackDao.value

  public override fun playEventDao(): PlayEventDao = _playEventDao.value

  public override fun playlistDao(): PlaylistDao = _playlistDao.value
}
