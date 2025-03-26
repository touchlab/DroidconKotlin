package co.touchlab.droidcon.util

import co.touchlab.droidcon.domain.service.ConferenceConfigProvider

/**
 * Utility class for app-specific checks.
 * Previously contained time zone hash checking, which has been removed.
 * Keeping the class for potential future app verification checks.
 */
class AppChecker(private val conferenceConfigProvider: ConferenceConfigProvider)
