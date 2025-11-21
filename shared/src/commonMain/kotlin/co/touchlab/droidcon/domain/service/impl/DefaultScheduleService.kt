package co.touchlab.droidcon.domain.service.impl

import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.repository.SessionRepository
import co.touchlab.droidcon.domain.service.ConferenceConfigProvider
import co.touchlab.droidcon.domain.service.ScheduleService
import kotlin.time.Instant

class DefaultScheduleService(
    private val sessionRepository: SessionRepository,
    private val conferenceConfigProvider: ConferenceConfigProvider,
) : ScheduleService {

    override suspend fun isInConflict(session: Session): Boolean {
        if (!session.rsvp.isAttending) {
            return false
        }
        val sessionRange = session.startsAt.rangeTo(session.endsAt)
        val conferenceId = conferenceConfigProvider.getConferenceId()
        return sessionRepository.allAttending(conferenceId).any { otherSession ->
            otherSession.id != session.id && sessionRange.intersects(otherSession.startsAt.rangeTo(otherSession.endsAt))
        }
    }

    private fun ClosedRange<Instant>.intersects(otherRange: ClosedRange<Instant>): Boolean =
        start.epochSeconds < otherRange.endInclusive.epochSeconds &&
            endInclusive.epochSeconds > otherRange.start.epochSeconds
}
