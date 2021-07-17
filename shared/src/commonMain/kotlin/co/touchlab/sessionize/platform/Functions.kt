package co.touchlab.sessionize.platform

import kotlinx.coroutines.CoroutineDispatcher

internal expect val mainThread: Boolean

internal fun assertMainThread() {
    if (!mainThread)
        throw IllegalStateException("Must be on main thread")
}

internal fun assertNotMainThread() {
    if (mainThread)
        throw IllegalStateException("Must not be on main thread")
}

/**
 * Current time in millis. Like Java's System.currentTimeMillis()
 */
expect fun currentTimeMillis(): Long

/**
 * Generates a unique string for use in tracking this user anonymously
 */
expect fun createUuid(): String

expect fun printThrowable(t:Throwable)

expect fun backgroundDispatcher():CoroutineDispatcher

//This stuff is just till ktor catches up
expect fun simpleGet(url:String):String

expect fun networkDispatcher():CoroutineDispatcher