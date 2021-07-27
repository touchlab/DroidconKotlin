package co.touchlab.sessionize

import co.touchlab.sessionize.api.AnalyticsApi
import co.touchlab.sessionize.api.NotificationsApi
import co.touchlab.sessionize.platform.defaultDriver
import co.touchlab.sessionize.platform.defaultSettings
import co.touchlabYeah.sessionize.EventViewModel
import com.russhwolf.settings.Settings
import com.squareup.sqldelight.db.SqlDriver
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val platformModule: Module
    get() = module {
        single<SqlDriver> {
            defaultDriver()
        }
        single<Settings> {
            defaultSettings()
        }
    }

@Suppress("unused")
fun injectModule(
    analyticsApi: AnalyticsApi,
    timeZone: String,
    notificationsApi: NotificationsApi,
    staticFileLoader: StaticFileLoader,
    ciLogCallback: ClLogCallback,
    softExceptionCallback: SoftExceptionCallback
) {
    val passedModule = module {
        single<AnalyticsApi> { analyticsApi }
        single<NotificationsApi> { notificationsApi }
        single(qualifier = named("timeZone")) { timeZone }
        single<StaticFileLoader>(qualifier = named("staticFile")) { staticFileLoader }
        single<ClLogCallback>(qualifier = named("clLog")) { ciLogCallback }
        single<SoftExceptionCallback>(qualifier = named("softExceptionCallback")) { softExceptionCallback }
    }

    initKoin(passedModule)
}