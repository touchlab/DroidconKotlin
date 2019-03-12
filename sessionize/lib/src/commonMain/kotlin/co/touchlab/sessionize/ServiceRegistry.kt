package co.touchlab.sessionize

import co.touchlab.sessionize.api.AnalyticsApi
import co.touchlab.sessionize.api.SessionizeApi
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
}