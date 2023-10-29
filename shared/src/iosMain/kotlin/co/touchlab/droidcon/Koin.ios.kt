package co.touchlab.droidcon

import app.cash.sqldelight.db.SqlDriver
import co.touchlab.droidcon.application.service.NotificationService
import co.touchlab.droidcon.domain.repository.impl.SqlDelightDriverFactory
import co.touchlab.droidcon.service.IOSNotificationService
import co.touchlab.droidcon.util.AppChecker
import co.touchlab.kermit.Logger
import co.touchlab.kermit.NSLogWriter
import co.touchlab.kermit.StaticConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ObjCClass
import kotlinx.cinterop.ObjCProtocol
import kotlinx.cinterop.getOriginalKotlinClass
import org.koin.core.Koin
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.Qualifier
import org.koin.dsl.module

@BetaInteropApi
actual val platformModule = module {
    single<SqlDriver> { SqlDelightDriverFactory().createDriver() }

    single<HttpClientEngine> {
        Darwin.create {}
    }

    single<NotificationService> {
        IOSNotificationService(
            log = getWith("IOSNotificationService")
        )
    }

    val baseKermit = Logger(config = StaticConfig(logWriterList = listOf(NSLogWriter())), tag = "Droidcon")
    factory { (tag: String?) -> if (tag != null) baseKermit.withTag(tag) else baseKermit }

    single { AppChecker }
}

@BetaInteropApi
fun Koin.get(objCClass: ObjCClass, qualifier: Qualifier?, parameter: Any): Any {
    val kClazz = requireNotNull(getOriginalKotlinClass(objCClass)) { "Could not get original kotlin class for $objCClass." }
    return get(kClazz, qualifier) { parametersOf(parameter) }
}

@BetaInteropApi
fun Koin.get(objCClass: ObjCClass, parameter: Any): Any {
    val kClazz = requireNotNull(getOriginalKotlinClass(objCClass)) { "Could not get original kotlin class for $objCClass." }
    return get(kClazz, null) { parametersOf(parameter) }
}

@BetaInteropApi
fun Koin.get(objCClass: ObjCClass, qualifier: Qualifier?): Any {
    val kClazz = requireNotNull(getOriginalKotlinClass(objCClass)) { "Could not get original kotlin class for $objCClass." }
    return get(kClazz, qualifier, null)
}

@BetaInteropApi
fun Koin.get(objCClass: ObjCClass): Any {
    val kClazz = requireNotNull(getOriginalKotlinClass(objCClass)) { "Could not get original kotlin class for $objCClass." }
    return get(kClazz, null)
}

@BetaInteropApi
fun Koin.get(objCProtocol: ObjCProtocol, qualifier: Qualifier?): Any {
    val kClazz = requireNotNull(getOriginalKotlinClass(objCProtocol)) { "Could not get original kotlin class for $objCProtocol." }
    return get(kClazz, qualifier, null)
}
