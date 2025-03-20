package co.touchlab.droidcon.domain.repository.impl

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import co.touchlab.droidcon.db.ConferenceQueries
import co.touchlab.droidcon.domain.entity.Conference
import co.touchlab.droidcon.domain.repository.ConferenceRepository
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class SqlDelightConferenceRepository(
    private val conferenceQueries: ConferenceQueries,
    private val log: Logger = Logger.withTag("SqlDelightConferenceRepository"),
) : ConferenceRepository {

    override fun observeAll(): Flow<List<Conference>> =
        conferenceQueries.selectAllActive(::conferenceFactory).asFlow().mapToList(Dispatchers.Main)

    override fun observeSelected(): Flow<Conference> =
        conferenceQueries.selectSelected(::conferenceFactory).asFlow().mapToOne(Dispatchers.Main)

    override suspend fun getSelected(): Conference = conferenceQueries.selectSelected(::conferenceFactory).executeAsOne()

    override suspend fun select(conferenceId: Long): Boolean {
        try {
            conferenceQueries.changeSelectedConference(conferenceId)
            return true
        } catch (e: Exception) {
            log.e(e) { "Error selecting conference" }
            return false
        }
    }

    override suspend fun add(conference: Conference): Long {
        conferenceQueries.insert(
            conferenceName = conference.name,
            conferenceTimeZone = conference.timeZone,
            projectId = conference.projectId,
            collectionName = conference.collectionName,
            apiKey = conference.apiKey,
            scheduleId = conference.scheduleId,
            selected = conference.selected,
            active = conference.active,
        )
        // Return the last inserted ID
        return conferenceQueries.lastInsertRowId().executeAsOne()
    }

    override suspend fun update(conference: Conference): Boolean {
        try {
            conferenceQueries.updateConference(
                conferenceName = conference.name,
                conferenceTimeZone = conference.timeZone,
                projectId = conference.projectId,
                collectionName = conference.collectionName,
                apiKey = conference.apiKey,
                scheduleId = conference.scheduleId,
                selected = conference.selected,
                active = conference.active,
                id = conference.id,
            )
            return true
        } catch (e: Exception) {
            log.e(e) { "Error updating conference" }
            return false
        }
    }

    override suspend fun delete(conferenceId: Long): Boolean {
        conferenceQueries.deleteById(conferenceId)
        return true
    }

    private fun conferenceFactory(
        id: Long,
        conferenceName: String,
        conferenceTimeZone: kotlinx.datetime.TimeZone,
        projectId: String,
        collectionName: String,
        apiKey: String,
        scheduleId: String,
        selected: Boolean,
        active: Boolean,
    ): Conference = Conference(
        _id = id,
        name = conferenceName,
        timeZone = conferenceTimeZone,
        projectId = projectId,
        collectionName = collectionName,
        apiKey = apiKey,
        scheduleId = scheduleId,
        selected = selected,
        active = active,
    )
}
