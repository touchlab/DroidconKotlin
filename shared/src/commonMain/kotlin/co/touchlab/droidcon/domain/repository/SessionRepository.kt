package co.touchlab.droidcon.domain.repository

import co.touchlab.droidcon.domain.entity.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository : Repository<Session.Id, Session> {

    fun observeAllAttending(conferenceId: Long): Flow<List<Session>>

    suspend fun allAttending(conferenceId: Long): List<Session>

    suspend fun setRsvp(sessionId: Session.Id, rsvp: Session.RSVP, conferenceId: Long)

    suspend fun setRsvpSent(sessionId: Session.Id, isSent: Boolean, conferenceId: Long)

    suspend fun setFeedback(sessionId: Session.Id, feedback: Session.Feedback, conferenceId: Long)

    suspend fun setFeedbackSent(sessionId: Session.Id, isSent: Boolean, conferenceId: Long)

    fun allSync(conferenceId: Long): List<Session>

    fun findSync(id: Session.Id, conferenceId: Long): Session?
}
