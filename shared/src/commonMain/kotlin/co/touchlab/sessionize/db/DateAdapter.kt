package co.touchlab.sessionize.db

import co.touchlab.sessionize.platform.Date
import co.touchlab.sessionize.platform.DateFormatHelper
import com.squareup.sqldelight.ColumnAdapter

const val SESSIONIZE_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"

class DateAdapter : ColumnAdapter<Date, String> {
    val formatter = DateFormatHelper(SESSIONIZE_DATE_FORMAT)
    override fun encode(value: Date) = formatter.formatConferenceTZ(value)
    override fun decode(databaseValue: String) = formatter.toConferenceDate(databaseValue)
}