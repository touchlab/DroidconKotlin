package co.touchlab.droidcon.android

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import co.touchlab.droidcon.AppInfo
import co.touchlab.droidcon.BuildConfig
import co.touchlab.droidcon.initKoin
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
                single<AppInfo> { AndroidAppInfo }
            }
        )
    }
}

object AndroidAppInfo : AppInfo {
    override val appId: String = BuildConfig.APPLICATION_ID
}
