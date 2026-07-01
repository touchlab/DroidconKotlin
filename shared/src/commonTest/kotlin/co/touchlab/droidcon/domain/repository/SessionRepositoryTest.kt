package co.touchlab.droidcon.domain.repository

import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.repository.impl.SqlDelightSessionRepository
import co.touchlab.droidcon.test.TestDatabase
import co.touchlab.droidcon.test.TestSessionFactory
import co.touchlab.droidcon.test.createTestDatabase
import co.touchlab.droidcon.test.runRepositoryTest
import co.touchlab.droidcon.test.seedSecondConference
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SessionRepositoryTest {

    @Test
    fun addOrUpdate_and_find_returns_session() = runRepositoryTest {
        val testDb = createTestDatabase()
        val repository = createRepository(testDb)
        val session = TestSessionFactory.session(title = "Kotlin Multiplatform")

        repository.addOrUpdate(session, testDb.conferenceId)

        val found = repository.findSync(session.id, testDb.conferenceId)
        assertNotNull(found)
        assertEquals("Kotlin Multiplatform", found.title)
        assertEquals(session.description, found.description)
        assertEquals(1, repository.allSync(testDb.conferenceId).size)
    }

    @Test
    fun setRsvp_updates_attending_and_observeAllAttending() = runRepositoryTest {
        val testDb = createTestDatabase()
        val repository = createRepository(testDb)
        val session = TestSessionFactory.session()
        repository.addOrUpdate(session, testDb.conferenceId)

        repository.setRsvp(session.id, Session.RSVP(isAttending = true, isSent = false), testDb.conferenceId)

        val attending = repository.allAttending(testDb.conferenceId)
        assertEquals(1, attending.size)
        assertTrue(attending.first().rsvp.isAttending)

        repository.setRsvp(session.id, Session.RSVP(isAttending = false, isSent = false), testDb.conferenceId)
        assertTrue(repository.allAttending(testDb.conferenceId).isEmpty())
    }

    @Test
    fun setRsvpSent_persists_sent_flag() = runRepositoryTest {
        val testDb = createTestDatabase()
        val repository = createRepository(testDb)
        val session = TestSessionFactory.session()
        repository.addOrUpdate(session, testDb.conferenceId)

        repository.setRsvp(session.id, Session.RSVP(isAttending = true, isSent = false), testDb.conferenceId)
        repository.setRsvpSent(session.id, isSent = true, testDb.conferenceId)

        val found = repository.findSync(session.id, testDb.conferenceId)
        assertNotNull(found)
        assertTrue(found.rsvp.isSent)
    }

    @Test
    fun setFeedback_persists_rating_and_comment() = runRepositoryTest {
        val testDb = createTestDatabase()
        val repository = createRepository(testDb)
        val session = TestSessionFactory.session()
        repository.addOrUpdate(session, testDb.conferenceId)

        val feedback = Session.Feedback(
            rating = Session.Feedback.Rating.SATISFIED,
            comment = "Great talk",
            isSent = true,
        )
        repository.setFeedback(session.id, feedback, testDb.conferenceId)

        val found = repository.findSync(session.id, testDb.conferenceId)
        assertNotNull(found?.feedback)
        assertEquals(Session.Feedback.Rating.SATISFIED, found.feedback?.rating)
        assertEquals("Great talk", found.feedback?.comment)
        assertFalse(found.feedback!!.isSent)
    }

    @Test
    fun setFeedbackSent_persists() = runRepositoryTest {
        val testDb = createTestDatabase()
        val repository = createRepository(testDb)
        val session = TestSessionFactory.session()
        repository.addOrUpdate(session, testDb.conferenceId)
        repository.setFeedback(
            session.id,
            Session.Feedback(
                rating = Session.Feedback.Rating.NORMAL,
                comment = "Good",
                isSent = false,
            ),
            testDb.conferenceId,
        )

        repository.setFeedbackSent(session.id, isSent = true, testDb.conferenceId)

        val found = repository.findSync(session.id, testDb.conferenceId)
        assertNotNull(found?.feedback)
        assertTrue(found.feedback?.isSent == true)
    }

    @Test
    fun remove_deletes_session() = runRepositoryTest {
        val testDb = createTestDatabase()
        val repository = createRepository(testDb)
        val session = TestSessionFactory.session()
        repository.addOrUpdate(session, testDb.conferenceId)

        val removed = repository.remove(session.id, testDb.conferenceId)
        assertTrue(removed)
        assertFalse(repository.contains(session.id, testDb.conferenceId))
        assertNull(repository.findSync(session.id, testDb.conferenceId))
    }

    @Test
    fun conference_isolation() = runRepositoryTest {
        val testDb = createTestDatabase()
        val repository = createRepository(testDb)
        val secondConferenceId = seedSecondConference(testDb.database)
        assertNotEquals(testDb.conferenceId, secondConferenceId)

        val conferenceOneSession = TestSessionFactory.session(id = "session-conf-one", title = "Conference One Session")
        val conferenceTwoSession = TestSessionFactory.session(id = "session-conf-two", title = "Conference Two Session")
        repository.addOrUpdate(conferenceOneSession, testDb.conferenceId)
        repository.addOrUpdate(conferenceTwoSession, secondConferenceId)

        assertEquals(
            "Conference One Session",
            repository.findSync(conferenceOneSession.id, testDb.conferenceId)?.title,
        )
        assertEquals(
            "Conference Two Session",
            repository.findSync(conferenceTwoSession.id, secondConferenceId)?.title,
        )
        assertNull(repository.findSync(conferenceOneSession.id, secondConferenceId))
        assertNull(repository.findSync(conferenceTwoSession.id, testDb.conferenceId))
    }

    private fun createRepository(testDb: TestDatabase): SqlDelightSessionRepository = SqlDelightSessionRepository(
        dateTimeService = TestSessionFactory.dateTimeService,
        sessionQueries = testDb.database.sessionQueries,
    )
}
