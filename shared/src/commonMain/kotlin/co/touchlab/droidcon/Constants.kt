package co.touchlab.droidcon

import kotlinx.datetime.TimeZone

object Constants {
    val conferenceTimeZone = TimeZone.of("Europe/London")

    /**
     * String of `${timeZoneString}|${Sessionize.scheduleId}` run through md5
     * When time zone is changed, generate a new hash by running `md5 -s "${timeZoneString}|${Sessionize.scheduleId}"` in command line
     */
    val conferenceTimeZoneHash = "a6aeae038ab00e8e9f725e2efdccd961"

    object Firestore {

        const val projectId = "droidcon-148cc"
        const val databaseName = "(default)"

        // Known variants: "sponsors", "sponsors-lisbon-2019", "sponsors-sf-2019", "sponsors-sf-2022", "sponsors-berlin-2022", "sponsors-nyc-2022"
        const val collectionName = "sponsors-london-2022"
        const val apiKey = "AIzaSyCkD5DH2rUJ8aZuJzANpIFj0AVuCNik1l0"
    }

    object Sessionize {

        const val scheduleId = "qi0g29hw"
        const val sponsorsId = "qi0g29hw"
    }
}
