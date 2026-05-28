package co.touchlab.droidcon.web

import co.touchlab.droidcon.domain.service.AnalyticsService
import co.touchlab.kermit.Logger

// Firebase Analytics does not support Web KMP, so we're using Kermit for now.
class WebAnalyticsService : AnalyticsService {

    private val log = Logger.withTag("WebAnalytics")

    override fun logEvent(name: String, params: Map<String, Any>) {
        if (params.isEmpty()) {
            log.i { "Event: $name" }
        } else {
            log.i { "Event: $name params=$params" }
        }
    }
}
