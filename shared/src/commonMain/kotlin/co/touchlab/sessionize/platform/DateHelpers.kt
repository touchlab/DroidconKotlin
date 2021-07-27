package co.touchlab.sessionize.platform

expect class Date {
    fun toLongMillis(): Long
}

expect class DateFormatHelper(format: String, timeZone: String) {
    fun toConferenceDate(s: String): Date
    fun toLocalDate(s: String): Date
    fun formatConferenceTZ(d: Date): String
    fun formatLocalTZ(d: Date): String
}

//`from***` methods