package co.touchlab.sessionize

import android.app.Application
import android.content.Context
import android.util.Log
import co.touchlab.droidcon.db.Database
import co.touchlab.sessionize.platform.AndroidAppContext
import co.touchlab.sessionize.platform.MainConcurrent
import co.touchlab.sessionize.api.SessionizeApiImpl
import co.touchlab.sessionize.api.AnalyticsApi
import co.touchlab.sessionize.api.NotificationsApi
import co.touchlab.sessionize.platform.cancelLocalNotificationOnPlatform
import co.touchlab.sessionize.platform.createLocalNotificationOnPlatform
import co.touchlab.sessionize.platform.deinitializeNotificationsOnPlatform
import co.touchlab.sessionize.platform.initializeNotificationsOnPlatform
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

        ServiceRegistry.initLambdas({filePrefix, fileType -> loadAsset("${filePrefix}.${fileType}")},
                { Log.w("MainApp", it) })

        ServiceRegistry.initServiceRegistry(AndroidSqliteDriver(Database.Schema, this, "droidcondb"), Dispatchers.Main,
                PlatformSettings.Factory(this).create("DROIDCON_SETTINGS"), MainConcurrent, SessionizeApiImpl,
                object: AnalyticsApi{
                    override fun logEvent(name: String, params: Map<String, Any>) {
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
                    }
                },
                NotificationsApiImpl(),
                BuildConfig.TIME_ZONE
        )

        AppContext.initAppContext ()

        AppContext.dataLoad()
        ServiceRegistry.notificationsApi.initializeNotifications()
    }

    override fun onTerminate() {
        super.onTerminate()
        ServiceRegistry.notificationsApi.deinitializeNotifications()
    }

    private fun loadAsset(name:String) = assets
            .open(name, Context.MODE_PRIVATE)
            .bufferedReader()
            .use { it.readText() }



    class NotificationsApiImpl : NotificationsApi {

        override fun createLocalNotification(title:String, message:String, timeInMS:Long, notificationId: Int) {
            createLocalNotificationOnPlatform(title,message,timeInMS,notificationId)
        }

        override fun cancelLocalNotification(notificationId: Int) {
            cancelLocalNotificationOnPlatform(notificationId)
        }

        override fun initializeNotifications() {
            initializeNotificationsOnPlatform()
        }

        override fun deinitializeNotifications() {
            deinitializeNotificationsOnPlatform()
        }

    }
}