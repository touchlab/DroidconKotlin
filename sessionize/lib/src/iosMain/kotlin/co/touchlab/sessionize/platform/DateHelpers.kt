package co.touchlab.sessionize.platform

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.timeIntervalSince1970
import kotlin.math.floor

actual class Date(val iosDate:NSDate) {
    actual fun toLongMillis():Long{
        return floor(iosDate.timeIntervalSince1970).toLong() * 1000L
    }
}

actual class DateFormatHelper actual constructor(format:String){

    val formatter:NSDateFormatter

    init
    {
        formatter = NSDateFormatter()
        formatter.dateFormat = format
    }

    actual fun toDate(s:String):Date = Date(formatter.dateFromString(s)!!)
    actual fun format(d:Date):String = formatter.stringFromDate(d.iosDate)
}