package co.touchlab.droidcon.domain.repository

import co.touchlab.droidcon.domain.repository.impl.SqlDelightRoomRepository
import co.touchlab.droidcon.test.TestDatabase
import co.touchlab.droidcon.test.TestEntityFactory
import co.touchlab.droidcon.test.createTestDatabase
import co.touchlab.droidcon.test.runRepositoryTest
import co.touchlab.droidcon.test.seedSecondConference
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RoomRepositoryTest {

    @Test
    fun addOrUpdate_and_allSync_returns_room() = runRepositoryTest {
        val testDb = createTestDatabase()
        val repository = createRepository(testDb)
        val room = TestEntityFactory.room(name = "Auditorium")

        repository.addOrUpdate(room, testDb.conferenceId)

        val all = repository.allSync(testDb.conferenceId)
        assertEquals(1, all.size)
        assertEquals("Auditorium", all.first().name)
        assertEquals(room.id, all.first().id)
    }

    @Test
    fun addOrUpdate_updates_existing_room() = runRepositoryTest {
        val testDb = createTestDatabase()
        val repository = createRepository(testDb)
        val room = TestEntityFactory.room()
        repository.addOrUpdate(room, testDb.conferenceId)

        repository.addOrUpdate(TestEntityFactory.room(id = room.id.value, name = "Updated Hall"), testDb.conferenceId)

        val found = repository.allSync(testDb.conferenceId).single()
        assertEquals("Updated Hall", found.name)
    }

    @Test
    fun remove_deletes_room() = runRepositoryTest {
        val testDb = createTestDatabase()
        val repository = createRepository(testDb)
        val room = TestEntityFactory.room()
        repository.addOrUpdate(room, testDb.conferenceId)

        val removed = repository.remove(room.id, testDb.conferenceId)
        assertTrue(removed)
        assertFalse(repository.contains(room.id, testDb.conferenceId))
        assertTrue(repository.allSync(testDb.conferenceId).isEmpty())
    }

    @Test
    fun conference_isolation() = runRepositoryTest {
        val testDb = createTestDatabase()
        val repository = createRepository(testDb)
        val secondConferenceId = seedSecondConference(testDb.database)
        assertNotEquals(testDb.conferenceId, secondConferenceId)

        val conferenceOneRoom = TestEntityFactory.room(id = 1L, name = "Room One")
        val conferenceTwoRoom = TestEntityFactory.room(id = 2L, name = "Room Two")
        repository.addOrUpdate(conferenceOneRoom, testDb.conferenceId)
        repository.addOrUpdate(conferenceTwoRoom, secondConferenceId)

        assertEquals("Room One", findRoom(repository, conferenceOneRoom.id, testDb.conferenceId)?.name)
        assertEquals("Room Two", findRoom(repository, conferenceTwoRoom.id, secondConferenceId)?.name)
        assertNull(findRoom(repository, conferenceOneRoom.id, secondConferenceId))
        assertNull(findRoom(repository, conferenceTwoRoom.id, testDb.conferenceId))
    }

    private fun createRepository(testDb: TestDatabase): SqlDelightRoomRepository =
        SqlDelightRoomRepository(roomQueries = testDb.database.roomQueries)

    private suspend fun findRoom(
        repository: SqlDelightRoomRepository,
        id: co.touchlab.droidcon.domain.entity.Room.Id,
        conferenceId: Long,
    ) = repository.allSync(conferenceId).find { it.id == id }
}
