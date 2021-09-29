package co.touchlab.droidcon.domain.service

interface AnalyticsService {

    companion object {

        const val EVENT_STARTED: String = "STARTED"
    }

    fun logEvent(name: String, params: Map<String, Any> = emptyMap())
}