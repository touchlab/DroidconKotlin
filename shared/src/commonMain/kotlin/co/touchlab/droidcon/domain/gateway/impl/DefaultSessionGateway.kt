package co.touchlab.droidcon.domain.gateway.impl

import co.touchlab.droidcon.domain.composite.ScheduleItem
import co.touchlab.droidcon.domain.composite.SponsorGroupWithSponsors
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.gateway.SessionGateway
import co.touchlab.droidcon.domain.repository.ProfileRepository
import co.touchlab.droidcon.domain.repository.RoomRepository
import co.touchlab.droidcon.domain.repository.SessionRepository
import co.touchlab.droidcon.domain.service.ConferenceConfigProvider
import co.touchlab.droidcon.domain.service.ScheduleService
import co.touchlab.kermit.Logger
import kotlin.collections.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class DefaultSessionGateway(
    private val sessionRepository: SessionRepository,
    private val roomRepository: RoomRepository,
    private val profileRepository: ProfileRepository,
    private val scheduleService: ScheduleService,
    private val conferenceConfigProvider: ConferenceConfigProvider,
) : SessionGateway {

    private val log = Logger.withTag("DefaultSessionGateway")

    override fun observeSchedule(): Flow<List<ScheduleItem>> = conferenceConfigProvider.observeChanges().flatMapLatest { conf ->
        log.i { "observeSchedule: Conference: ${conf?.id}" }
        if (conf == null) {
            flowOf(emptyList())
        } else {
            sessionRepository.observeAll(conf.id)
        }
    }.map { sessions ->
        log.i { "observeSchedule: Map: $sessions" }

        sessions.map { session ->
            scheduleItemForSession(session)
        }
    }

    override fun observeAgenda(): Flow<List<ScheduleItem>> = conferenceConfigProvider.observeChanges().flatMapLatest { conf ->
        if (conf == null) {
            flowOf(emptyList())
        } else {
            sessionRepository.observeAllAttending(conf.id)
        }
    }.map { sessions ->
        sessions.map { session ->
            scheduleItemForSession(session)
        }
    }

    override fun observeScheduleItem(id: Session.Id): Flow<ScheduleItem> {
        return conferenceConfigProvider.observeChanges()
            .filterNotNull()
            .map { conference -> conference.id }
            .distinctUntilChanged()
            .flatMapLatest { confId ->
                sessionRepository.observe(id, confId).map { session ->
                    scheduleItemForSession(session)
                }
            }
    }

    private suspend fun scheduleItemForSession(session: Session): ScheduleItem {
        val conferenceId = conferenceConfigProvider.getConferenceId()

        return ScheduleItem(
            session,
            scheduleService.isInConflict(session),
            session.room?.let { roomRepository.find(it, conferenceId) },
            profileRepository.getSpeakersBySession(session.id, conferenceId),
        )
    }

    override suspend fun setAttending(session: Session, attending: Boolean) {
        val conferenceId = conferenceConfigProvider.getConferenceId()
        sessionRepository.setRsvp(session.id, Session.RSVP(attending, false), conferenceId)
    }

    override suspend fun setFeedback(session: Session, feedback: Session.Feedback) {
        val conferenceId = conferenceConfigProvider.getConferenceId()
        sessionRepository.setFeedback(session.id, feedback, conferenceId)
    }

    override suspend fun getScheduleItem(id: Session.Id): ScheduleItem? {
        val conferenceId = conferenceConfigProvider.getConferenceId()
        return sessionRepository.find(id, conferenceId)?.let { scheduleItemForSession(it) }
    }
}
