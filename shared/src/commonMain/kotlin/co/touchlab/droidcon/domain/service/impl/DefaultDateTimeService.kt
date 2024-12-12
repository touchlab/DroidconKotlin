package co.touchlab.droidcon.domain.service.impl

import co.touchlab.droidcon.domain.service.DateTimeService
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

class DefaultDateTimeService(private val clock: Clock, private val conferenceTimeZone: TimeZone) : DateTimeService {

    override fun now(): Instant = clock.now()

    override fun conferenceNow(): LocalDateTime = now().toConferenceDateTime()

    override fun Instant.toConferenceDateTime(): LocalDateTime = toLocalDateTime(conferenceTimeZone)

    override fun LocalDateTime.fromConferenceDateTime(): Instant = toInstant(conferenceTimeZone)
}
