package co.touchlab.sessionize

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import co.touchlab.droidcon.db.DroidconDb
import co.touchlab.sessionize.api.AnalyticsApi
import co.touchlab.sessionize.api.NetworkRepo
import co.touchlab.sessionize.api.NotificationsApi
import co.touchlab.sessionize.event.EventViewModel
import co.touchlab.sessionize.feedback.FeedbackManager
import co.touchlab.sessionize.schedule.ScheduleViewModel
import co.touchlab.sessionize.settings.SettingsViewModel
import co.touchlab.sessionize.speaker.SpeakerViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.russhwolf.settings.AndroidSettings
import com.russhwolf.settings.Settings
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.qualifier.named
import org.koin.dsl.module

class MainApp : Application(), KoinComponent {
    override fun onCreate() {
        super.onCreate()

        initKoin(
            module {
                single<Context> { this@MainApp }
                single<SharedPreferences> {
                    get<Context>().getSharedPreferences("DROIDCON_SETTINGS2", Context.MODE_PRIVATE)
                }
                single<SqlDriver> {
                    AndroidSqliteDriver(
                        DroidconDb.Schema,
                        this@MainApp,
                        "droidcondb2"
                    )
                }
                single<Settings> {
                    AndroidSettings(get())
                }
                single<AnalyticsApi> { AnalyticsApiImpl(FirebaseAnalytics.getInstance(this@MainApp)) }
                single { FeedbackManager(get(), get(), get(qualifier = named("softExceptionCallback")), this@MainApp) }
                single<NotificationsApi> { NotificationsApiImpl(get(), get(qualifier = named("timeZone")), this@MainApp) }
                single(qualifier = named("timeZone")) { BuildConfig.TIME_ZONE }
                single<StaticFileLoader>(qualifier = named("staticFile")) { this@MainApp::loadAsset }
                single<ClLogCallback>(qualifier = named("clLog")) { { Log.w("MainApp", it) } }
                single<SoftExceptionCallback>(qualifier = named("softExceptionCallback")) {
                    { e: Throwable, message: String ->
                        Log.e("MainApp", message, e)
                    }
                }
                viewModel { SettingsViewModel(get(), get()) }
                viewModel { (sessionId:String) -> EventViewModel(sessionId, get(), get(), get()) }
                viewModel { (allEvents:Boolean) -> ScheduleViewModel(allEvents, get(), get(qualifier = named("timeZone"))) }
                viewModel { (userId:String) -> SpeakerViewModel(userId, get()) }
            }
        )

        AppContext.initAppContext(get(), get(), get(), get())

        get<NetworkRepo>().sendFeedback()

        @Suppress("ConstantConditionIf")
        if (BuildConfig.FIREBASE_ENABLED) {
            //FirebaseMessageHandler.init()
        } else {
            Log.d("MainApp", "Firebase json not found: Firebased Not Enabled")
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        get<NotificationsApi>().deinitializeNotifications()
    }

    private fun loadAsset(fileName: String, filePrefix: String): String? =
        assets.open("$fileName.$filePrefix", Context.MODE_PRIVATE)
            .bufferedReader()
            .use { it.readText() }
}
