package co.touchlab.droidcon

import app.cash.sqldelight.ColumnAdapter
import co.touchlab.droidcon.application.gateway.SettingsGateway
import co.touchlab.droidcon.application.gateway.impl.DefaultSettingsGateway
import co.touchlab.droidcon.application.repository.AboutRepository
import co.touchlab.droidcon.application.repository.SettingsRepository
import co.touchlab.droidcon.application.repository.impl.DefaultAboutRepository
import co.touchlab.droidcon.application.repository.impl.DefaultSettingsRepository
import co.touchlab.droidcon.application.service.NotificationSchedulingService
import co.touchlab.droidcon.application.service.impl.DefaultNotificationSchedulingService
import co.touchlab.droidcon.db.ConferenceTable
import co.touchlab.droidcon.db.DroidconDatabase
import co.touchlab.droidcon.db.SessionTable
import co.touchlab.droidcon.db.SponsorGroupTable
import co.touchlab.droidcon.domain.gateway.SessionGateway
import co.touchlab.droidcon.domain.gateway.SponsorGateway
import co.touchlab.droidcon.domain.gateway.impl.DefaultSessionGateway
import co.touchlab.droidcon.domain.gateway.impl.DefaultSponsorGateway
import co.touchlab.droidcon.domain.repository.ConferenceRepository
import co.touchlab.droidcon.domain.repository.ProfileRepository
import co.touchlab.droidcon.domain.repository.RoomRepository
import co.touchlab.droidcon.domain.repository.SessionRepository
import co.touchlab.droidcon.domain.repository.SponsorGroupRepository
import co.touchlab.droidcon.domain.repository.SponsorRepository
import co.touchlab.droidcon.domain.repository.impl.SqlDelightConferenceRepository
import co.touchlab.droidcon.domain.repository.impl.SqlDelightProfileRepository
import co.touchlab.droidcon.domain.repository.impl.SqlDelightRoomRepository
import co.touchlab.droidcon.domain.repository.impl.SqlDelightSessionRepository
import co.touchlab.droidcon.domain.repository.impl.SqlDelightSponsorGroupRepository
import co.touchlab.droidcon.domain.repository.impl.SqlDelightSponsorRepository
import co.touchlab.droidcon.domain.repository.impl.adapter.InstantSqlDelightAdapter
import co.touchlab.droidcon.domain.service.DateTimeService
import co.touchlab.droidcon.domain.service.FeedbackService
import co.touchlab.droidcon.domain.service.ScheduleService
import co.touchlab.droidcon.domain.service.ServerApi
import co.touchlab.droidcon.domain.service.SyncService
import co.touchlab.droidcon.domain.service.UserIdProvider
import co.touchlab.droidcon.domain.service.impl.DefaultApiDataSource
import co.touchlab.droidcon.domain.service.impl.DefaultDateTimeService
import co.touchlab.droidcon.domain.service.impl.DefaultFeedbackService
import co.touchlab.droidcon.domain.service.impl.DefaultScheduleService
import co.touchlab.droidcon.domain.service.impl.DefaultServerApi
import co.touchlab.droidcon.domain.service.impl.DefaultSyncService
import co.touchlab.droidcon.domain.service.impl.DefaultUserIdProvider
import co.touchlab.droidcon.domain.service.impl.json.AboutJsonResourceDataSource
import co.touchlab.droidcon.domain.service.impl.json.JsonResourceReader
import co.touchlab.droidcon.domain.service.impl.json.JsonSeedResourceDataSource
import io.ktor.client.HttpClient
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.serialization.json.Json
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.qualifier
import org.koin.core.scope.Scope
import org.koin.dsl.module

fun initKoin(additionalModules: List<Module>): KoinApplication {
    val koinApplication = startKoin {
        modules(
            additionalModules +
                platformModule +
                coreModule,
        )
    }

    return koinApplication
}

val intToLongAdapter = object : ColumnAdapter<Int, Long> {
    override fun decode(databaseValue: Long): Int = databaseValue.toInt()

    override fun encode(value: Int): Long = value.toLong()
}

val timeZoneAdapter = object : ColumnAdapter<TimeZone, String> {
    override fun decode(databaseValue: String): TimeZone = TimeZone.of(databaseValue)
    override fun encode(value: TimeZone): String = value.id
}

// We defined this adapter but it's not being used automatically by SQLDelight yet
// Will be used when we manually handle the selected flag
val booleanAdapter = object : ColumnAdapter<Boolean, Long> {
    override fun decode(databaseValue: Long): Boolean = databaseValue == 1L
    override fun encode(value: Boolean): Long = if (value) 1L else 0L
}

private val coreModule = module {
    single {
        DroidconDatabase.invoke(
            driver = get(),
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
    }
    single<Clock> { Clock.System }

    single {
        HttpClient(engine = get()) {}
    }

    single {
        Json {
            ignoreUnknownKeys = true
        }
    }

    single {
        JsonResourceReader(
            resourceReader = get(),
            json = get(),
        )
    }

    single<DateTimeService> {
        DefaultDateTimeService(
            clock = get(),
            conferenceTimeZone = Constants.conferenceTimeZone,
        )
    }
    single<ProfileRepository> {
        SqlDelightProfileRepository(
            profileQueries = get<DroidconDatabase>().profileQueries,
            speakerQueries = get<DroidconDatabase>().sessionSpeakerQueries,
            representativeQueries = get<DroidconDatabase>().sponsorRepresentativeQueries,
        )
    }
    single<SessionRepository> {
        SqlDelightSessionRepository(
            dateTimeService = get(),
            sessionQueries = get<DroidconDatabase>().sessionQueries,
        )
    }
    single<RoomRepository> {
        SqlDelightRoomRepository(roomQueries = get<DroidconDatabase>().roomQueries)
    }
    single<SponsorRepository> {
        SqlDelightSponsorRepository(sponsorQueries = get<DroidconDatabase>().sponsorQueries)
    }
    single<SponsorGroupRepository> {
        SqlDelightSponsorGroupRepository(sponsorGroupQueries = get<DroidconDatabase>().sponsorGroupQueries)
    }
    single<ConferenceRepository> {
        SqlDelightConferenceRepository(conferenceQueries = get<DroidconDatabase>().conferenceQueries)
    }
    single<SyncService> {
        DefaultSyncService(
            log = getWith("SyncService"),
            settings = get(),
            dateTimeService = get(),
            profileRepository = get(),
            sessionRepository = get(),
            roomRepository = get(),
            sponsorRepository = get(),
            sponsorGroupRepository = get(),
            seedDataSource = get(qualifier(DefaultSyncService.DataSource.Kind.Seed)),
            apiDataSource = get(qualifier(DefaultSyncService.DataSource.Kind.Api)),
            serverApi = get(),
            db = get(),
        )
    }
    single<DefaultSyncService.DataSource>(qualifier(DefaultSyncService.DataSource.Kind.Api)) {
        DefaultApiDataSource(
            client = get(),
            json = get(),
        )
    }
    single<DefaultSyncService.DataSource>(qualifier(DefaultSyncService.DataSource.Kind.Seed)) {
        JsonSeedResourceDataSource(
            jsonResourceReader = get(),
        )
    }
    single<SessionGateway> {
        DefaultSessionGateway(
            sessionRepository = get(),
            roomRepository = get(),
            profileRepository = get(),
            scheduleService = get(),
        )
    }
    single<ScheduleService> {
        DefaultScheduleService(
            sessionRepository = get(),
        )
    }
    single<SponsorGateway> {
        DefaultSponsorGateway(
            sponsorRepository = get(),
            sponsorGroupRepository = get(),
            profileRepository = get(),
        )
    }
    single<SettingsGateway> {
        DefaultSettingsGateway(
            settingsRepository = get(),
        )
    }
    single<SettingsRepository> {
        DefaultSettingsRepository(
            observableSettings = get(),
        )
    }
    single {
        AboutJsonResourceDataSource(
            jsonResourceReader = get(),
        )
    }
    single<AboutRepository> {
        DefaultAboutRepository(
            aboutJsonResourceDataSource = get(),
        )
    }
    single<NotificationSchedulingService> {
        DefaultNotificationSchedulingService(
            sessionRepository = get(),
            roomRepository = get(),
            settingsRepository = get(),
            notificationService = get(),
            dateTimeService = get(),
            settings = get(),
            json = get(),
            localizedStringFactory = get(),
        )
    }
    single<ServerApi> {
        DefaultServerApi(
            userIdProvider = get(),
            client = get(),
            json = get(),
        )
    }
    single<UserIdProvider> {
        DefaultUserIdProvider(
            observableSettings = get(),
        )
    }
    single<FeedbackService> {
        DefaultFeedbackService(
            sessionGateway = get(),
            settings = get(),
            json = get(),
            clock = get(),
        )
    }
}

internal inline fun <reified T> Scope.getWith(vararg params: Any?): T = get(parameters = { parametersOf(*params) })

expect val platformModule: Module
