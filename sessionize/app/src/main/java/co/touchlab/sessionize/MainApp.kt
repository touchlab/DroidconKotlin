package co.touchlab.sessionize

import android.app.Application
import android.content.Context
import android.util.Log
import co.touchlab.droidcon.db.DroidconDb
import co.touchlab.sessionize.api.AnalyticsApi
import co.touchlab.sessionize.api.SessionizeApiImpl
import co.touchlab.sessionize.platform.AndroidAppContext
import co.touchlab.sessionize.platform.MainConcurrent
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import com.russhwolf.settings.AndroidSettings
import com.squareup.sqldelight.android.AndroidSqliteDriver
import io.fabric.sdk.android.Fabric
import kotlinx.coroutines.Dispatchers

class MainApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidAppContext.app = this
        Fabric.with(this, Answers())
        Fabric.with(this, Crashlytics())

        ServiceRegistry.initLambdas(this::loadAsset) { Log.w("MainApp", it) }

        ServiceRegistry.initServiceRegistry(
                AndroidSqliteDriver(DroidconDb.Schema, this, "droidcondb"),
                Dispatchers.Main,
                AndroidSettings.Factory(this).create("DROIDCON_SETTINGS"),
                MainConcurrent,
                SessionizeApiImpl,
                AnalyticsApiImpl(),
                NotificationsApiImpl(),
                BuildConfig.TIME_ZONE
        )

        AppContext.initAppContext()

        FirebaseMessageHandler.initFirebaseApp(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        ServiceRegistry.notificationsApi.deinitializeNotifications()
    }

    private fun loadAsset(fileName: String, filePrefix: String): String? =
            assets.open("$fileName.$filePrefix", Context.MODE_PRIVATE)
                    .bufferedReader()
                    .use { it.readText() }
}