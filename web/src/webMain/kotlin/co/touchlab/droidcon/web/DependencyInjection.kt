package co.touchlab.droidcon.web

import app.cash.sqldelight.db.SqlDriver
import co.touchlab.droidcon.application.service.NotificationSchedulingService
import co.touchlab.droidcon.db.ConferenceTable
import co.touchlab.droidcon.db.DroidconDatabase
import co.touchlab.droidcon.db.SessionTable
import co.touchlab.droidcon.db.SponsorGroupTable
import co.touchlab.droidcon.domain.repository.impl.SqlDelightDriverFactory
import co.touchlab.droidcon.domain.repository.impl.adapter.InstantSqlDelightAdapter
import co.touchlab.droidcon.domain.service.AnalyticsService
import co.touchlab.droidcon.domain.service.impl.ResourceReader
import co.touchlab.droidcon.initKoin
import co.touchlab.droidcon.intToLongAdapter
import co.touchlab.droidcon.service.JsNotificationService
import co.touchlab.droidcon.service.ParseUrlViewService
import co.touchlab.droidcon.timeZoneAdapter
import co.touchlab.droidcon.ui.uiModule
import co.touchlab.droidcon.util.formatter.DateFormatter
import co.touchlab.droidcon.viewmodel.WaitForLoadedContextModel
import co.touchlab.droidcon.web.service.DefaultParseUrlViewService
import co.touchlab.droidcon.web.util.NotificationLocalizedStringFactory
import co.touchlab.droidcon.web.util.WebResourceReader
import co.touchlab.droidcon.web.util.formatter.WebDateFormatter
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings
import org.koin.core.KoinApplication
import org.koin.dsl.module
import com.russhwolf.settings.observable.makeObservable
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalSettingsApi::class, ExperimentalTime::class)
suspend fun startKoin(): KoinApplication {

    val driver = SqlDelightDriverFactory().createDriver()
    DroidconDatabase.Schema.create(driver).await()

    val db = DroidconDatabase.invoke(
        driver = driver,
        sessionTableAdapter = SessionTable.Adapter(
            startsAtAdapter = InstantSqlDelightAdapter,
            endsAtAdapter = InstantSqlDelightAdapter,
            feedbackRatingAdapter = intToLongAdapter,
        ),
        sponsorGroupTableAdapter = SponsorGroupTable.Adapter(
            intToLongAdapter,
        ),
        conferenceTableAdapter = ConferenceTable.Adapter(
            conferenceTimeZoneAdapter = timeZoneAdapter,
            // Note: selectedAdapter will be added when the adapter is regenerated
        ),
    )

    return initKoin(
        module {
            single<ObservableSettings> {
                val storageSettings: Settings = StorageSettings()
                storageSettings.makeObservable()
            }
            single<ResourceReader> { WebResourceReader() }

            single<DateFormatter> { WebDateFormatter() }

            single<NotificationSchedulingService.LocalizedStringFactory> { NotificationLocalizedStringFactory() }

            single<AnalyticsService> { WebAnalyticsService() }

            single<ParseUrlViewService> { DefaultParseUrlViewService() }

            // Provide JsNotificationService which is required by the JS platform module
            single<JsNotificationService> { JsNotificationService() }

            single<SqlDriver> { driver }
            single<DroidconDatabase> { db }
        } + uiModule
    )
}

@OptIn(ExperimentalWasmJsInterop::class)
@Suppress("unused")
val KoinApplication.waitForLoadedContextModel: WaitForLoadedContextModel
    get() = get()
