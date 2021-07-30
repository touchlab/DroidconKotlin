package co.touchlab.droidcon.android

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import co.touchlab.droidcon.android.service.DateTimeFormatterViewService
import co.touchlab.droidcon.android.service.ParseUrlViewService
import co.touchlab.droidcon.android.service.impl.DefaultDateTimeFormatterViewService
import co.touchlab.droidcon.android.service.impl.DefaultParseUrlViewService
import co.touchlab.droidcon.domain.service.impl.ResourceReader
import co.touchlab.droidcon.initKoin
import co.touchlab.droidcon.util.AssetResourceReader
import org.koin.dsl.module

class MainApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin(
            module {
                single<Context> { this@MainApp }
                single<SharedPreferences> {
                    get<Context>().getSharedPreferences("DROIDCON_SETTINGS", Context.MODE_PRIVATE)
                }

                single<ParseUrlViewService> {
                    DefaultParseUrlViewService()
                }

                single<DateTimeFormatterViewService> {
                    DefaultDateTimeFormatterViewService()
                }
                single<ResourceReader> {
                    AssetResourceReader(get())
                }
            }
        )
    }
}
