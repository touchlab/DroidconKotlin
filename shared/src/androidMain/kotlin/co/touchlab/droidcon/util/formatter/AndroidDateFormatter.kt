package co.touchlab.droidcon.util.formatter

import co.touchlab.droidcon.Constants.conferenceTimeZone
import co.touchlab.droidcon.domain.service.DateTimeService
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.atTime

class AndroidDateFormatter(private val dateTimeService: DateTimeService) : DateFormatter {

    // TODOKPG - May not need to set timezone. Java date has no TZ
    private val shortDateFormat =
        SimpleDateFormat("MMM d", Locale.getDefault()).apply { timeZone = java.util.TimeZone.getTimeZone(conferenceTimeZone.id) }
    private val minuteHourTimeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault())
        .apply { timeZone = java.util.TimeZone.getTimeZone(conferenceTimeZone.id) }

    override fun monthWithDay(date: LocalDate): String {
        return shortDateFormat.format(
            Date(with(dateTimeService) { date.atTime(0, 0).fromConferenceDateTime() }.toEpochMilliseconds())
        ).uppercase()
    }

    override fun timeOnly(dateTime: LocalDateTime): String? {
        return minuteHourTimeFormat.format(
            Date(with(dateTimeService) { dateTime.fromConferenceDateTime() }.toEpochMilliseconds())
        )
    }

    override fun timeOnlyInterval(fromDateTime: LocalDateTime, toDateTime: LocalDateTime): String {
        return timeOnly(fromDateTime) + " - " + timeOnly(toDateTime)
    }
}
