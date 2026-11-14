package com.example.muse.core.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.muse.core.database.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {
    @Query("SELECT * FROM tracks ORDER BY dateAdded DESC")
    fun observeAll(): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE stableKey = :stableKey LIMIT 1")
    suspend fun findByStableKey(stableKey: String): TrackEntity?

    @Query("SELECT * FROM tracks WHERE id = :id LIMIT 1")
    suspend fun findById(id: String): TrackEntity?

    @Query("SELECT * FROM tracks")
    suspend fun getAll(): List<TrackEntity>

    /**
     * Атомарно пытается захватить trackId с новым fencing epoch.
     * Успех: claimEpoch == 0 ИЛИ claimedAt < staleThreshold.
     * Возвращает новый epoch при успехе, 0 при неудаче.
     *
     * SQL: UPDATE ... SET claimEpoch = claimEpoch + 1, claimedAt = :now
     *      WHERE id = :trackId AND (claimEpoch = 0 OR claimedAt < :staleThreshold)
     * Затем: SELECT claimEpoch FROM tracks WHERE id = :trackId
     */
    @Query("""
        UPDATE tracks
        SET claimEpoch = claimEpoch + 1, claimedAt = :now
        WHERE id = :trackId
          AND (claimEpoch = 0 OR claimedAt < :staleThreshold)
    """)
    suspend fun tryIncrementClaimEpoch(trackId: String, now: Long, staleThreshold: Long): Int

    @Query("SELECT claimEpoch FROM tracks WHERE id = :trackId LIMIT 1")
    suspend fun getCurrentClaimEpoch(trackId: String): Long?

    /**
     * Owner-aware обновление. Обновляет только если claimEpoch совпадает.
     * Возвращает 1 при успехе, 0 если claim потерян.
     */
    @Query("""
        UPDATE tracks
        SET copyStatus = :status, museCopyPath = :path, nextAttemptAt = :nextAttemptAt
        WHERE id = :trackId AND claimEpoch = :epoch
    """)
    suspend fun updateCopyStateIfOwner(
        trackId: String,
        epoch: Long,
        status: String,
        path: String?,
        nextAttemptAt: Long
    ): Int

    /** Освобождает claim, если epoch совпадает. */
    @Query("UPDATE tracks SET claimEpoch = 0, claimedAt = 0 WHERE id = :trackId AND claimEpoch = :epoch")
    suspend fun releaseClaim(trackId: String, epoch: Long): Int

    @Query("""
        SELECT * FROM tracks
        WHERE originalAvailable = 1
          AND copyStatus != 'IN_PROGRESS'
          AND (copyStatus != 'COMPLETED' AND copyStatus != 'COMPLETED_UNVERIFIED' OR museCopyPath IS NULL)
          AND nextAttemptAt < :permanentThreshold
          AND nextAttemptAt <= :now
          AND (claimEpoch = 0 OR claimedAt < :staleThreshold)
          AND id NOT IN (:excludeIds)
        ORDER BY dateAdded DESC
        LIMIT 1
    """)
    suspend fun findNextPendingCopy(
        now: Long,
        permanentThreshold: Long,
        staleThreshold: Long,
        excludeIds: List<String>
    ): TrackEntity?

    @Query("""
        SELECT MIN(nextAttemptAt) FROM tracks
        WHERE originalAvailable = 1
          AND copyStatus = 'FAILED'
          AND nextAttemptAt > :now
          AND nextAttemptAt < :permanentThreshold
    """)
    suspend fun findNearestRetryableAttemptAt(now: Long, permanentThreshold: Long): Long?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(track: TrackEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(track: TrackEntity)

    @Query("UPDATE tracks SET originalAvailable = :available WHERE id = :id")
    suspend fun setOriginalAvailable(id: String, available: Boolean)

    @Query("SELECT COUNT(*) FROM tracks")
    suspend fun count(): Int
}