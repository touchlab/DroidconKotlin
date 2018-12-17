package co.touchlab.sessionize.platform

import com.russhwolf.settings.Settings
import com.squareup.sqldelight.db.SqlDatabase
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

expect fun currentTimeMillis():Long

expect fun <B> backgroundTask(backJob:()-> B, mainJob:(B) -> Unit)

val callbackMap = HashMap<Int, (Any?)->Unit>()
var callbackId = 0

fun callCallback(id:Int, result:Any?){
    callbackMap.remove(id)!!(result)
}

suspend fun <R> backgroundSuspend(backJob:()-> R):R{
    var continuation:Continuation<Any?>? = null

    val callbackIdLocal = callbackId++
    callbackMap.put(callbackIdLocal) {
        continuation!!.resume(it)
    }
    backgroundTask(backJob){
        callCallback(callbackIdLocal, it)
    }

    return suspendCoroutine<Any?> {
        continuation = it
    } as R
}

expect fun backgroundTask(backJob:()->Unit)

expect fun networkBackgroundTask(backJob:()->Unit)

expect fun initSqldelightDatabase():SqlDatabase

expect fun logException(t:Throwable)

expect fun settingsFactory(): Settings.Factory

expect fun createUuid():String
