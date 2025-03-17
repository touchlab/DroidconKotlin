package co.touchlab.droidcon.domain.gateway.impl

import co.touchlab.droidcon.Constants
import co.touchlab.droidcon.domain.composite.ScheduleItem
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.gateway.SessionGateway
import co.touchlab.droidcon.domain.repository.ProfileRepository
import co.touchlab.droidcon.domain.repository.RoomRepository
import co.touchlab.droidcon.domain.repository.SessionRepository
import co.touchlab.droidcon.domain.service.ScheduleService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DefaultSessionGateway(
    private val sessionRepository: SessionRepository,
    private val roomRepository: RoomRepository,
    private val profileRepository: ProfileRepository,
    private val scheduleService: ScheduleService,
) : SessionGateway {

    override fun observeSchedule(): Flow<List<ScheduleItem>> = sessionRepository.observeAll(Constants.conferenceId).map { sessions ->
        sessions.map { session ->
            scheduleItemForSession(session)
        }
    }

    override fun observeAgenda(): Flow<List<ScheduleItem>> = sessionRepository.observeAllAttending(Constants.conferenceId).map { sessions ->
        sessions.map { session ->
            scheduleItemForSession(session)
        }
    }

    override fun observeScheduleItem(id: Session.Id): Flow<ScheduleItem> =
        sessionRepository.observe(id, Constants.conferenceId).map { session ->
            scheduleItemForSession(session)
        }

    private suspend fun scheduleItemForSession(session: Session): ScheduleItem = ScheduleItem(
        session,
        scheduleService.isInConflict(session),
        session.room?.let { roomRepository.find(it, Constants.conferenceId) },
        profileRepository.getSpeakersBySession(session.id, Constants.conferenceId),
    )

    override suspend fun setAttending(session: Session, attending: Boolean) {
        sessionRepository.setRsvp(session.id, Session.RSVP(attending, false), Constants.conferenceId)
    }

    override suspend fun setFeedback(session: Session, feedback: Session.Feedback) {
        sessionRepository.setFeedback(session.id, feedback, Constants.conferenceId)
    }

    override suspend fun getScheduleItem(id: Session.Id): ScheduleItem? =
        sessionRepository.find(id, Constants.conferenceId)?.let { scheduleItemForSession(it) }
}
