package co.touchlab.droidcon.android

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import co.touchlab.droidcon.android.service.impl.AndroidAnalyticsService
import co.touchlab.droidcon.android.service.impl.DefaultParseUrlViewService
import co.touchlab.droidcon.android.util.NotificationLocalizedStringFactory
import co.touchlab.droidcon.application.service.NotificationSchedulingService
import co.touchlab.droidcon.domain.service.AnalyticsService
import co.touchlab.droidcon.domain.service.AuthenticationService
import co.touchlab.droidcon.domain.service.impl.ResourceReader
import co.touchlab.droidcon.initKoin
import co.touchlab.droidcon.service.ParseUrlViewService
import co.touchlab.droidcon.ui.uiModule
import co.touchlab.droidcon.util.ClasspathResourceReader
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.dsl.module

@OptIn(ExperimentalSettingsApi::class)
class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin(
            module {
                single<Context> { this@MainApp }
                single<Class<out Activity>> { MainActivity::class.java }
                single<SharedPreferences> {
                    get<Context>().getSharedPreferences(
                        "DROIDCON_SETTINGS_2023",
                        Context.MODE_PRIVATE
                    )
                }
                single<ObservableSettings> { SharedPreferencesSettings(delegate = get()) }

                single<ParseUrlViewService> {
                    DefaultParseUrlViewService()
                }

                single<ResourceReader> {
                    ClasspathResourceReader()
                }

                single<NotificationSchedulingService.LocalizedStringFactory> {
                    NotificationLocalizedStringFactory(context = get())
                }

                single<AnalyticsService> {
                    AndroidAnalyticsService(firebaseAnalytics = Firebase.analytics)
                }

                single<AuthenticationService>{
                    FirebaseService()
                }
            } + uiModule
        )
    }
}
