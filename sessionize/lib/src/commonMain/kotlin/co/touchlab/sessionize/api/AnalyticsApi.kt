package co.touchlab.sessionize.api

interface AnalyticsApi {
    fun logEvent(name: String, params: Map<String, Any>)
}