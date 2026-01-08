package co.touchlab.droidcon.web.util.formatter

import co.touchlab.droidcon.util.formatter.DateFormatter
import kotlin.getValue
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

class WebDateFormatter : DateFormatter {
    override fun monthWithDay(date: LocalDate): String? = null

    override fun timeOnly(dateTime: LocalDateTime): String? = null

    override fun timeOnlyInterval(fromDateTime: LocalDateTime, toDateTime: LocalDateTime): String = ""
}
