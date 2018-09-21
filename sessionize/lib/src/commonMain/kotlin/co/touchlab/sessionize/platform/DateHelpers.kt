package co.touchlab.sessionize.platform

expect class Date {
    fun toLongMillis():Long
}

expect class DateFormatHelper(format:String){
    fun toDate(s:String):Date
    fun format(d:Date):String
}