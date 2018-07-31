package co.touchlab.sessionize.utils

actual class Date(val date:java.util.Date) {
    actual fun toLongMillis(): Long = date.time
}