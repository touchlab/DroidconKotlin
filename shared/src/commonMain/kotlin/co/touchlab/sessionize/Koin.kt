package co.touchlab.sessionize

import co.touchlab.droidcon.db.DroidconDb
import co.touchlab.droidcon.db.Session
import co.touchlab.kermit.Kermit
import co.touchlab.sessionize.api.AnalyticsApi
import co.touchlab.sessionize.api.NetworkRepo
import co.touchlab.sessionize.api.SessionizeApi
import co.touchlab.sessionize.api.SessionizeApiImpl
import co.touchlab.sessionize.db.DateAdapter
import co.touchlab.sessionize.db.SessionizeDbHelper
import co.touchlab.sessionize.file.FileRepo
import co.touchlab.sessionize.platform.NotificationsModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock
import org.koin.core.KoinApplication
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
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
    single<SessionizeApi> { SessionizeApiImpl(get(), get()) }
    single {
        val timeZone: String = get(qualifier = named("timeZone"))
        DroidconDb(
            get(), Session.Adapter(
                startsAtAdapter = DateAdapter(timeZone = timeZone),
                endsAtAdapter = DateAdapter(timeZone = timeZone)
            )
        )
    }
    single(qualifier = named("background")) { Dispatchers.Default }
    single { Dispatchers.Default }
    single { SessionizeDbHelper(get(), get()) }
    single { FeedbackModel(get(), get(), get()) }
    single { NetworkRepo(get(), get(), get(), get()) }
    single { NotificationsModel(get(), get(), get()) }
    single { FileRepo(get()) }
}

internal val KoinComponent.backgroundDispatcher: CoroutineDispatcher
    get() = get(qualifier = named("background"))

internal val KoinComponent.softExceptionCallback: SoftExceptionCallback
    get() = get(qualifier = named("softExceptionCallback"))

internal val KoinComponent.clLogCallback: ClLogCallback
    get() = get(qualifier = named("clLog"))

internal val KoinComponent.staticFileLoader: StaticFileLoader
    get() = get(qualifier = named("staticFile"))

internal val KoinComponent.analyticsApi: AnalyticsApi
    get() = get()

internal val KoinComponent.timeZone: String
    get() = get(qualifier = named("timeZone"))

internal inline fun <reified T> Scope.getWith(vararg params: Any?): T {
    return get(parameters = { parametersOf(*params) })
}

expect val platformModule: Module