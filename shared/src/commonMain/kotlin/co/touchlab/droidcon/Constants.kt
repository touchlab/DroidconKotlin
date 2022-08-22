package co.touchlab.droidcon

import kotlinx.datetime.TimeZone

object Constants {
    val conferenceTimeZone = TimeZone.of("America/New_York")

    /**
     * String of `${timeZoneString}|${Sessionize.scheduleId}` run through md5
     * When time zone is changed, generate a new hash by running `md5 -s "${timeZoneString}|${Sessionize.scheduleId}"` in command line
     */
    val conferenceTimeZoneHash = "8b484e09200df61364e7affaff42d261"

    object Firestore {

        const val projectId = "droidcon-148cc"
        const val databaseName = "(default)"

        // Known variants: "sponsors", "sponsors-lisbon-2019", "sponsors-sf-2019", "sponsors-sf-2022", "sponsors-berlin-2022"
        const val collectionName = "sponsors-nyc-2022"
        const val apiKey = "AIzaSyCkD5DH2rUJ8aZuJzANpIFj0AVuCNik1l0"
    }

    object Sessionize {
        const val scheduleId = "zwd2wtgt"
        const val sponsorsId = "zwd2wtgt"
    }
}
