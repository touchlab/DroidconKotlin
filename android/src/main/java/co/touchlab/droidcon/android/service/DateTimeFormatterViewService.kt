package co.touchlab.droidcon.android.service

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

interface DateTimeFormatterViewService {

    fun time(time: LocalDateTime): String

    fun timeRange(start: LocalDateTime, end: LocalDateTime): String

    fun shortDate(date: LocalDate): String
}