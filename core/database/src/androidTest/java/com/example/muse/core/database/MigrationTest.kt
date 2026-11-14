package com.example.muse.core.database

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MigrationTest {

    private val TEST_DB = "migration-test"

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        MuseDatabase::class.java,
        emptyList(),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migrate1To2() {
        helper.createDatabase(TEST_DB, 1).apply {
            execSQL("INSERT INTO play_events (id, sessionId, playbackAttemptId, trackId, eventType, positionMs, timestamp, synced) VALUES ('e1', 's1', 'a1', 't1', 'PLAY_START', 0, 1000, 0)")
            close()
        }

        val db = helper.runMigrationsAndValidate(TEST_DB, 2, true, MuseDatabase.MIGRATION_1_2)

        db.query("SELECT playbackItemId FROM play_events WHERE id = 'e1'").use { cursor ->
            cursor.moveToFirst()
            val value = cursor.getString(0)
            assertEquals("", value) // 默认值为空字符串
        }
    }
}