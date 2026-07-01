package co.touchlab.droidcon.test

import app.cash.sqldelight.async.coroutines.awaitAsList
import co.touchlab.droidcon.createDroidconDatabase
import co.touchlab.droidcon.db.DroidconDatabase
import kotlinx.datetime.TimeZone

data class TestDatabase(val database: DroidconDatabase, val conferenceId: Long)

suspend fun createTestDatabase(): TestDatabase {
    val driver = createInMemoryDriver()
    val database = createDroidconDatabase(driver)
    val conferenceId = seedConference(database)
    return TestDatabase(database = database, conferenceId = conferenceId)
}

private suspend fun seedConference(database: DroidconDatabase): Long {
    val conferenceName = "Test Conference"
    database.conferenceQueries.insert(
        conferenceName = conferenceName,
        conferenceTimeZone = TimeZone.of("America/Los_Angeles"),
        projectId = "test-project",
        collectionName = "test-collection",
        apiKey = "test-api-key",
        scheduleId = "test-schedule",
        selected = true,
        active = true,
        venueMap = null,
    )
    return database.conferenceIdForName(conferenceName)
}

internal suspend fun DroidconDatabase.conferenceIdForName(conferenceName: String): Long =
    conferenceQueries.selectAll { id, name, _, _, _, _, _, _, _, _ ->
        id to name
    }.awaitAsList().first { (_, name) -> name == conferenceName }.first

internal suspend fun seedSecondConference(database: DroidconDatabase, conferenceName: String = "Second Conference"): Long {
    database.conferenceQueries.insert(
        conferenceName = conferenceName,
        conferenceTimeZone = TimeZone.of("Europe/Berlin"),
        projectId = "project-2",
        collectionName = "collection-2",
        apiKey = "api-key-2",
        scheduleId = "schedule-2",
        selected = false,
        active = true,
        venueMap = null,
    )
    return database.conferenceIdForName(conferenceName)
}
