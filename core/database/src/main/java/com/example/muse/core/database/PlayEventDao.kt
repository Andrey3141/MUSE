package com.example.muse.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.muse.core.database.entity.PlayEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayEventDao {
    @Insert
    suspend fun insert(event: PlayEventEntity)

    @Query("SELECT * FROM play_events WHERE synced = 0 ORDER BY timestamp ASC")
    suspend fun pendingSync(): List<PlayEventEntity>

    @Query("UPDATE play_events SET synced = 1 WHERE id IN (:ids)")
    suspend fun markSynced(ids: List<String>)

    @Query("SELECT * FROM play_events WHERE trackId = :trackId ORDER BY timestamp DESC")
    fun observeByTrack(trackId: String): Flow<List<PlayEventEntity>>
}