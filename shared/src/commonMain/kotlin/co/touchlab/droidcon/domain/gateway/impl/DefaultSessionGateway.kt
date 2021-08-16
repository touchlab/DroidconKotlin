package co.touchlab.droidcon.domain.gateway.impl

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
): SessionGateway {

    override fun observeSchedule(): Flow<List<ScheduleItem>> {
        return sessionRepository.observeAll().map { sessions ->
            sessions.map { session ->
                scheduleItemForSession(session)
            }
        }
    }

    override fun observeAgenda(): Flow<List<ScheduleItem>> {
        return sessionRepository.observeAllAttending().map { sessions ->
            sessions.map { session ->
                scheduleItemForSession(session)
            }
        }
    }

    override fun observeScheduleItem(id: Session.Id): Flow<ScheduleItem> {
        return sessionRepository.observe(id).map { session ->
            scheduleItemForSession(session)
        }
    }

    private suspend fun scheduleItemForSession(session: Session): ScheduleItem =
        ScheduleItem(
            session,
            scheduleService.isInConflict(session),
            session.room?.let { roomRepository.get(it) },
            profileRepository.getSpeakersBySession(session.id),
        )

    override suspend fun setAttending(session: Session, attending: Boolean) {
        sessionRepository.setRsvp(session.id, Session.RSVP(attending, false))
    }

    override suspend fun setFeedback(session: Session, feedback: Session.Feedback) {
        sessionRepository.setFeedback(session.id, feedback)
    }
}