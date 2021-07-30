package co.touchlab.droidcon

import co.touchlab.droidcon.db.DroidconDatabase
import co.touchlab.droidcon.db.SessionTable
import co.touchlab.droidcon.domain.repository.impl.adapter.InstantSqlDelightAdapter
import co.touchlab.droidcon.domain.service.DateTimeService
import co.touchlab.droidcon.domain.service.impl.DefaultDateTimeService
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope
import org.koin.dsl.module

fun initKoin(appModule: Module): KoinApplication {
    val koinApplication = startKoin {
        modules(
            appModule,
            platformModule,
            coreModule
        )
    }

    return koinApplication
}

private val coreModule = module {
    single {
        DroidconDatabase(
            driver = get(),
            sessionTableAdapter = SessionTable.Adapter(
                startsAtAdapter = InstantSqlDelightAdapter,
                endsAtAdapter = InstantSqlDelightAdapter,
            ),
        )
    }

    single<Clock> { Clock.System }
    // TODO: Get this from the conference timezone.
    single { TimeZone.currentSystemDefault() }
    single<DateTimeService> { DefaultDateTimeService(get(), get()) }
}

internal inline fun <reified T> Scope.getWith(vararg params: Any?): T {
    return get(parameters = { parametersOf(*params) })
}

expect val platformModule: Module
