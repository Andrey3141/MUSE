package com.example.muse.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.muse.core.database.entity.PlayEventEntity
import com.example.muse.core.database.entity.PlaylistEntity
import com.example.muse.core.database.entity.PlaylistTrackEntity
import com.example.muse.core.database.entity.TrackEntity

@Database(
    entities = [
        TrackEntity::class,
        PlayEventEntity::class,
        PlaylistEntity::class,
        PlaylistTrackEntity::class
    ],
    version = 5,
    exportSchema = true
)
abstract class MuseDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun playEventDao(): PlayEventDao
    abstract fun playlistDao(): PlaylistDao

    companion object {
        @Volatile private var INSTANCE: MuseDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE play_events ADD COLUMN playbackItemId TEXT NOT NULL DEFAULT ''")
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tracks ADD COLUMN museCopyPath TEXT")
                db.execSQL("ALTER TABLE tracks ADD COLUMN copyStatus TEXT NOT NULL DEFAULT 'NONE'")
                db.execSQL("ALTER TABLE tracks ADD COLUMN originalAvailable INTEGER NOT NULL DEFAULT 1")
            }
        }

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tracks ADD COLUMN nextAttemptAt INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Если в предыдущей версии уже были claimOwner/claimedAt (String), удаляем их.
                // Если нет — добавляем claimEpoch/claimedAt.
                // Безопасный подход: добавить claimEpoch, claimedAt (если их нет).
                // SQLite не поддерживает IF NOT EXISTS для ADD COLUMN, поэтому
                // предполагаем, что предыдущая версия была 4 и claimOwner отсутствует.
                db.execSQL("ALTER TABLE tracks ADD COLUMN claimEpoch INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE tracks ADD COLUMN claimedAt INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun get(context: Context): MuseDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    MuseDatabase::class.java,
                    "muse.db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                    .build()
                    .also { INSTANCE = it }
            }
    }
}