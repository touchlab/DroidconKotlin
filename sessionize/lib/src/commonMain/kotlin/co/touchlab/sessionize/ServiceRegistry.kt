package co.touchlab.sessionize

import co.touchlab.sessionize.api.AnalyticsApi
import co.touchlab.sessionize.api.SessionizeApi
import co.touchlab.sessionize.platform.Concurrent
import co.touchlab.stately.concurrency.AtomicReference
import co.touchlab.stately.freeze
import com.russhwolf.settings.Settings
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.CoroutineDispatcher
import kotlin.reflect.KProperty

object ServiceRegistry {
    var sessionizeApi:SessionizeApi by FrozenDelegate()
    var analyticsApi: AnalyticsApi by FrozenDelegate()
    var dbDriver: SqlDriver by FrozenDelegate()
    var coroutinesDispatcher: CoroutineDispatcher by FrozenDelegate()
    var appSettings: Settings by FrozenDelegate()
    var concurrent: Concurrent by FrozenDelegate()
    var timeZone: String by FrozenDelegate()

    fun initServiceRegistry(sqlDriver: SqlDriver, coroutineDispatcher: CoroutineDispatcher, settings: Settings,
                            concurrent: Concurrent, sessionizeApi: SessionizeApi, analyticsApi: AnalyticsApi,
                            timeZone: String) {
        ServiceRegistry.dbDriver = sqlDriver
        ServiceRegistry.coroutinesDispatcher = coroutineDispatcher
        ServiceRegistry.appSettings = settings
        ServiceRegistry.concurrent = concurrent
        ServiceRegistry.sessionizeApi = sessionizeApi
        ServiceRegistry.analyticsApi = analyticsApi
        ServiceRegistry.timeZone = timeZone
    }
}

internal class FrozenDelegate<T>{
    private val delegateReference = AtomicReference<T?>(null)
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = delegateReference.get()!!

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        delegateReference.set(value.freeze())
    }
}