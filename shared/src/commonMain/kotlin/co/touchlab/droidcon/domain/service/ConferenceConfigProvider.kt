package co.touchlab.droidcon.domain.service

import co.touchlab.droidcon.domain.entity.Conference
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.TimeZone

interface ConferenceConfigProvider {
    fun getConferenceId(): Long
    fun getConferenceTimeZone(): TimeZone
    fun getProjectId(): String
    fun getCollectionName(): String
    fun getApiKey(): String
    fun getScheduleId(): String
    fun showVenueMap(): Boolean
    fun observeChanges(): Flow<Conference>

    /**
     * Get the currently selected conference
     */
    suspend fun getSelectedConference(): Conference

    /**
     * Initiates conference observation.
     */
    suspend fun loadSelectedConference()
}
