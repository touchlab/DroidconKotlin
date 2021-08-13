package co.touchlab.droidcon.android.service.impl

import android.os.Bundle
import co.touchlab.droidcon.domain.service.AnalyticsService
import com.google.firebase.analytics.FirebaseAnalytics

class AndroidAnalyticsService(private val firebaseAnalytics: FirebaseAnalytics): AnalyticsService {

    override fun logEvent(name: String, params: Map<String, Any>) {
        val bundle = Bundle()
        params.keys.forEach { key ->
            when (val obj = params[key]) {
                is String -> bundle.putString(key, obj)
                is Boolean -> bundle.putBoolean(key, obj)
                is Int -> bundle.putInt(key, obj)
                is Long -> bundle.putLong(key, obj)
                is Double -> bundle.putDouble(key, obj)
                is BooleanArray -> bundle.putBooleanArray(key, obj)
                is IntArray -> bundle.putIntArray(key, obj)
                is LongArray -> bundle.putLongArray(key, obj)
                is DoubleArray -> bundle.putDoubleArray(key, obj)
                else -> throw IllegalArgumentException("Unsupported type $obj with key $key")
            }
        }
        firebaseAnalytics.logEvent(name, bundle)
    }
}