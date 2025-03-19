package co.touchlab.droidcon.domain.service.impl

import co.touchlab.droidcon.domain.service.DateTimeService
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

class DefaultDateTimeService(private val clock: Clock) : DateTimeService {

    override fun now(): Instant = clock.now()

    override fun conferenceNow(timeZone: TimeZone): LocalDateTime = now().toConferenceDateTime(timeZone)

    override fun Instant.toConferenceDateTime(conferenceTimeZone: TimeZone): LocalDateTime = toLocalDateTime(conferenceTimeZone)

    override fun LocalDateTime.fromConferenceDateTime(conferenceTimeZone: TimeZone): Instant = toInstant(conferenceTimeZone)
}
