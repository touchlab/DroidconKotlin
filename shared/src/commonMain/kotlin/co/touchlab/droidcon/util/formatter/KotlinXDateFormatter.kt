package co.touchlab.droidcon.util.formatter

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

/**
 * Shared [DateFormatter] for all platforms using kotlinx-datetime only (no java.text / NSDateFormatter / JS stubs).
 * Schedule data uses conference-local [LocalDate] / [LocalDateTime]; we format those fields directly.
 */
class KotlinXDateFormatter : DateFormatter {

    override fun monthWithDay(date: LocalDate): String {
        val m = date.month.name.take(3).uppercase()
        return "$m ${date.day}"
    }

    override fun timeOnly(dateTime: LocalDateTime): String = formatTime12h(dateTime)

    override fun timeOnlyInterval(fromDateTime: LocalDateTime, toDateTime: LocalDateTime): String =
        "${timeOnly(fromDateTime)} - ${timeOnly(toDateTime)}"

    private fun formatTime12h(dateTime: LocalDateTime): String {
        val h = dateTime.hour
        val min = dateTime.minute
        val h12 = when {
            h == 0 -> 12
            h > 12 -> h - 12
            else -> h
        }
        val suffix = if (h < 12) "AM" else "PM"
        return "$h12:${min.toString().padStart(2, '0')} $suffix"
    }
}
