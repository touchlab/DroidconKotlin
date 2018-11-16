package co.touchlab.sessionize.platform

import com.russhwolf.settings.Settings
import com.squareup.sqldelight.db.SqlDatabase
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

expect fun currentTimeMillis():Long

expect fun <B> backgroundTask(backJob:()-> B, mainJob:(B) -> Unit)

var cont2:Continuation<Any?>? = null
val callbackMap = HashMap<Int, (Any?)->Unit>()
var callbackId = 0

fun callCallback(id:Int, result:Any?){
    callbackMap.get(id)!!(result)
}

suspend fun backgroundSupend(backJob:()-> Any?):Any?{
    var continuation:Continuation<Any?>? = null
    println("fa 1")
    val callbackIdLocal = callbackId++
    callbackMap.put(callbackIdLocal) {
        continuation!!.resume(it)
    }
    backgroundTask(backJob){
        callCallback(callbackIdLocal, it)
    }
    println("fa 2")
    return suspendCoroutine<Any?> {
        println("fa 5")
        continuation = it
    }
}

var cont:Continuation<String>? = null

suspend fun backgroundSupendLight(backJob:()-> Unit): String{
    println("fa 1")
    backgroundTask(backJob)
    println("fa 2")
    return suspendCoroutine<String> {
        println("fa 5")
        cont = it
    }
}

expect fun backgroundTask(backJob:()->Unit)

expect fun networkBackgroundTask(backJob:()->Unit)

expect fun initSqldelightDatabase():SqlDatabase

expect fun simpleGet(url:String):String

expect fun logException(t:Throwable)

expect fun settingsFactory(): Settings.Factory

expect fun createUuid():String
