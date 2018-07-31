package co.touchlab.sessionize.db

import co.touchlab.sessionize.utils.Date
import co.touchlab.sessionize.utils.DateFormatHelper
import co.touchlab.sessionize.utils.SESSIONIZE_DATE_FORMAT
import com.squareup.sqldelight.ColumnAdapter

class DateAdapter : ColumnAdapter<Date, String> {
    val formatter = DateFormatHelper(SESSIONIZE_DATE_FORMAT)
    override fun encode(value: Date) = formatter.format(value)
    override fun decode(databaseValue: String) = formatter.toDate(databaseValue)
}