package co.touchlab.droidcon.domain.repository

import co.touchlab.droidcon.domain.repository.impl.SqlDelightSponsorGroupRepository
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

class SponsorGroupRepositoryTest {

    @Test
    fun addOrUpdate_and_allSync_returns_sponsor_group() = runRepositoryTest {
        val testDb = createTestDatabase()
        val repository = createRepository(testDb)
        val group = TestEntityFactory.sponsorGroup(name = "Platinum", displayPriority = 0)

        repository.addOrUpdate(group, testDb.conferenceId)

        val all = repository.allSync(testDb.conferenceId)
        assertEquals(1, all.size)
        assertEquals("Platinum", all.first().name)
        assertEquals(0, all.first().displayPriority)
        assertTrue(all.first().isProminent)
    }

    @Test
    fun addOrUpdate_updates_existing_group() = runRepositoryTest {
        val testDb = createTestDatabase()
        val repository = createRepository(testDb)
        val group = TestEntityFactory.sponsorGroup(isProminent = false)
        repository.addOrUpdate(group, testDb.conferenceId)

        repository.addOrUpdate(
            TestEntityFactory.sponsorGroup(displayPriority = 5, isProminent = true),
            testDb.conferenceId,
        )

        val found = repository.allSync(testDb.conferenceId).single()
        assertEquals(5, found.displayPriority)
        assertTrue(found.isProminent)
    }

    @Test
    fun remove_deletes_sponsor_group() = runRepositoryTest {
        val testDb = createTestDatabase()
        val repository = createRepository(testDb)
        val group = TestEntityFactory.sponsorGroup()
        repository.addOrUpdate(group, testDb.conferenceId)

        val removed = repository.remove(group.id, testDb.conferenceId)
        assertTrue(removed)
        assertFalse(repository.contains(group.id, testDb.conferenceId))
        assertTrue(repository.allSync(testDb.conferenceId).isEmpty())
    }

    @Test
    fun conference_isolation() = runRepositoryTest {
        val testDb = createTestDatabase()
        val repository = createRepository(testDb)
        val secondConferenceId = seedSecondConference(testDb.database)
        assertNotEquals(testDb.conferenceId, secondConferenceId)

        val conferenceOneGroup = TestEntityFactory.sponsorGroup(name = "Gold", displayPriority = 1)
        val conferenceTwoGroup = TestEntityFactory.sponsorGroup(name = "Silver", displayPriority = 99)
        repository.addOrUpdate(conferenceOneGroup, testDb.conferenceId)
        repository.addOrUpdate(conferenceTwoGroup, secondConferenceId)

        assertEquals(1, findGroup(repository, conferenceOneGroup.id, testDb.conferenceId)?.displayPriority)
        assertEquals(99, findGroup(repository, conferenceTwoGroup.id, secondConferenceId)?.displayPriority)
        assertNull(findGroup(repository, conferenceOneGroup.id, secondConferenceId))
        assertNull(findGroup(repository, conferenceTwoGroup.id, testDb.conferenceId))
    }

    private fun createRepository(testDb: TestDatabase): SqlDelightSponsorGroupRepository =
        SqlDelightSponsorGroupRepository(sponsorGroupQueries = testDb.database.sponsorGroupQueries)

    private suspend fun findGroup(
        repository: SqlDelightSponsorGroupRepository,
        id: co.touchlab.droidcon.domain.entity.SponsorGroup.Id,
        conferenceId: Long,
    ) = repository.allSync(conferenceId).find { it.id == id }
}
