package co.touchlab.droidcon.domain.service

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime

interface DateTimeService {
    fun now(): Instant

    fun conferenceNow(): LocalDateTime

    fun Instant.toConferenceDateTime(): LocalDateTime

    fun LocalDateTime.fromConferenceDateTime(): Instant
}

fun Instant.toConferenceDateTime(dateTimeService: DateTimeService): LocalDateTime = with(dateTimeService) {
    toConferenceDateTime()
}

fun LocalDateTime.fromConferenceDateTime(dateTimeService: DateTimeService): Instant = with(dateTimeService) {
    fromConferenceDateTime()
}
