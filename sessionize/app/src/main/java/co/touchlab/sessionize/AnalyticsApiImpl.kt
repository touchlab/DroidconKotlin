package co.touchlab.sessionize

import co.touchlab.sessionize.api.AnalyticsApi
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent

class AnalyticsApiImpl : AnalyticsApi {
    override fun logEvent(name: String, params: Map<String, Any>) {
        val event = CustomEvent(name)
        params.keys.forEach { key ->
            when (val obj = params[key]) {
                is String -> event.putCustomAttribute(key, obj)
                is Number -> event.putCustomAttribute(key, obj)
                else -> {
                    throw IllegalArgumentException("Don't know what this is $key/$obj")
                }
            }
        }
        Answers.getInstance().logCustom(event)
    }
}