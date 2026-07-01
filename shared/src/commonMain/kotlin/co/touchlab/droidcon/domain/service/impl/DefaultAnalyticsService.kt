package co.touchlab.droidcon.domain.service.impl

import co.touchlab.droidcon.domain.service.AnalyticsService
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.analytics.analytics

class DefaultAnalyticsService : AnalyticsService {

    override fun logEvent(name: String, params: Map<String, Any>) {
        Firebase.analytics.logEvent(name, params.toAnalyticsParameters())
    }

    private fun Map<String, Any>.toAnalyticsParameters(): Map<String, Any>? {
        if (isEmpty()) {
            return null
        }

        return mapValues { (_, value) ->
            when (value) {
                is String, is Boolean, is Int, is Long, is Double -> value
                is Float -> value.toDouble()
                else -> throw IllegalArgumentException("Unsupported analytics parameter type: ${value::class.simpleName}")
            }
        }
    }
}
