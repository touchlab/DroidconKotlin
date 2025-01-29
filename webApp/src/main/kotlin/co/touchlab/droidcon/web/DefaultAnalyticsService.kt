package co.touchlab.droidcon.web
import co.touchlab.droidcon.domain.service.AnalyticsService

import dev.gitlive.firebase.analytics.FirebaseAnalytics

class DefaultAnalyticsService(private val firebaseAnalytics: FirebaseAnalytics) : AnalyticsService {

    override fun logEvent(name: String, params: Map<String, Any>) {
        firebaseAnalytics.logEvent(name, params)
    }
}
