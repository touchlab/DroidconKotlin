package co.touchlab.droidcon.ios

import co.touchlab.droidcon.application.gateway.SettingsGateway
import co.touchlab.droidcon.composite.Url
import co.touchlab.droidcon.domain.composite.ScheduleItem
import co.touchlab.droidcon.domain.entity.Profile
import co.touchlab.droidcon.domain.entity.Room
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.gateway.SessionGateway
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
import com.russhwolf.settings.AppleSettings
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
import platform.Foundation.NSUserDefaults
import kotlin.time.ExperimentalTime
import kotlin.time.days
import kotlin.time.hours

@OptIn(ExperimentalTime::class)
fun initKoinIos(
    userDefaults: NSUserDefaults,
): KoinApplication = initKoin(
    module {
        single<Settings> { AppleSettings(userDefaults) }

        single<SessionGateway> {
            object: SessionGateway {
                private val items = listOf(
                    ScheduleItem(
                        session = Session(
                            id = Session.Id("juli-session"),
                            title = "Juli Session",
                            description = "Best session",
                            startsAt = Clock.System.now() - 4.hours,
                            endsAt = Clock.System.now() - 2.hours,
                            isServiceSession = false,
                            room = Room.Id(1),
                            isAttending = true,
                            feedback = null,
                        ),
                        isInConflict = true,
                        room = Room(
                            id = Room.Id(1),
                            name = "Track 1 - Blue Room",
                        ),
                        speakers = listOf(
                            Profile(
                                id = Profile.Id("juli"),
                                fullName = "Juli a Tabi",
                                bio = null,
                                tagLine = null,
                                profilePicture = null,
                                twitter = null,
                                linkedIn = null,
                                website = null,
                            )
                        )
                    ),
                    ScheduleItem(
                        session = Session(
                            id = Session.Id("juli-session-2"),
                            title = "Juli Session 2",
                            description = "Best session",
                            startsAt = Clock.System.now(),
                            endsAt = Clock.System.now() + 2.hours,
                            isServiceSession = false,
                            room = Room.Id(2),
                            isAttending = true,
                            feedback = null,
                        ),
                        isInConflict = true,
                        room = Room(
                            id = Room.Id(2),
                            name = "Track 2 - Blue Room",
                        ),
                        speakers = listOf(
                            Profile(
                                id = Profile.Id("juli"),
                                fullName = "Juli a Tabi",
                                bio = null,
                                tagLine = null,
                                profilePicture = null,
                                twitter = null,
                                linkedIn = null,
                                website = null,
                            )
                        )
                    ),
                    ScheduleItem(
                        session = Session(
                            id = Session.Id("juli-session-3"),
                            title = "Juli Session 3",
                            description = "Best session",
                            startsAt = Clock.System.now(),
                            endsAt = Clock.System.now() + 2.hours,
                            isServiceSession = false,
                            room = Room.Id(3),
                            isAttending = true,
                            feedback = null,
                        ),
                        isInConflict = true,
                        room = Room(
                            id = Room.Id(2),
                            name = "Track 3 - Blue Room",
                        ),
                        speakers = listOf(
                            Profile(
                                id = Profile.Id("juli"),
                                fullName = "Juli a Tabi",
                                bio = null,
                                tagLine = null,
                                profilePicture = null,
                                twitter = null,
                                linkedIn = null,
                                website = null,
                            ),
                            Profile(
                                id = Profile.Id("ble"),
                                fullName = "Ble Blue",
                                bio = "Yes I am indeed very important.",
                                tagLine = "The most not important person in the company.",
                                profilePicture = Url("https://images.pexels.com/photos/1933873/pexels-photo-1933873.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=150&w=160"),
                                twitter = null,
                                linkedIn = null,
                                website = null,
                            ),
                            Profile(
                                id = Profile.Id("blue"),
                                fullName = "Bla Blue",
                                bio = "Yes I am indeed not very important.",
                                tagLine = "The most not important person in the company.",
                                profilePicture = Url("https://images.pexels.com/photos/2690323/pexels-photo-2690323.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=150&w=160"),
                                twitter = null,
                                linkedIn = null,
                                website = null,
                            ),
                        )
                    ),
                    ScheduleItem(
                        session = Session(
                            id = Session.Id("juli-session-4"),
                            title = "Juli Session 4",
                            description = "Eh session",
                            startsAt = Clock.System.now() + 1.days,
                            endsAt = Clock.System.now() + 1.days + 2.hours,
                            isServiceSession = false,
                            room = Room.Id(2),
                            isAttending = true,
                            feedback = null,
                        ),
                        isInConflict = false,
                        room = Room(
                            id = Room.Id(4),
                            name = "Track 4 - Red Room",
                        ),
                        speakers = listOf(
                            Profile(
                                id = Profile.Id("juli"),
                                fullName = "Juli a Tabi",
                                bio = null,
                                tagLine = null,
                                profilePicture = null,
                                twitter = null,
                                linkedIn = null,
                                website = null,
                            )
                        )
                    ),
                )

                override fun observeSchedule(): Flow<List<ScheduleItem>> {
                    return flowOf(items)
                }

                override fun observeAgenda(): Flow<List<ScheduleItem>> {
                    return flowOf(items.filter { it.session.isAttending })
                }

                override fun observeScheduleItem(id: Session.Id): Flow<ScheduleItem> {
                    return flowOf(items.single { it.session.id == id })
                }
            }
        }

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
        factory { ApplicationViewModel(get(), get(), get()) }

        factory { ScheduleViewModel.Factory(get(), get(), get()) }
        factory { AgendaViewModel.Factory(get(), get(), get()) }
        factory { SessionBlockViewModel.Factory(get(), get(), get()) }
        factory { SessionDayViewModel.Factory(get(), get(), get()) }
        factory { SessionListItemViewModel.Factory(get(), get()) }

        factory { SessionDetailViewModel.Factory(get(), get(), get(), get(), get(), get()) }
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
): BaseViewModel() {
    val schedule by managed(scheduleFactory.create())
    val agenda by managed(agendaFactory.create())
    val settings by managed(settingsFactory.create())
}

val Koin.applicationViewModel: ApplicationViewModel
    get() = get()