package co.touchlab.droidcon.domain.repository

import co.touchlab.droidcon.domain.repository.impl.SqlDelightProfileRepository
import co.touchlab.droidcon.domain.repository.impl.SqlDelightSessionRepository
import co.touchlab.droidcon.domain.repository.impl.SqlDelightSponsorGroupRepository
import co.touchlab.droidcon.domain.repository.impl.SqlDelightSponsorRepository
import co.touchlab.droidcon.test.TestDatabase
import co.touchlab.droidcon.test.TestEntityFactory
import co.touchlab.droidcon.test.TestSessionFactory
import co.touchlab.droidcon.test.createTestDatabase
import co.touchlab.droidcon.test.runRepositoryTest
import co.touchlab.droidcon.test.seedSecondConference
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ProfileRepositoryTest {

    @Test
    fun addOrUpdate_and_allSync_returns_profile() = runRepositoryTest {
        val testDb = createTestDatabase()
        val repository = createRepository(testDb)
        val profile = TestEntityFactory.profile(fullName = "Alex Developer")

        repository.addOrUpdate(profile, testDb.conferenceId)

        val all = repository.allSync(testDb.conferenceId)
        assertEquals(1, all.size)
        assertEquals("Alex Developer", all.first().fullName)
        assertEquals(profile.bio, all.first().bio)
        assertEquals(profile.twitter, all.first().twitter)
    }

    @Test
    fun setSessionSpeakers_and_getSpeakersBySession() = runRepositoryTest {
        val testDb = createTestDatabase()
        val repository = createRepository(testDb)
        val sessionRepository = createSessionRepository(testDb)
        val session = TestSessionFactory.session()
        sessionRepository.addOrUpdate(session, testDb.conferenceId)
        val speakerOne = TestEntityFactory.profile(id = "speaker-1", fullName = "First Speaker")
        val speakerTwo = TestEntityFactory.profile(id = "speaker-2", fullName = "Second Speaker")
        repository.addOrUpdate(speakerOne, testDb.conferenceId)
        repository.addOrUpdate(speakerTwo, testDb.conferenceId)

        repository.setSessionSpeakers(
            session,
            listOf(speakerTwo.id, speakerOne.id),
            testDb.conferenceId,
        )

        val speakers = repository.getSpeakersBySession(session.id, testDb.conferenceId)
        assertEquals(listOf("Second Speaker", "First Speaker"), speakers.map { it.fullName })
    }

    @Test
    fun setSponsorRepresentatives_and_getSponsorRepresentatives() = runRepositoryTest {
        val testDb = createTestDatabase()
        val repository = createRepository(testDb)
        val groupRepository = SqlDelightSponsorGroupRepository(testDb.database.sponsorGroupQueries)
        val sponsorRepository = SqlDelightSponsorRepository(testDb.database.sponsorQueries)
        val group = TestEntityFactory.sponsorGroup()
        groupRepository.addOrUpdate(group, testDb.conferenceId)
        val sponsor = TestEntityFactory.sponsor()
        sponsorRepository.addOrUpdate(sponsor, testDb.conferenceId)
        val repOne = TestEntityFactory.profile(id = "rep-1", fullName = "Rep One")
        val repTwo = TestEntityFactory.profile(id = "rep-2", fullName = "Rep Two")
        repository.addOrUpdate(repOne, testDb.conferenceId)
        repository.addOrUpdate(repTwo, testDb.conferenceId)

        repository.setSponsorRepresentatives(
            sponsor,
            listOf(repTwo.id, repOne.id),
            testDb.conferenceId,
        )

        val representatives = repository.getSponsorRepresentatives(sponsor.id, testDb.conferenceId)
        assertEquals(listOf("Rep Two", "Rep One"), representatives.map { it.fullName })
    }

    @Test
    fun remove_deletes_profile() = runRepositoryTest {
        val testDb = createTestDatabase()
        val repository = createRepository(testDb)
        val profile = TestEntityFactory.profile()
        repository.addOrUpdate(profile, testDb.conferenceId)

        val removed = repository.remove(profile.id, testDb.conferenceId)
        assertTrue(removed)
        assertFalse(repository.contains(profile.id, testDb.conferenceId))
        assertTrue(repository.allSync(testDb.conferenceId).isEmpty())
    }

    @Test
    fun conference_isolation() = runRepositoryTest {
        val testDb = createTestDatabase()
        val repository = createRepository(testDb)
        val secondConferenceId = seedSecondConference(testDb.database)
        assertNotEquals(testDb.conferenceId, secondConferenceId)

        val conferenceOneProfile = TestEntityFactory.profile(id = "profile-conf-one", fullName = "Conference One")
        val conferenceTwoProfile = TestEntityFactory.profile(id = "profile-conf-two", fullName = "Conference Two")
        repository.addOrUpdate(conferenceOneProfile, testDb.conferenceId)
        repository.addOrUpdate(conferenceTwoProfile, secondConferenceId)

        assertEquals(
            "Conference One",
            findProfile(repository, conferenceOneProfile.id, testDb.conferenceId)?.fullName,
        )
        assertEquals(
            "Conference Two",
            findProfile(repository, conferenceTwoProfile.id, secondConferenceId)?.fullName,
        )
        assertNull(findProfile(repository, conferenceOneProfile.id, secondConferenceId))
        assertNull(findProfile(repository, conferenceTwoProfile.id, testDb.conferenceId))
    }

    private fun createRepository(testDb: TestDatabase): SqlDelightProfileRepository = SqlDelightProfileRepository(
        profileQueries = testDb.database.profileQueries,
        speakerQueries = testDb.database.sessionSpeakerQueries,
        representativeQueries = testDb.database.sponsorRepresentativeQueries,
    )

    private fun createSessionRepository(testDb: TestDatabase): SqlDelightSessionRepository = SqlDelightSessionRepository(
        dateTimeService = TestSessionFactory.dateTimeService,
        sessionQueries = testDb.database.sessionQueries,
    )

    private suspend fun findProfile(
        repository: SqlDelightProfileRepository,
        id: co.touchlab.droidcon.domain.entity.Profile.Id,
        conferenceId: Long,
    ) = repository.allSync(conferenceId).find { it.id == id }
}
