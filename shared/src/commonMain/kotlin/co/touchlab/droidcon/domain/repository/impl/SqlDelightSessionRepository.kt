package co.touchlab.droidcon.domain.repository.impl

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrNull
import co.touchlab.droidcon.db.SessionQueries
import co.touchlab.droidcon.domain.entity.Room
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.repository.SessionRepository
import co.touchlab.droidcon.domain.service.DateTimeService
import co.touchlab.kermit.Logger
import kotlin.time.Instant
import kotlinx.coroutines.CoroutineScope
import kotlin.time.Instant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.plus

class SqlDelightSessionRepository(private val dateTimeService: DateTimeService, private val sessionQueries: SessionQueries) :
    BaseRepository<Session.Id, Session>(),
    SessionRepository {

    private val log = Logger.withTag("SqlDelightSessionRepository")
    val lifecycleScope = CoroutineScope(SupervisorJob()) + Dispatchers.Main

    override fun observe(id: Session.Id, conferenceId: Long): Flow<Session> =
        sessionQueries.sessionById(id.value, conferenceId, ::sessionFactory).asFlow().mapToOne(Dispatchers.Main)

    suspend fun sessionById(id: Session.Id, conferenceId: Long): Session? =
        sessionQueries.sessionById(id.value, conferenceId, ::sessionFactory).awaitAsOneOrNull()

    override fun observeOrNull(id: Session.Id, conferenceId: Long): Flow<Session?> =
        sessionQueries.sessionById(id.value, conferenceId, ::sessionFactory).asFlow().mapToOneOrNull(Dispatchers.Main)

    override fun observeAllAttending(conferenceId: Long): Flow<List<Session>> =
        sessionQueries.attendingSessions(conferenceId, ::sessionFactory).asFlow().mapToList(Dispatchers.Main)

    override suspend fun allAttending(conferenceId: Long): List<Session> = observeAllAttending(conferenceId).first()

    override suspend fun setRsvp(sessionId: Session.Id, rsvp: Session.RSVP, conferenceId: Long) {
        sessionQueries.updateRsvp(rsvp.isAttending.toLong(), sessionId.value, conferenceId)
    }

    override suspend fun setRsvpSent(sessionId: Session.Id, isSent: Boolean, conferenceId: Long) {
        sessionQueries.updateRsvpSent(isSent.toLong(), sessionId.value, conferenceId)
    }

    override suspend fun setFeedback(sessionId: Session.Id, feedback: Session.Feedback, conferenceId: Long) {
        sessionQueries.updateFeedBack(feedback.rating, feedback.comment, sessionId.value, conferenceId)
    }

    override suspend fun setFeedbackSent(sessionId: Session.Id, isSent: Boolean, conferenceId: Long) {
        sessionQueries.updateFeedBackSent(if (isSent) 1 else 0, sessionId.value, conferenceId)
    }

    override suspend fun allSync(conferenceId: Long): List<Session> =
        sessionQueries.allSessions(conferenceId, ::sessionFactory).awaitAsList()

    override suspend fun findSync(id: Session.Id, conferenceId: Long): Session? =
        sessionQueries.sessionById(id.value, conferenceId, mapper = ::sessionFactory).awaitAsOneOrNull()

    override fun observeAll(conferenceId: Long): Flow<List<Session>> =
        sessionQueries.allSessions(conferenceId, ::sessionFactory).asFlow().mapToList(Dispatchers.Main)

    override suspend fun doUpsert(entity: Session, conferenceId: Long) {
        sessionQueries.upsert(
            id = entity.id.value,
            conferenceId = conferenceId,
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
            feedbackSent = entity.feedback?.isSent?.toLong() ?: 0,
        )
    }

    override suspend fun doDelete(id: Session.Id, conferenceId: Long) {
        sessionQueries.deleteById(id.value, conferenceId)
    }

    override suspend fun contains(id: Session.Id, conferenceId: Long): Boolean =
        sessionQueries.existsById(id.value, conferenceId).awaitAsOne().toBoolean()

    private fun sessionFactory(
        id: String,
        conferenceId: Long,
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
