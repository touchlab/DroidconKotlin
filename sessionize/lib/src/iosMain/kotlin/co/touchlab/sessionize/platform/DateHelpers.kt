package co.touchlab.sessionize.platform

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSTimeZone
import platform.Foundation.NSTimeZoneMeta
import platform.Foundation.timeIntervalSince1970
import platform.Foundation.timeZoneForSecondsFromGMT
import kotlin.math.floor

actual class Date(val iosDate: NSDate) {
    actual fun toLongMillis(): Long {
        return floor(iosDate.timeIntervalSince1970).toLong() * 1000L
    }
}

actual class DateFormatHelper actual constructor(format: String) {

    private val formatter: NSDateFormatter = NSDateFormatter().apply { dateFormat = format }

    actual fun setTimeZone(t: String) {
        val symbol = t[t.length-5]
        val hoursStr = t.substring(t.length-4,t.length-2)
        val minutesStr = t.substring(t.length-2)

        var seconds = (hoursStr.toInt()*60*60) + (minutesStr.toInt()*60)
        if(symbol == '-'){
            seconds *= -1
        }

        formatter.timeZone = NSTimeZone.timeZoneForSecondsFromGMT(seconds)
    }
    actual fun toDate(s: String): Date = Date(formatter.dateFromString(s)!!)
    actual fun format(d: Date): String = formatter.stringFromDate(d.iosDate)
}