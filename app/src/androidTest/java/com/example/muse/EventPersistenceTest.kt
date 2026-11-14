package com.example.muse

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.muse.core.database.MuseDatabase
import com.example.muse.core.database.entity.PlayEventEntity
import com.example.muse.core.model.EventType
import com.example.muse.core.model.PlayEvent
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EventPersistenceTest {

    private lateinit var db: MuseDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(context, MuseDatabase::class.java).build()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun eventIsPersistedInRoom() = runBlocking {
        val event = PlayEvent(
            sessionId = "session-1",
            playbackAttemptId = "attempt-1",
            trackId = "track-1",
            eventType = EventType.PLAY_START,
            positionMs = 0L
        )
        db.playEventDao().insert(PlayEventEntity.fromDomain(event))

        val pending = db.playEventDao().pendingSync()
        assertEquals(1, pending.size)
        assertEquals("session-1", pending[0].sessionId)
        assertEquals("attempt-1", pending[0].playbackAttemptId)
    }
}