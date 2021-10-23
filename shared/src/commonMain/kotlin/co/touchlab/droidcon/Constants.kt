package co.touchlab.droidcon

import kotlinx.datetime.TimeZone

object Constants {
    val conferenceTimeZone = TimeZone.of("Europe/London")

    object Firestore {
        const val projectId = "droidcon-148cc"
        const val databaseName = "(default)"

        // Known variants: "sponsors", "sponsors-lisbon-2019", "sponsors-sf-2019"
        const val collectionName = "sponsors-london-2021"
        const val apiKey = "AIzaSyCkD5DH2rUJ8aZuJzANpIFj0AVuCNik1l0"
    }

    object Sessionize {
        const val scheduleId = "4b752cf5"
        const val sponsorsId = "4b752cf5"
    }
}