package co.touchlab.sessionize.platform

import com.russhwolf.settings.Settings
import com.squareup.sqldelight.db.SqlDatabase
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

expect fun currentTimeMillis():Long

expect fun <B> backgroundTask(backJob:()-> B, mainJob:(B) -> Unit)

suspend fun <B> backgroundSupend(backJob:()-> B):B{
    var continuation:Continuation<B>? = null
    backgroundTask(backJob){
        continuation!!.resume(it)
    }
    return suspendCoroutine<B> {
        continuation = it
    }
}

expect fun backgroundTask(backJob:()->Unit)

expect fun networkBackgroundTask(backJob:()->Unit)

expect fun initSqldelightDatabase():SqlDatabase

expect fun simpleGet(url:String):String

expect fun logException(t:Throwable)

expect fun settingsFactory(): Settings.Factory

expect fun createUuid():String
