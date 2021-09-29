package co.touchlab.droidcon.ios.util

import kotlinx.datetime.LocalDateTime

val LocalDateTime.startOfMinute: LocalDateTime
    get() = LocalDateTime(year, month, dayOfMonth, hour, minute)