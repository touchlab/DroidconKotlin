package co.touchlab.notepad.utils

val SESSIONIZE_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"

expect class DateFormatHelper(format:String){
    fun toDate(s:String):Date
}