package co.touchlab.droidcon.domain.repository

import co.touchlab.droidcon.domain.repository.impl.SqlDelightSponsorGroupRepository
import co.touchlab.droidcon.domain.repository.impl.SqlDelightSponsorRepository
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

class SponsorRepositoryTest {

    @Test
    fun addOrUpdate_and_allSync_returns_sponsor() = runRepositoryTest {
        val testDb = createTestDatabase()
        val groupRepository = createGroupRepository(testDb)
        val repository = createRepository(testDb)
        val group = TestEntityFactory.sponsorGroup()
        groupRepository.addOrUpdate(group, testDb.conferenceId)
        val sponsor = TestEntityFactory.sponsor(name = "Acme Corp")

        repository.addOrUpdate(sponsor, testDb.conferenceId)

        val all = repository.allSync(testDb.conferenceId)
        assertEquals(1, all.size)
        assertEquals("Acme Corp", all.first().name)
        assertEquals("KMP specialists", all.first().description)
    }

    @Test
    fun allByGroupName_returns_sponsors_in_group() = runRepositoryTest {
        val testDb = createTestDatabase()
        val groupRepository = createGroupRepository(testDb)
        val repository = createRepository(testDb)
        groupRepository.addOrUpdate(TestEntityFactory.sponsorGroup(name = "Gold"), testDb.conferenceId)
        groupRepository.addOrUpdate(TestEntityFactory.sponsorGroup(name = "Silver"), testDb.conferenceId)
        repository.addOrUpdate(TestEntityFactory.sponsor(name = "Sponsor A", group = "Gold"), testDb.conferenceId)
        repository.addOrUpdate(TestEntityFactory.sponsor(name = "Sponsor B", group = "Gold"), testDb.conferenceId)
        repository.addOrUpdate(TestEntityFactory.sponsor(name = "Sponsor C", group = "Silver"), testDb.conferenceId)

        val goldSponsors = repository.allByGroupName("Gold", testDb.conferenceId)
        assertEquals(2, goldSponsors.size)
        assertEquals(setOf("Sponsor A", "Sponsor B"), goldSponsors.map { it.name }.toSet())
    }

    @Test
    fun remove_deletes_sponsor() = runRepositoryTest {
        val testDb = createTestDatabase()
        val groupRepository = createGroupRepository(testDb)
        val repository = createRepository(testDb)
        groupRepository.addOrUpdate(TestEntityFactory.sponsorGroup(), testDb.conferenceId)
        val sponsor = TestEntityFactory.sponsor()
        repository.addOrUpdate(sponsor, testDb.conferenceId)

        val removed = repository.remove(sponsor.id, testDb.conferenceId)
        assertTrue(removed)
        assertFalse(repository.contains(sponsor.id, testDb.conferenceId))
        assertTrue(repository.allSync(testDb.conferenceId).isEmpty())
    }

    @Test
    fun conference_isolation() = runRepositoryTest {
        val testDb = createTestDatabase()
        val groupRepository = createGroupRepository(testDb)
        val repository = createRepository(testDb)
        val secondConferenceId = seedSecondConference(testDb.database)
        assertNotEquals(testDb.conferenceId, secondConferenceId)

        groupRepository.addOrUpdate(TestEntityFactory.sponsorGroup(), testDb.conferenceId)
        groupRepository.addOrUpdate(TestEntityFactory.sponsorGroup(), secondConferenceId)
        val conferenceOneSponsor = TestEntityFactory.sponsor(name = "Sponsor One", description = "Conference one")
        val conferenceTwoSponsor = TestEntityFactory.sponsor(name = "Sponsor Two", description = "Conference two")
        repository.addOrUpdate(conferenceOneSponsor, testDb.conferenceId)
        repository.addOrUpdate(conferenceTwoSponsor, secondConferenceId)

        assertEquals(
            "Conference one",
            findSponsor(repository, conferenceOneSponsor.id, testDb.conferenceId)?.description,
        )
        assertEquals(
            "Conference two",
            findSponsor(repository, conferenceTwoSponsor.id, secondConferenceId)?.description,
        )
        assertNull(findSponsor(repository, conferenceOneSponsor.id, secondConferenceId))
        assertNull(findSponsor(repository, conferenceTwoSponsor.id, testDb.conferenceId))
    }

    private fun createRepository(testDb: TestDatabase): SqlDelightSponsorRepository =
        SqlDelightSponsorRepository(sponsorQueries = testDb.database.sponsorQueries)

    private fun createGroupRepository(testDb: TestDatabase): SqlDelightSponsorGroupRepository =
        SqlDelightSponsorGroupRepository(sponsorGroupQueries = testDb.database.sponsorGroupQueries)

    private suspend fun findSponsor(
        repository: SqlDelightSponsorRepository,
        id: co.touchlab.droidcon.domain.entity.Sponsor.Id,
        conferenceId: Long,
    ) = repository.allSync(conferenceId).find { it.id == id }
}
