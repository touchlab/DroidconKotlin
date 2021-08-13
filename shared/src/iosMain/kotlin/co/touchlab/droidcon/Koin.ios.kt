package co.touchlab.droidcon

import co.touchlab.droidcon.application.service.NotificationService
import co.touchlab.droidcon.domain.repository.impl.SqlDelightDriverFactory
import co.touchlab.droidcon.service.IOSNotificationService
import co.touchlab.kermit.Kermit
import co.touchlab.kermit.NSLogLogger
import com.squareup.sqldelight.db.SqlDriver
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.ios.Ios
import kotlinx.cinterop.ObjCClass
import kotlinx.cinterop.ObjCProtocol
import kotlinx.cinterop.getOriginalKotlinClass
import org.koin.core.Koin
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.Qualifier
import org.koin.dsl.module

actual val platformModule = module {
    single<SqlDriver> { SqlDelightDriverFactory().createDriver() }

    single<HttpClientEngine> {
        Ios.create {}
    }

    single<NotificationService> {
        IOSNotificationService(
            log = getWith("IOSNotificationService"),
        )
    }

    val baseKermit = Kermit(NSLogLogger()).withTag("Droidcon")
    factory { (tag: String?) -> if (tag != null) baseKermit.withTag(tag) else baseKermit }
}

fun Koin.get(objCClass: ObjCClass, qualifier: Qualifier?, parameter: Any): Any {
    val kClazz = requireNotNull(getOriginalKotlinClass(objCClass)) { "Could not get original kotlin class for $objCClass." }
    return get(kClazz, qualifier) { parametersOf(parameter) }
}

fun Koin.get(objCClass: ObjCClass, parameter: Any): Any {
    val kClazz = requireNotNull(getOriginalKotlinClass(objCClass)) { "Could not get original kotlin class for $objCClass." }
    return get(kClazz, null) { parametersOf(parameter) }
}

fun Koin.get(objCClass: ObjCClass, qualifier: Qualifier?): Any {
    val kClazz = requireNotNull(getOriginalKotlinClass(objCClass)) { "Could not get original kotlin class for $objCClass." }
    return get(kClazz, qualifier, null)
}

fun Koin.get(objCProtocol: ObjCProtocol, qualifier: Qualifier?): Any {
    val kClazz = requireNotNull(getOriginalKotlinClass(objCProtocol)) { "Could not get original kotlin class for $objCProtocol." }
    return get(kClazz, qualifier, null)
}

