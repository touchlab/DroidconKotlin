package co.touchlab.droidcon.web

import dev.gitlive.firebase.analytics.FirebaseAnalytics

class DefaultAnalyticsService(private val firebaseAnalytics: FirebaseAnalytics) { // : AnalyticsService {

    fun logEvent(name: String, params: Map<String, Any>) {
        firebaseAnalytics.logEvent(name, params)
    }
}
