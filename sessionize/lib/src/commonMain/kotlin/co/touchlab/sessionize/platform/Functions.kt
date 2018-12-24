package co.touchlab.sessionize.platform

import com.russhwolf.settings.Settings
import com.squareup.sqldelight.db.SqlDatabase
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Suspends current execution while backJob runs in a background thread. When
 * multithreaded coroutines arrive this will be pretty useless, but good for now.
 */
internal suspend fun <R> backgroundSuspend(backJob:()-> R):R{

    val continuationContainer = ContinuationContainer(null)

    backgroundTask(backJob){
        continuationContainer.continuation!!.resume(it)
    }

    return suspendCoroutine<Any?> {
        continuationContainer.continuation = it
    } as R
}

internal expect fun <B> backgroundTask(backJob:()-> B, mainJob:(B) -> Unit)

internal expect fun <B> backToFront(b:()->B, job: (B) -> Unit)

internal expect val mainThread:Boolean

internal fun assertMainThread(){
    if(!mainThread)
        throw IllegalStateException("Must be on main thread")
}

private class ContinuationContainer(var continuation:Continuation<Any?>?)

/**
 * Current time in millis. Like Java's System.currentTimeMillis()
 */
expect fun currentTimeMillis():Long

/**
 * Create SqlDatabase for Sqldelight
 */
internal expect fun initSqldelightDatabase():SqlDatabase

expect fun logException(t:Throwable)

/**
 * Create shared settings instance
 */
expect fun settingsFactory(): Settings.Factory

/**
 * Generates a unique string for use in tracking this user anonymously
 */
expect fun createUuid():String
