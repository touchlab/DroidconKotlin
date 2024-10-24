package co.touchlab.droidcon

import kotlinx.datetime.TimeZone

object Constants {
    val conferenceTimeZone = TimeZone.of("Europe/London")

    /**
     * String of `${timeZoneString}|${Sessionize.scheduleId}` run through md5
     * When time zone is changed, generate a new hash by running `md5 -s "${timeZoneString}|${Sessionize.scheduleId}"` in command line
     */
    val conferenceTimeZoneHash = "7f5ba3fbad43896bad6847f6e8c662ac"
    val showVenueMap: Boolean = false

    object Firestore {

        const val projectId = "droidcon-148cc"
        const val databaseName = "(default)"

        // Known variants: "sponsors", "sponsors-lisbon-2019", "sponsors-sf-2019", "sponsors-sf-2022", "sponsors-berlin-2022", "sponsors-nyc-2022"
        const val collectionName = "sponsors-london-2024"
        const val apiKey = "AIzaSyCkD5DH2rUJ8aZuJzANpIFj0AVuCNik1l0"
    }

    object Sessionize {

        const val scheduleId = "78xrdv22"
        const val sponsorsId = "78xrdv22"
    }

    object SisterApp {
        val showLaunchButton: Boolean = false
        const val name = "Fluttercon"
        const val androidPackageName = "co.touchlab.fluttercon"
        const val iosUrlString = "fluttercon://open"
        const val iosAppStoreUrlString = "https://apps.apple.com/app/fluttercon/id6670623431"
    }
}
