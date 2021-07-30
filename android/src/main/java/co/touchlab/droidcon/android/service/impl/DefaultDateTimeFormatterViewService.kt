package co.touchlab.droidcon.android.service.impl

import co.touchlab.droidcon.android.service.DateTimeFormatterViewService
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toInstant
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DefaultDateTimeFormatterViewService: DateTimeFormatterViewService {

    private val shortDateFormat = SimpleDateFormat("MMM d", Locale.getDefault())
    private val minuteHourTimeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault())

    override fun time(time: LocalDateTime): String {
        return minuteHourTimeFormat.format(time.toDate())
    }

    override fun timeRange(start: LocalDateTime, end: LocalDateTime): String {
        return time(start) + " - " + time(end)
    }

    override fun shortDate(date: LocalDate): String {
        return shortDateFormat.format(date.toDate()).uppercase()
    }

    private fun LocalDateTime.toDate(): Date =
        Date(this.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds())

    private fun LocalDate.toDate(): Date =
        Date(this.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds())
}