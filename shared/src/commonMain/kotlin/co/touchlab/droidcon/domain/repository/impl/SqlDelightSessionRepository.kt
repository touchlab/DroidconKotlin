package co.touchlab.droidcon.domain.repository.impl

import co.touchlab.droidcon.db.SessionQueries
import co.touchlab.droidcon.domain.entity.Room
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.repository.SessionRepository
import co.touchlab.droidcon.domain.service.DateTimeService
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Instant

class SqlDelightSessionRepository(
    private val dateTimeService: DateTimeService,
    private val sessionQueries: SessionQueries,
): BaseRepository<Session.Id, Session>(), SessionRepository {
    override fun observe(id: Session.Id): Flow<Session> {
        return sessionQueries.sessionById(id.value, ::sessionFactory).asFlow().mapToOne()
    }

    override fun observeOrNull(id: Session.Id): Flow<Session?> {
        return sessionQueries.sessionById(id.value, ::sessionFactory).asFlow().mapToOneOrNull()
    }

    override fun observeAllAttending(): Flow<List<Session>> {
        return sessionQueries.attendingSessions(::sessionFactory).asFlow().mapToList()
    }

    override suspend fun allAttending(): List<Session> {
        return observeAllAttending().first()
    }

    override suspend fun setAttending(sessionId: Session.Id, attending: Boolean) {
        sessionQueries.updateRsvp(attending.toLong(), sessionId.value)
    }

    override suspend fun setFeedback(sessionId: Session.Id, feedback: Session.Feedback) {
        sessionQueries.updateFeedBack(feedback.rating, feedback.comment, sessionId.value)
    }

    override suspend fun setFeedbackSent(sessionId: Session.Id, isSent: Boolean) {
        sessionQueries.updateFeedBackSent(if (isSent) 1 else 0, sessionId.value)
    }

    override fun observeAll(): Flow<List<Session>> {
        return sessionQueries.allSessions(::sessionFactory).asFlow().mapToList()
    }

    override suspend fun doUpsert(entity: Session) {
        sessionQueries.upsert(
            id = entity.id.value,
            title = entity.title,
            description = entity.description,
            startsAt = entity.startsAt,
            endsAt = entity.endsAt,
            serviceSession = entity.isServiceSession.toLong(),
            roomId = entity.room?.value,
        )
    }

    override suspend fun doDelete(id: Session.Id) {
        return sessionQueries.deleteById(id.value)
    }

    override suspend fun contains(id: Session.Id): Boolean {
        return sessionQueries.existsById(id.value).executeAsOne().toBoolean()
    }

    private fun sessionFactory(
        id: String,
        title: String,
        description: String?,
        startsAt: Instant,
        endsAt: Instant,
        serviceSession: Long,
        rsvp: Long,
        roomId: Long?,
        feedbackRating: Int?,
        feedbackComment: String?,
        feedbackSent: Long,
    ) = Session(
        dateTimeService = dateTimeService,
        id = Session.Id(id),
        title = title,
        description = description,
        startsAt = startsAt,
        endsAt = endsAt,
        isServiceSession = serviceSession.toBoolean(),
        room = roomId?.let(Room::Id),
        isAttending = rsvp.toBoolean(),
        feedback = if (feedbackRating != null && feedbackComment != null) {
            Session.Feedback(feedbackRating, feedbackComment, feedbackSent.toBoolean())
        } else {
            null
        },
    )
}
