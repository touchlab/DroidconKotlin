package co.touchlab.droidcon

import kotlinx.datetime.TimeZone

@Suppress("ktlint:standard:property-naming")
object Constants {
    val showVenueMap: Boolean = true

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
