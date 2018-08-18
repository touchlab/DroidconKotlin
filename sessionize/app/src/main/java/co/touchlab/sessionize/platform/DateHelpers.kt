package co.touchlab.sessionize.platform

import java.text.DateFormat
import java.text.SimpleDateFormat

actual class Date(val date:java.util.Date) {
    actual fun toLongMillis(): Long = date.time
}

actual class DateFormatHelper actual constructor(format: String) {
    val dateFormatter = object : ThreadLocal<DateFormat>(){
        override fun initialValue(): DateFormat = SimpleDateFormat(format)
    }

    actual fun toDate(s: String): Date = Date(dateFormatter.get()!!.parse(s))

    actual fun format(d: Date): String = dateFormatter.get()!!.format(d.date)
}