package co.touchlab.droidcon.android.service

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

interface DateTimeFormatterViewService {

    fun time(time: Instant): String

    fun timeRange(start: Instant, end: Instant): String

    fun shortDate(date: LocalDate): String
}