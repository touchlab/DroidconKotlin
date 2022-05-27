package co.touchlab.droidcon

import kotlinx.datetime.TimeZone

object Constants {
    val conferenceTimeZone = TimeZone.of("America/Los_Angeles")

    object Firestore {
        const val projectId = "droidcon-148cc"
        const val databaseName = "(default)"

        // Known variants: "sponsors", "sponsors-lisbon-2019", "sponsors-sf-2019", "sponsors-sf-2022"
        const val collectionName = "sponsors-sf-2022"
        const val apiKey = "AIzaSyCkD5DH2rUJ8aZuJzANpIFj0AVuCNik1l0"
    }

    object Sessionize {
        const val scheduleId = "qx6mydae"
        const val sponsorsId = "qx6mydae"
    }
}