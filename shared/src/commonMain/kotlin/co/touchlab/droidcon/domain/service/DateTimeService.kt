package co.touchlab.droidcon.domain.service

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone

interface DateTimeService {
    fun now(): Instant

    fun conferenceNow(): LocalDateTime

    fun deviceNow(): LocalDateTime

    fun Instant.toConferenceDateTime(): LocalDateTime

    fun Instant.toDeviceDateTime(): LocalDateTime

    fun LocalDateTime.fromConferenceDateTime(): Instant

    fun LocalDateTime.fromDeviceDateTime(): Instant
}

fun Instant.toConferenceDateTime(dateTimeService: DateTimeService): LocalDateTime = with(dateTimeService) {
    toConferenceDateTime()
}

fun Instant.toDeviceDateTime(dateTimeService: DateTimeService): LocalDateTime = with(dateTimeService) {
    toDeviceDateTime()
}

fun LocalDateTime.fromConferenceDateTime(dateTimeService: DateTimeService): Instant = with(dateTimeService) {
    fromConferenceDateTime()
}

fun LocalDateTime.fromDeviceDateTime(dateTimeService: DateTimeService): Instant = with(dateTimeService) {
    fromDeviceDateTime()
}