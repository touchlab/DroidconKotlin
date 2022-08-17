package co.touchlab.droidcon.util.formatter

import co.touchlab.droidcon.Constants.conferenceTimeZone
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toJavaLocalDateTime
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AndroidDateFormatter: DateFormatter {

    //TODOKPG - May not need to set timezone. Java date has no TZ
    private val shortDateFormat =
        SimpleDateFormat("MMM d", Locale.getDefault()).apply { timeZone = java.util.TimeZone.getTimeZone(conferenceTimeZone.id) }
    private val minuteHourTimeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault())
        .apply { timeZone = java.util.TimeZone.getTimeZone(conferenceTimeZone.id) }

    override fun monthWithDay(date: LocalDate): String? {
        return shortDateFormat.format(date.toConferenceDate()).uppercase()
    }

    override fun timeOnly(dateTime: LocalDateTime): String? {
        return minuteHourTimeFormat.format(dateTime.toConferenceDateTime())
    }

    override fun timeOnlyInterval(fromDateTime: LocalDateTime, toDateTime: LocalDateTime): String {
        return timeOnly(fromDateTime) + " - " + timeOnly(toDateTime)
    }

    private fun LocalDate.toConferenceDate(): Date =
        Date(this.atStartOfDayIn(conferenceTimeZone).toEpochMilliseconds())

    private fun LocalDateTime.toConferenceDateTime(): Date =
        Date(this.toInstant(conferenceTimeZone).toEpochMilliseconds())
}
