@file:Suppress("ktlint:standard:filename")

package co.touchlab.droidcon.util

import kotlinx.datetime.LocalDateTime

val LocalDateTime.startOfMinute: LocalDateTime
    get() = LocalDateTime(year, month, dayOfMonth, hour, minute)
