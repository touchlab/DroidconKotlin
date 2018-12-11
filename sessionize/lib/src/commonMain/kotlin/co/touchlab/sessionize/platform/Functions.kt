package co.touchlab.sessionize.platform

import com.russhwolf.settings.Settings
import com.squareup.sqldelight.db.SqlDatabase

expect fun currentTimeMillis():Long

expect fun <B> backgroundTask(backJob:()-> B, mainJob:(B) -> Unit)

expect fun backgroundTask(backJob:()->Unit)

expect fun networkBackgroundTask(backJob:()->Unit)

expect fun initSqldelightDatabase():SqlDatabase

expect fun simpleGet(url:String):String

expect fun logException(t:Throwable)

expect fun settingsFactory(): Settings.Factory

expect fun createUuid():String
