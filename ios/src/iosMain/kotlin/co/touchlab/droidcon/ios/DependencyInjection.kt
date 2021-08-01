package co.touchlab.droidcon.ios

import co.touchlab.droidcon.application.gateway.SettingsGateway
import co.touchlab.droidcon.composite.Url
import co.touchlab.droidcon.domain.composite.ScheduleItem
import co.touchlab.droidcon.domain.entity.Profile
import co.touchlab.droidcon.domain.entity.Room
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.gateway.SessionGateway
import co.touchlab.droidcon.domain.service.SyncService
import co.touchlab.droidcon.domain.service.impl.ResourceReader
import co.touchlab.droidcon.initKoin
import co.touchlab.droidcon.ios.util.formatter.DateFormatter
import co.touchlab.droidcon.ios.viewmodel.AboutViewModel
import co.touchlab.droidcon.ios.viewmodel.AgendaViewModel
import co.touchlab.droidcon.ios.viewmodel.ScheduleViewModel
import co.touchlab.droidcon.ios.viewmodel.SessionBlockViewModel
import co.touchlab.droidcon.ios.viewmodel.SessionDayViewModel
import co.touchlab.droidcon.ios.viewmodel.SessionDetailViewModel
import co.touchlab.droidcon.ios.viewmodel.SessionListItemViewModel
import co.touchlab.droidcon.ios.viewmodel.SettingsViewModel
import co.touchlab.droidcon.ios.viewmodel.SpeakerDetailViewModel
import co.touchlab.droidcon.ios.viewmodel.SpeakerListItemViewModel
import co.touchlab.droidcon.util.BundleResourceReader
import com.russhwolf.settings.AppleSettings
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.brightify.hyperdrive.multiplatformx.BaseViewModel
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.dsl.module
import platform.Foundation.NSBundle
import platform.Foundation.NSUserDefaults
import kotlin.time.ExperimentalTime
import kotlin.time.days
import kotlin.time.hours

@OptIn(ExperimentalTime::class)
fun initKoinIos(
    userDefaults: NSUserDefaults,
): KoinApplication = initKoin(
    module {
        single<ObservableSettings> { AppleSettings(userDefaults) }
        single<ResourceReader> { BundleResourceReader(NSBundle.mainBundle) }

        single<SettingsGateway> {
            object: SettingsGateway {
                private var settings = co.touchlab.droidcon.application.composite.Settings(
                    isFeedbackEnabled = true,
                    isRemindersEnabled = true,
                )

                override fun settings(): StateFlow<co.touchlab.droidcon.application.composite.Settings> {
                    return MutableStateFlow(settings)
                }

                override suspend fun setFeedbackEnabled(enabled: Boolean) {
                    settings = settings.copy(isFeedbackEnabled = enabled)
                }

                override suspend fun setRemindersEnabled(enabled: Boolean) {
                    settings = settings.copy(isRemindersEnabled = enabled)
                }
            }
        }

        single { DateFormatter(get()) }

        // MARK: View model factories.
        factory { ApplicationViewModel(get(), get(), get(), get()) }

        factory { ScheduleViewModel.Factory(get(), get(), get(), get()) }
        factory { AgendaViewModel.Factory(get(), get(), get(), get()) }
        factory { SessionBlockViewModel.Factory(get(), get()) }
        factory { SessionDayViewModel.Factory(get(), get(), get()) }
        factory { SessionListItemViewModel.Factory(get(), get()) }

        factory { SessionDetailViewModel.Factory(get(), get(), get(), get(), get()) }
        factory { SpeakerListItemViewModel.Factory() }

        factory { SpeakerDetailViewModel.Factory() }

        factory { SettingsViewModel.Factory(get(), get()) }
        factory { AboutViewModel.Factory() }
    }
)

class ApplicationViewModel(
    scheduleFactory: ScheduleViewModel.Factory,
    agendaFactory: AgendaViewModel.Factory,
    settingsFactory: SettingsViewModel.Factory,
    private val syncService: SyncService,
): BaseViewModel() {
    val schedule by managed(scheduleFactory.create())
    val agenda by managed(agendaFactory.create())
    val settings by managed(settingsFactory.create())

    override suspend fun whileAttached() {
        syncService.runSynchronization()
    }
}

val Koin.applicationViewModel: ApplicationViewModel
    get() = get()