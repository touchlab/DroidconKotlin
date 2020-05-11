package co.touchlab.sessionize

import android.app.Application
import android.content.Context
import android.util.Log
import co.touchlab.droidcon.db.DroidconDb
import co.touchlab.sessionize.api.NetworkRepo
import co.touchlab.sessionize.api.SessionizeApiImpl
import co.touchlab.sessionize.platform.AndroidAppContext
import com.google.firebase.analytics.FirebaseAnalytics
import com.russhwolf.settings.AndroidSettings
import com.squareup.sqldelight.android.AndroidSqliteDriver
import kotlinx.coroutines.Dispatchers

class MainApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidAppContext.app = this
        ServiceRegistry.initLambdas(this::loadAsset, { Log.w("MainApp", it) }, {e:Throwable, message:String ->
            Log.e("MainApp", message, e)
        })

        ServiceRegistry.initServiceRegistry(
                AndroidSqliteDriver(DroidconDb.Schema, this, "droidcondb2"),
                AndroidSettings.Factory(this).create("DROIDCON_SETTINGS2"),
                SessionizeApiImpl,
                AnalyticsApiImpl(FirebaseAnalytics.getInstance(this)),
                NotificationsApiImpl(),
                BuildConfig.TIME_ZONE
        )

        AppContext.initAppContext()

        NetworkRepo.sendFeedback()

        @Suppress("ConstantConditionIf")
        if(BuildConfig.FIREBASE_ENABLED) {
            //FirebaseMessageHandler.init()
        }else{
            Log.d("MainApp","Firebase json not found: Firebased Not Enabled")
        }
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
