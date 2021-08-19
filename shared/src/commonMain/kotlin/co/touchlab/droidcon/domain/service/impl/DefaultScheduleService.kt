package co.touchlab.droidcon.domain.service.impl

import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.repository.SessionRepository
import co.touchlab.droidcon.domain.service.ScheduleService
import kotlinx.datetime.Instant

class DefaultScheduleService(
    private val sessionRepository: SessionRepository,
): ScheduleService {

    override suspend fun isInConflict(session: Session): Boolean {
        if (session.rsvp?.isAttending != true) {
            return false
        }
        val sessionRange = session.startsAt.rangeTo(session.endsAt)
        return sessionRepository.allAttending().any { otherSession ->
            otherSession.id != session.id && sessionRange.intersects(otherSession.startsAt.rangeTo(otherSession.endsAt))
        }
    }

    private fun ClosedRange<Instant>.intersects(otherRange: ClosedRange<Instant>): Boolean {
        return start.epochSeconds < otherRange.endInclusive.epochSeconds
            && endInclusive.epochSeconds > otherRange.start.epochSeconds
    }
}