package co.touchlab.droidcon

import kotlinx.datetime.TimeZone

object Constants {
    val conferenceTimeZone = TimeZone.of("America/New_York")

    /**
     * String of `${timeZoneString}|${Sessionize.scheduleId}` run through md5
     * When time zone is changed, generate a new hash by running `md5 -s "${timeZoneString}|${Sessionize.scheduleId}"` in command line
     */
    val conferenceTimeZoneHash = "11ee5758034d91b8a40a4e89f6cf9324"

    object Firestore {

        const val projectId = "droidcon-148cc"
        const val databaseName = "(default)"

        // Known variants: "sponsors", "sponsors-lisbon-2019", "sponsors-sf-2019", "sponsors-sf-2022", "sponsors-berlin-2022", "sponsors-nyc-2022"
        const val collectionName = "sponsors-nyc-2024"
        const val apiKey = "AIzaSyCkD5DH2rUJ8aZuJzANpIFj0AVuCNik1l0"
    }

    object Sessionize {

        const val scheduleId = "orzenzbc"
        const val sponsorsId = "orzenzbc"
    }

    object SisterApp {
        const val name = "Fluttercon"
        const val androidPackageName = "co.touchlab.fluttercon"
        const val iosUrlString = "fluttercon://open"
        const val iosAppStoreUrlString = "https://apps.apple.com/app/fluttercon/id6670623431"
    }
}
