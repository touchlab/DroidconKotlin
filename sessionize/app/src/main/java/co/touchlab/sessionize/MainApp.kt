package co.touchlab.sessionize

import android.app.Application
import android.content.Context
import android.util.Log
import co.touchlab.droidcon.db.Database
import co.touchlab.sessionize.platform.AndroidAppContext
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import com.crashlytics.android.answers.CustomEvent
import com.russhwolf.settings.PlatformSettings
import com.squareup.sqldelight.android.AndroidSqliteDriver
import kotlinx.coroutines.*

class MainApp :Application(){
    override fun onCreate() {
        super.onCreate()
        AndroidAppContext.app = this
        Fabric.with(this, Answers())
        Fabric.with(this, Crashlytics())

        ServiceRegistry.dbDriver = AndroidSqliteDriver(Database.Schema, this, "droidcondb")
        ServiceRegistry.coroutinesDispatcher = Dispatchers.Main
        ServiceRegistry.appSettings = PlatformSettings.Factory(this).create("DROIDCON_SETTINGS")

        AppContext.initPlatformClient ({filePrefix, fileType ->
            loadAsset("${filePrefix}.${fileType}")},
                {name: String, params: Map<String, Any> ->

                    val event = CustomEvent(name)
                    for (key in params.keys) {
                        val obj = params.get(key)
                        when(obj){
                            is String -> event.putCustomAttribute(key, obj)
                            is Number -> event.putCustomAttribute(key, obj)
                            else -> {
                                throw IllegalArgumentException("Don't know what this is $key/$obj")
                            }
                        }
                    }

                    Answers.getInstance().logCustom(event)
                },
                { Log.w("MainApp", it) }
                )
    }

    private fun loadAsset(name:String) = assets
            .open(name, Context.MODE_PRIVATE)
            .bufferedReader()
            .use { it.readText() }
}