package co.touchlab.notepad.utils

import platform.Foundation.*

actual class DateFormatHelper actual constructor(format:String){

    val formatter:NSDateFormatter

    init
    {
        formatter = NSDateFormatter()
        formatter.dateFormat = format
    }

    actual fun toDate(s:String):Date{
        return Date(formatter.dateFromString(s)!!)
    }
}