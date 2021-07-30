package co.touchlab.droidcon.ios.util.formatter

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toNSDateComponents
import kotlinx.datetime.toNSTimeZone
import platform.Foundation.NSCalendar
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSDateFormatterMediumStyle
import platform.Foundation.NSDateFormatterNoStyle
import platform.Foundation.NSDateFormatterShortStyle
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale

class DateFormatter(
    timeZoner: TimeZone,
) {
    private val monthWithDay: NSDateFormatter by lazy {
        NSDateFormatter().also {
            val dateTemplate = "MMM d"
            it.dateFormat = NSDateFormatter.dateFormatFromTemplate(dateTemplate, 0, NSLocale.currentLocale)!!
            it.timeZone = timeZoner.toNSTimeZone()
        }
    }

    private val timeOnly: NSDateFormatter by lazy {
        NSDateFormatter().also {
            it.dateStyle = NSDateFormatterNoStyle
            it.timeStyle = NSDateFormatterShortStyle
            it.timeZone = timeZoner.toNSTimeZone()
        }
    }

    private val timeOnlyNoPeriod: NSDateFormatter by lazy {
        NSDateFormatter().also {
            val dateTemplate = "hh:mm"
            it.dateFormat = NSDateFormatter.dateFormatFromTemplate(dateTemplate, 0, NSLocale.currentLocale)!!
            it.timeZone = timeZoner.toNSTimeZone()
        }
    }

    fun monthWithDay(date: LocalDate) =
        date.date()?.let { monthWithDay.stringFromDate(it) }

    fun timeOnly(dateTime: LocalDateTime) =
        dateTime.date()?.let { timeOnly.stringFromDate(it) }

    fun timeOnlyInterval(fromDateTime: LocalDateTime, toDateTime: LocalDateTime) = interval(
        fromDateTime.date()?.let { timeOnlyNoPeriod.stringFromDate(it) },
        toDateTime.date()?.let { timeOnly.stringFromDate(it) },
    )

    private fun LocalDate.date() = NSCalendar.currentCalendar.dateFromComponents(toNSDateComponents())

    private fun LocalDateTime.date() = NSCalendar.currentCalendar.dateFromComponents(toNSDateComponents())

    private fun interval(from: String?, to: String?) = listOfNotNull(from, to).joinToString(" – ")
}