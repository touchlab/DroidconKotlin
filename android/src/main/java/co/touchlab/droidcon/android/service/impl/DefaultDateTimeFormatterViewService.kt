package co.touchlab.droidcon.android.service.impl

import co.touchlab.droidcon.android.service.DateTimeFormatterViewService
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toJavaInstant
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DefaultDateTimeFormatterViewService(private val conferenceTimeZone: TimeZone) : DateTimeFormatterViewService {

    // TODOKPG - May not need to set timezone. Java date has no TZ
    private val shortDateFormat = SimpleDateFormat("MMM d", Locale.getDefault()).apply { timeZone = java.util.TimeZone.getTimeZone(conferenceTimeZone.id) }
    private val minuteHourTimeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault()).apply { timeZone = java.util.TimeZone.getTimeZone(conferenceTimeZone.id) }

    override fun time(time: Instant): String {
        return minuteHourTimeFormat.format(Date.from(time.toJavaInstant())) // TODOKPG - Converting tz-less datetime to a Java Date with device tz
    }

    override fun timeRange(start: Instant, end: Instant): String {
        return time(start) + " - " + time(end)
    }

    override fun shortDate(date: LocalDate): String {
        return shortDateFormat.format(date.toConferenceDate()).uppercase()
    }

    private fun LocalDate.toConferenceDate(): Date =
        Date(this.atStartOfDayIn(conferenceTimeZone).toEpochMilliseconds())
}
