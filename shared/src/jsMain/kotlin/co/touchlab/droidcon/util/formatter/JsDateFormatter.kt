package co.touchlab.droidcon.util.formatter

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

class JsDateFormatter : DateFormatter {

    override fun monthWithDay(date: LocalDate): String? = null

    override fun timeOnly(dateTime: LocalDateTime): String? = null

    override fun timeOnlyInterval(fromDateTime: LocalDateTime, toDateTime: LocalDateTime): String = ""
}
