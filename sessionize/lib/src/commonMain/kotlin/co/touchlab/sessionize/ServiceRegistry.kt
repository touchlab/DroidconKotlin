package co.touchlab.sessionize

import co.touchlab.sessionize.api.AnalyticsApi
import co.touchlab.sessionize.api.SessionizeApi

object ServiceRegistry {
    lateinit var sessionizeApi: SessionizeApi
    lateinit var analyticsApi: AnalyticsApi
}