package co.touchlab.sessionize.platform

import co.touchlab.sessionize.ServiceRegistry
import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSTimeZone
import platform.Foundation.defaultTimeZone
import platform.Foundation.timeIntervalSince1970
import platform.Foundation.timeZoneForSecondsFromGMT
import kotlin.math.floor

actual class Date(val iosDate: NSDate) {
    actual fun toLongMillis(): Long {
        return floor(iosDate.timeIntervalSince1970).toLong() * 1000L
    }
}

actual class DateFormatHelper actual constructor(format: String) {

    private val dateFormatterConference: NSDateFormatter = NSDateFormatter().apply {

        val t = ServiceRegistry.timeZone
        val symbol = t[t.length-5]
        val hoursStr = t.substring(t.length-4,t.length-2)
        val minutesStr = t.substring(t.length-2)

        var seconds = (hoursStr.toInt()*60*60) + (minutesStr.toInt()*60)
        if(symbol == '-'){
            seconds *= -1
        }
        this.timeZone = NSTimeZone.timeZoneForSecondsFromGMT(seconds.toLong())
        this.dateFormat = format
    }

    private val dateFormatterLocal: NSDateFormatter = NSDateFormatter().apply {
        this.timeZone = NSTimeZone.defaultTimeZone
        this.dateFormat = format

    }

    actual fun toConferenceDate(s: String): Date  = Date(dateFormatterConference.dateFromString(s)!!)
    actual fun toLocalDate(s: String): Date  = Date(dateFormatterLocal.dateFromString(s)!!)
    actual fun formatConferenceTZ(d: Date): String = dateFormatterConference.stringFromDate(d.iosDate)
    actual fun formatLocalTZ(d: Date): String = dateFormatterLocal.stringFromDate(d.iosDate)
}
