package co.touchlab.droidcon.domain.repository

import app.cash.sqldelight.async.coroutines.awaitAsList
import co.touchlab.droidcon.domain.repository.impl.SqlDelightConferenceRepository
import co.touchlab.droidcon.test.TestDatabase
import co.touchlab.droidcon.test.TestEntityFactory
import co.touchlab.droidcon.test.createTestDatabase
import co.touchlab.droidcon.test.runRepositoryTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ConferenceRepositoryTest {

    @Test
    fun add_persists_conference_and_returns_id() = runRepositoryTest {
        val testDb = createTestDatabase()
        val repository = createRepository(testDb)
        val conference = TestEntityFactory.conference(name = "Added Conference")

        val id = repository.add(conference)

        assertTrue(id > 0)
        val stored = testDb.database.conferenceQueries.selectById(id) { confId, name, _, _, _, _, _, _, _, _ ->
            confId to name
        }.awaitAsList().single()
        assertEquals(id, stored.first)
        assertEquals("Added Conference", stored.second)
    }

    @Test
    fun update_modifies_conference() = runRepositoryTest {
        val testDb = createTestDatabase()
        val repository = createRepository(testDb)
        val conference = TestEntityFactory.conference(
            id = testDb.conferenceId,
            name = "Updated Conference",
            projectId = "updated-project",
        )

        val updated = repository.update(conference)

        assertTrue(updated)
        val storedName = testDb.database.conferenceQueries.selectById(testDb.conferenceId) { _, name, _, _, _, _, _, _, _, _ ->
            name
        }.awaitAsList().single()
        assertEquals("Updated Conference", storedName)
    }

    @Test
    fun select_changes_selected_conference() = runRepositoryTest {
        val testDb = createTestDatabase()
        val repository = createRepository(testDb)
        val newConferenceId = repository.add(TestEntityFactory.conference(name = "Selectable Conference"))

        val selected = repository.select(newConferenceId)

        assertTrue(selected)
        val selectedConference = repository.getSelected()
        assertEquals(newConferenceId, selectedConference.id)
        assertEquals("Selectable Conference", selectedConference.name)
    }

    @Test
    fun delete_removes_conference() = runRepositoryTest {
        val testDb = createTestDatabase()
        val repository = createRepository(testDb)
        val conferenceId = repository.add(TestEntityFactory.conference(name = "Deleted Conference"))

        val deleted = repository.delete(conferenceId)

        assertTrue(deleted)
        assertTrue(
            testDb.database.conferenceQueries.selectById(conferenceId) { id, _, _, _, _, _, _, _, _, _ ->
                id
            }.awaitAsList().isEmpty(),
        )
    }

    @Test
    fun getSelected_returns_test_conference_after_select() = runRepositoryTest {
        val testDb = createTestDatabase()
        val repository = createRepository(testDb)

        repository.select(testDb.conferenceId)

        val selected = repository.getSelected()
        assertEquals(testDb.conferenceId, selected.id)
        assertEquals("Test Conference", selected.name)
        assertNotEquals("Droidcon NYC 2025", selected.name)
    }

    private fun createRepository(testDb: TestDatabase): SqlDelightConferenceRepository =
        SqlDelightConferenceRepository(conferenceQueries = testDb.database.conferenceQueries)
}
