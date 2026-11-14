package com.example.muse.core.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Track(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val uri: String,
    val artworkUri: String? = null,
    val dateAdded: Long = System.currentTimeMillis(),
    val stableKey: String = "",
    val vectorVersion: Int = 0,
    val analyzedAt: Long? = null,
    val museCopyPath: String? = null,
    val copyStatus: String = CopyStatus.NONE,
    val originalAvailable: Boolean = true
) {
    object CopyStatus {
        const val NONE = "NONE"
        const val IN_PROGRESS = "IN_PROGRESS"
        /** Размер совпал с оригиналом (полная проверка). */
        const val COMPLETED = "COMPLETED"
        /** Файл непуст, но размер оригинала неизвестен (ограниченная проверка). */
        const val COMPLETED_UNVERIFIED = "COMPLETED_UNVERIFIED"
        const val FAILED = "FAILED"
    }
}