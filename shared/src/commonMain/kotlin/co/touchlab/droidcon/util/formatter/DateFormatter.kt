package co.touchlab.droidcon.util.formatter

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

interface DateFormatter {

    fun monthWithDay(date: LocalDate): String?
    fun timeOnly(dateTime: LocalDateTime): String?
    fun timeOnlyInterval(fromDateTime: LocalDateTime, toDateTime: LocalDateTime): String
}
