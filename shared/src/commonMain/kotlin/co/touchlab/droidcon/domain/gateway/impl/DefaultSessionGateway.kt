package co.touchlab.droidcon.domain.gateway.impl

import co.touchlab.droidcon.domain.composite.ScheduleItem
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.gateway.SessionGateway
import co.touchlab.droidcon.domain.repository.ProfileRepository
import co.touchlab.droidcon.domain.repository.RoomRepository
import co.touchlab.droidcon.domain.repository.SessionRepository
import co.touchlab.droidcon.domain.service.ConferenceConfigProvider
import co.touchlab.droidcon.domain.service.ScheduleService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DefaultSessionGateway(
    private val sessionRepository: SessionRepository,
    private val roomRepository: RoomRepository,
    private val profileRepository: ProfileRepository,
    private val scheduleService: ScheduleService,
    private val conferenceConfigProvider: ConferenceConfigProvider,
) : SessionGateway {

    private val conferenceId get() = conferenceConfigProvider.getConferenceId()

    override fun observeSchedule(): Flow<List<ScheduleItem>> = sessionRepository.observeAll(conferenceId).map { sessions ->
        sessions.map { session ->
            scheduleItemForSession(session)
        }
    }

    override fun observeAgenda(): Flow<List<ScheduleItem>> = sessionRepository.observeAllAttending(conferenceId).map { sessions ->
        sessions.map { session ->
            scheduleItemForSession(session)
        }
    }

    override fun observeScheduleItem(id: Session.Id): Flow<ScheduleItem> = sessionRepository.observe(id, conferenceId).map { session ->
        scheduleItemForSession(session)
    }

    private suspend fun scheduleItemForSession(session: Session): ScheduleItem = ScheduleItem(
        session,
        scheduleService.isInConflict(session),
        session.room?.let { roomRepository.find(it, conferenceId) },
        profileRepository.getSpeakersBySession(session.id, conferenceId),
    )

    override suspend fun setAttending(session: Session, attending: Boolean) {
        sessionRepository.setRsvp(session.id, Session.RSVP(attending, false), conferenceId)
    }

    override suspend fun setFeedback(session: Session, feedback: Session.Feedback) {
        sessionRepository.setFeedback(session.id, feedback, conferenceId)
    }

    override suspend fun getScheduleItem(id: Session.Id): ScheduleItem? =
        sessionRepository.find(id, conferenceId)?.let { scheduleItemForSession(it) }
}
