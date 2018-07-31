package co.touchlab.sessionize.utils

val SESSIONIZE_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"

expect class DateFormatHelper(format:String){
    fun toDate(s:String):Date
    fun format(d:Date):String
}