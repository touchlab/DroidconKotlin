package co.touchlab.droidcon.domain.service.impl

import co.touchlab.droidcon.domain.service.DateTimeService
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

class DefaultDateTimeService(
    private val clock: Clock,
    private val deviceTimeZone: TimeZone,
    private val conferenceTimeZone: TimeZone,
): DateTimeService {

    override fun now(): Instant = clock.now()

    override fun conferenceNow(): LocalDateTime = now().toConferenceDateTime()

    override fun deviceNow(): LocalDateTime = now().toDeviceDateTime()

    override fun Instant.toConferenceDateTime(): LocalDateTime {
        return toLocalDateTime(conferenceTimeZone)
    }

    override fun Instant.toDeviceDateTime(): LocalDateTime {
        return toLocalDateTime(deviceTimeZone)
    }

    override fun LocalDateTime.fromConferenceDateTime(): Instant {
        return toInstant(conferenceTimeZone)
    }

    override fun LocalDateTime.fromDeviceDateTime(): Instant {
        return toInstant(deviceTimeZone)
    }
}