package co.touchlab.droidcon

import kotlinx.datetime.TimeZone

object Constants {
    val conferenceTimeZone = TimeZone.of("Europe/Berlin")

    /**
     * String of `${timeZoneString}|${Sessionize.scheduleId}` run through md5
     * When time zone is changed, generate a new hash by running `md5 -s "${timeZoneString}|${Sessionize.scheduleId}"` in command line
     */
    val conferenceTimeZoneHash = "b0ff26808102a633934723c03818b085"

    object Firestore {

        const val projectId = "droidcon-148cc"
        const val databaseName = "(default)"

        // Known variants: "sponsors", "sponsors-lisbon-2019", "sponsors-sf-2019", "sponsors-sf-2022"
        const val collectionName = "sponsors-berlin-2022"
        const val apiKey = "AIzaSyCkD5DH2rUJ8aZuJzANpIFj0AVuCNik1l0"
    }

    object Sessionize {
        const val scheduleId = "zwd2wtgt"
        const val sponsorsId = "zwd2wtgt"
    }
}

