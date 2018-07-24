package co.touchlab.notepad.utils

actual class Date(val date:java.util.Date) {
    actual fun toLongMillis(): Long = date.time
}