package co.touchlab.sessionize.utils

import platform.Foundation.*

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