package co.touchlab.droidcon.domain.repository

import co.touchlab.droidcon.domain.entity.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository: Repository<Session.Id, Session> {

    fun observeAllAttending(): Flow<List<Session>>

    suspend fun allAttending(): List<Session>

    suspend fun setRsvp(sessionId: Session.Id, isAttending: Boolean)

    suspend fun setRsvpSent(sessionId: Session.Id, isSent: Boolean)

    suspend fun setFeedback(sessionId: Session.Id, feedback: Session.Feedback)

    suspend fun setFeedbackSent(sessionId: Session.Id, isSent: Boolean)
}
