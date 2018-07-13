package co.touchlab.notepad.utils

import platform.Foundation.*
import kotlin.math.floor

actual class Date(val iosDate:NSDate) {
    actual fun toLongMillis():Long{
        return floor(iosDate.timeIntervalSince1970).toLong() * 1000L
    }
}