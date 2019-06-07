package co.touchlab.sessionize

import android.os.Bundle
import co.touchlab.sessionize.api.AnalyticsApi
import com.google.firebase.analytics.FirebaseAnalytics

class AnalyticsApiImpl(val firebaseAnalytics: FirebaseAnalytics) : AnalyticsApi {
    override fun logEvent(name: String, params: Map<String, Any>) {
        val bundle = Bundle()
        params.keys.forEach { key ->
            when (val obj = params[key]) {
                is String -> bundle.putString(key, obj)
                is Int -> bundle.putInt(key, obj)
                else -> {
                    throw IllegalArgumentException("Don't know what this is $key/$obj")
                }
            }
        }
        firebaseAnalytics.logEvent(name, bundle)
    }
}