package co.touchlab.droidcon

import kotlinx.datetime.TimeZone

object Constants {
    val conferenceTimeZone = TimeZone.of("America/New_York")

    /**
     * String of `${timeZoneString}|${Sessionize.scheduleId}` run through md5
     * When time zone is changed, generate a new hash by running `md5 -s "${timeZoneString}|${Sessionize.scheduleId}"` in command line
     */
    val conferenceTimeZoneHash = "818478d0420f629b22f9623202329e95"

    object Firestore {

        const val projectId = "droidcon-148cc"
        const val databaseName = "(default)"

        // Known variants: "sponsors", "sponsors-lisbon-2019", "sponsors-sf-2019", "sponsors-sf-2022", "sponsors-berlin-2022", "sponsors-nyc-2022"
        const val collectionName = "sponsors-fluttercon-usa-2024"
        const val apiKey = "AIzaSyCkD5DH2rUJ8aZuJzANpIFj0AVuCNik1l0"
    }

    object Sessionize {

        const val scheduleId = "av0qbkjs"
        const val sponsorsId = "av0qbkjs"
    }

    object SisterApp {
        const val name = "Droidcon"
        const val androidPackageName = "co.touchlab.droidcon.nyc2019"
        const val iosUrlString = "droidconnyc://open"
        const val iosAppStoreUrlString = "https://apps.apple.com/us/app/droidcon-nyc/id1477469914"
    }
}
