package co.touchlab.droidcon.android

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import co.touchlab.droidcon.android.service.DateTimeFormatterViewService
import co.touchlab.droidcon.android.service.ParseUrlViewService
import co.touchlab.droidcon.android.service.impl.DefaultDateTimeFormatterViewService
import co.touchlab.droidcon.android.service.impl.DefaultParseUrlViewService
import co.touchlab.droidcon.android.util.NotificationLocalizedStringFactory
import co.touchlab.droidcon.application.service.NotificationSchedulingService
import co.touchlab.droidcon.domain.service.impl.ResourceReader
import co.touchlab.droidcon.initKoin
import co.touchlab.droidcon.util.AssetResourceReader
import co.touchlab.droidcon.util.ClasspathResourceReader
import com.russhwolf.settings.AndroidSettings
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import org.koin.dsl.module

@OptIn(ExperimentalSettingsApi::class)
class MainApp: Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin(
            module {
                single<Context> { this@MainApp }
                single<Class<out Activity>> { MainActivity::class.java }
                single<SharedPreferences> {
                    get<Context>().getSharedPreferences("DROIDCON_SETTINGS", Context.MODE_PRIVATE)
                }
                single<ObservableSettings> { AndroidSettings(get()) }

                single<ParseUrlViewService> {
                    DefaultParseUrlViewService()
                }

                single<DateTimeFormatterViewService> {
                    DefaultDateTimeFormatterViewService()
                }
                single<ResourceReader> {
                    ClasspathResourceReader()
                }

                single<NotificationSchedulingService.LocalizedStringFactory> {
                    NotificationLocalizedStringFactory(get())
                }
            }
        )
    }
}
