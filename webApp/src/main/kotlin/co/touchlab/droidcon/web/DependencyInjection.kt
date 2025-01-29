package co.touchlab.droidcon.web

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.analytics.analytics
import dev.gitlive.firebase.initialize
import co.touchlab.droidcon.application.service.NotificationSchedulingService
import co.touchlab.droidcon.domain.service.AnalyticsService
import co.touchlab.droidcon.domain.service.impl.ResourceReader
import co.touchlab.droidcon.initKoin
import co.touchlab.droidcon.service.ParseUrlViewService
import co.touchlab.droidcon.ui.uiModule
import co.touchlab.droidcon.util.formatter.DateFormatter
import co.touchlab.droidcon.viewmodel.ApplicationViewModel
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.StorageSettings
import com.russhwolf.settings.observable.makeObservable
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.dsl.module

private val app = Firebase.initialize(
    options = FirebaseOptions(
        apiKey = "AIzaSyCkD5DH2rUJ8aZuJzANpIFj0AVuCNik1l0", // Take from Common
        projectId = "droidcon-148cc", // Take from common
        applicationId = "1:606665771229:web:c1f0f09aa42abc12",
    ),
)

private val firebaseAnalytics = Firebase.analytics(app)

@OptIn(ExperimentalSettingsApi::class)
fun initKoinWeb(): KoinApplication = initKoin(
    module {
        single<ObservableSettings> { StorageSettings().makeObservable() }
        single<ParseUrlViewService> { DefaultParseUrlViewService() }
        single<ResourceReader> { DefaultResourceReader() }
        single<NotificationSchedulingService.LocalizedStringFactory> { NotificationLocalizedStringFactory(context = get()) }
        single<AnalyticsService> { DefaultAnalyticsService(firebaseAnalytics = Firebase.analytics) }
    } + uiModule,
)

val Koin.applicationViewModel: ApplicationViewModel
    get() = get()
