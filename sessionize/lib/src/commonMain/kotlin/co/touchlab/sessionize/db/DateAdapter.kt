package co.touchlab.sessionize.db

import co.touchlab.sessionize.platform.Date
import co.touchlab.sessionize.platform.DateFormatHelper
import com.squareup.sqldelight.ColumnAdapter

const val SESSIONIZE_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ"

class DateAdapter(timeZone:String) : ColumnAdapter<Date, String> {
    val formatter = DateFormatHelper(SESSIONIZE_DATE_FORMAT).apply {
        this.setTimeZone(timeZone)
    }
    override fun encode(value: Date) = formatter.format(value)
    override fun decode(databaseValue: String) = formatter.toDate(databaseValue)
}