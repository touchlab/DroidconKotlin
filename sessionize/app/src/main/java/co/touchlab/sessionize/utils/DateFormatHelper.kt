package co.touchlab.sessionize.utils

import java.text.DateFormat
import java.text.SimpleDateFormat

actual class DateFormatHelper actual constructor(format: String) {
    val dateFormatter = object : ThreadLocal<DateFormat>(){
        override fun initialValue(): DateFormat = SimpleDateFormat(format)
    }

    actual fun toDate(s: String): Date = Date(dateFormatter.get().parse(s))

    actual fun format(d: Date): String = dateFormatter.get().format(d.date)
}