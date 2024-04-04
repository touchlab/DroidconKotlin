package co.touchlab.droidcon.domain.repository.impl

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrNull
import co.touchlab.droidcon.db.SessionQueries
import co.touchlab.droidcon.domain.entity.Room
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.repository.SessionRepository
import co.touchlab.droidcon.domain.service.DateTimeService
import co.touchlab.droidcon.domain.service.impl.DefaultSyncService
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.datetime.Instant

class SqlDelightSessionRepository(
    private val dateTimeService: DateTimeService,
    private val sessionQueries: SessionQueries,
    ) : BaseRepository<Session.Id, Session>(), SessionRepository {
    override fun observe(id: Session.Id): Flow<Session> {
        return sessionQueries.sessionById(id.value, ::sessionFactory).asFlow().mapToOne(Dispatchers.Main)
    }

    fun sessionById(id: Session.Id): Session? = sessionQueries.sessionById(id.value, ::sessionFactory).executeAsOneOrNull()

    override fun observeOrNull(id: Session.Id): Flow<Session?> {
        return sessionQueries.sessionById(id.value, ::sessionFactory).asFlow().mapToOneOrNull(Dispatchers.Main)
    }

    override fun observeAllAttending(): Flow<List<Session>> {
        return sessionQueries.attendingSessions(::sessionFactory).asFlow().mapToList(Dispatchers.Main)
    }

    override suspend fun allAttending(): List<Session> {
        return observeAllAttending().first()
    }

    override suspend fun setRsvp(sessionId: Session.Id, rsvp: Session.RSVP) {
        Logger.withTag("KEVINTEST").i { "Setting RSVP" }
        sessionQueries.updateRsvp(rsvp.isAttending.toLong(), sessionId.value) // SQL
    }

    override suspend fun setRsvpSent(sessionId: Session.Id, isSent: Boolean) {
        sessionQueries.updateRsvpSent(isSent.toLong(), sessionId.value)
    }

    override suspend fun setFeedback(sessionId: Session.Id, feedback: Session.Feedback) {
        sessionQueries.updateFeedBack(feedback.rating, feedback.comment, sessionId.value)
    }

    override suspend fun setFeedbackSent(sessionId: Session.Id, isSent: Boolean) {
        sessionQueries.updateFeedBackSent(if (isSent) 1 else 0, sessionId.value)
    }

    override fun allSync(): List<Session> = sessionQueries.allSessions(::sessionFactory).executeAsList()

    override fun findSync(id: Session.Id): Session? = sessionQueries.sessionById(id.value, mapper = ::sessionFactory).executeAsOneOrNull()

    override fun observeAll(): Flow<List<Session>> {
        return sessionQueries.allSessions(::sessionFactory).asFlow().mapToList(Dispatchers.Main)
    }

    override fun doUpsert(entity: Session) {
        sessionQueries.upsert(
            id = entity.id.value,
            title = entity.title,
            description = entity.description,
            startsAt = entity.startsAt,
            endsAt = entity.endsAt,
            serviceSession = entity.isServiceSession.toLong(),
            roomId = entity.room?.value,
            rsvp = entity.rsvp.isAttending.toLong(),
            rsvpSent = entity.rsvp.isSent.toLong(),
            feedbackRating = entity.feedback?.rating,
            feedbackComment = entity.feedback?.comment,
            feedbackSent = entity.feedback?.isSent?.toLong() ?: 0
        )
    }

    override fun doDelete(id: Session.Id) {
        return sessionQueries.deleteById(id.value)
    }

    override fun contains(id: Session.Id): Boolean {
        return sessionQueries.existsById(id.value).executeAsOne().toBoolean()
    }

    private fun sessionFactory(
        id: String,
        title: String,
        description: String?,
        startsAt: Instant,
        endsAt: Instant,
        serviceSession: Long,
        rsvp: Long?,
        rsvpSent: Long,
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
        rsvp = Session.RSVP(rsvp?.toBoolean() ?: false, rsvpSent.toBoolean()),
        feedback = if (feedbackRating != null && feedbackComment != null) {
            Session.Feedback(feedbackRating, feedbackComment, feedbackSent.toBoolean())
        } else {
            null
        },
    )
}
