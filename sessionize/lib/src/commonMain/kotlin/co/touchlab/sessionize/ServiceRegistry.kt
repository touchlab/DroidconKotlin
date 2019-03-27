package co.touchlab.sessionize

import co.touchlab.sessionize.api.AnalyticsApi
import co.touchlab.sessionize.api.SessionizeApi
import co.touchlab.sessionize.api.SessionizeApiImpl
import co.touchlab.sessionize.platform.Concurrent
import com.russhwolf.settings.Settings
import com.squareup.sqldelight.db.SqlDriver
import kotlinx.coroutines.CoroutineDispatcher
import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object ServiceRegistry {
    lateinit var sessionizeApi: SessionizeApi
    lateinit var analyticsApi: AnalyticsApi
    lateinit var dbDriver: SqlDriver
    lateinit var coroutinesDispatcher: CoroutineDispatcher
    lateinit var appSettings: Settings
    lateinit var concurrent: Concurrent

    fun initServiceRegistry(sqlDriver: SqlDriver, coroutineDispatcher: CoroutineDispatcher, settings: Settings,
                            concurrent: Concurrent, sessionizeApi: SessionizeApi, analyticsApi: AnalyticsApi) {
        ServiceRegistry.dbDriver = sqlDriver
        ServiceRegistry.coroutinesDispatcher = coroutineDispatcher
        ServiceRegistry.appSettings = settings
        ServiceRegistry.concurrent = concurrent
        ServiceRegistry.sessionizeApi = sessionizeApi
        ServiceRegistry.analyticsApi = analyticsApi
    }
}