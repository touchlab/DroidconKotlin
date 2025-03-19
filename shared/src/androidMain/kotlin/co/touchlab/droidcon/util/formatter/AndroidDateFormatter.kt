package co.touchlab.droidcon.util.formatter

import co.touchlab.droidcon.domain.service.ConferenceConfigProvider
import co.touchlab.droidcon.domain.service.DateTimeService
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.atTime

class AndroidDateFormatter(private val dateTimeService: DateTimeService, private val conferenceConfigProvider: ConferenceConfigProvider) :
    DateFormatter {

    // Get timezone from ConferenceConfigProvider
    private val conferenceTimeZone get() = conferenceConfigProvider.getConferenceTimeZone()

    // Create formatters as properties to ensure they use the current conference timezone
    private val shortDateFormat
        get() = SimpleDateFormat("MMM d", Locale.getDefault()).apply {
            timeZone = java.util.TimeZone.getTimeZone(conferenceTimeZone.id)
        }

    private val minuteHourTimeFormat
        get() = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault())
            .apply { timeZone = java.util.TimeZone.getTimeZone(conferenceTimeZone.id) }

    override fun monthWithDay(date: LocalDate): String = shortDateFormat.format(
        Date(with(dateTimeService) { date.atTime(0, 0).fromConferenceDateTime(conferenceTimeZone) }.toEpochMilliseconds()),
    ).uppercase()

    override fun timeOnly(dateTime: LocalDateTime): String? = minuteHourTimeFormat.format(
        Date(with(dateTimeService) { dateTime.fromConferenceDateTime(conferenceTimeZone) }.toEpochMilliseconds()),
    )

    override fun timeOnlyInterval(fromDateTime: LocalDateTime, toDateTime: LocalDateTime): String =
        timeOnly(fromDateTime) + " - " + timeOnly(toDateTime)
}
