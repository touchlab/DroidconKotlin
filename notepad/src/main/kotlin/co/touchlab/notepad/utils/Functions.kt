package co.touchlab.notepad.utils

import co.touchlab.multiplatform.architecture.db.sqlite.NativeOpenHelperFactory
import com.russhwolf.settings.Settings

expect fun currentTimeMillis():Long

expect fun <B> backgroundTask(backJob:()-> B, mainJob:(B) -> Unit)

expect fun backgroundTask(backJob:()->Unit)

expect fun networkBackgroundTask(backJob:()->Unit)

expect fun initContext():NativeOpenHelperFactory

expect fun sleepThread(millis:Long)

expect fun <T> goFreeze(a:T):T

expect fun simpleGet(url:String):String

expect fun logException(t:Throwable)

expect fun settingsFactory(): Settings.Factory

expect fun logEvent(name: String, vararg params: String)