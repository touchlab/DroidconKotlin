package co.touchlab.droidcon.android.util

import kotlinx.datetime.LocalDateTime

val LocalDateTime.startOfMinute: LocalDateTime
    get() = LocalDateTime(year, month, dayOfMonth, hour, minute)