package co.touchlab.droidcon.android

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import co.touchlab.droidcon.android.service.impl.DefaultParseUrlViewService
import co.touchlab.droidcon.domain.service.impl.ComposeResourceReader
import co.touchlab.droidcon.domain.service.impl.ResourceReader
import co.touchlab.droidcon.initKoin
import co.touchlab.droidcon.service.ParseUrlViewService
import co.touchlab.droidcon.ui.uiModule
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SharedPreferencesSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.component.KoinComponent
import org.koin.dsl.module

@OptIn(ExperimentalSettingsApi::class)
class MainApp :
    Application(),
    KoinComponent {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        initKoin(
            module {
                single<Context> { this@MainApp }
                single<Class<out Activity>> { MainActivity::class.java }
                single<SharedPreferences> {
                    get<Context>().getSharedPreferences("DROIDCON_SETTINGS_2024", Context.MODE_PRIVATE)
                }
                single<ObservableSettings> { SharedPreferencesSettings(delegate = get()) }

                single<ParseUrlViewService> {
                    DefaultParseUrlViewService()
                }

                single<ResourceReader> {
                    ComposeResourceReader()
                }
            } + uiModule,
        )
    }
}
