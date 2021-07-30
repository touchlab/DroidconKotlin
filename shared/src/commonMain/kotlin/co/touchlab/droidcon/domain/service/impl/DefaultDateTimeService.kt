package co.touchlab.droidcon.domain.service.impl

import co.touchlab.droidcon.domain.service.DateTimeService
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class DefaultDateTimeService(
    private val clock: Clock,
    private val timeZone: TimeZone,
): DateTimeService {
    override fun now(): LocalDateTime =
        clock.now().toLocalDateTime(timeZone)
}