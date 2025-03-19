package co.touchlab.droidcon.domain.service

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone

interface DateTimeService {
    fun now(): Instant

    fun conferenceNow(timeZone: TimeZone): LocalDateTime

    fun Instant.toConferenceDateTime(conferenceTimeZone: TimeZone): LocalDateTime

    fun LocalDateTime.fromConferenceDateTime(conferenceTimeZone: TimeZone): Instant
}

fun Instant.toConferenceDateTime(dateTimeService: DateTimeService, conferenceTimeZone: TimeZone): LocalDateTime = with(dateTimeService) {
    toConferenceDateTime(conferenceTimeZone)
}

fun LocalDateTime.fromConferenceDateTime(dateTimeService: DateTimeService, conferenceTimeZone: TimeZone): Instant = with(dateTimeService) {
    fromConferenceDateTime(conferenceTimeZone)
}
