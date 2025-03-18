package co.touchlab.droidcon.domain.service.impl

import co.touchlab.droidcon.domain.entity.Conference
import co.touchlab.droidcon.domain.repository.ConferenceRepository
import co.touchlab.droidcon.domain.service.ConferenceConfigProvider
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.TimeZone

class DefaultConferenceConfigProvider(
    private val conferenceRepository: ConferenceRepository,
    initialConference: Conference
) : ConferenceConfigProvider {
    private val log = Logger.withTag("DefaultConferenceConfigProvider")
    private val _currentConferenceState = MutableStateFlow(initialConference)
    val currentConferenceState: StateFlow<Conference> = _currentConferenceState

    private val currentConference: Conference
        get() = currentConferenceState.value

    override fun getConferenceId(): Long = currentConference.id

    override fun getConferenceTimeZone(): TimeZone = currentConference.timeZone

    override fun getProjectId(): String = currentConference.projectId

    override fun getCollectionName(): String = currentConference.collectionName

    override fun getApiKey(): String = currentConference.apiKey

    override fun getScheduleId(): String = currentConference.scheduleId

    override fun showVenueMap(): Boolean =
        true // Default to true, will be configurable per conference later

    override fun observeChanges(): Flow<Conference> = conferenceRepository.observeSelected()

    // Implementation of the interface method to load the conference asynchronously
    // Also sets up continuous observation of conference changes
    override suspend fun loadSelectedConference() {
        conferenceRepository.observeSelected().collect { conference ->
            _currentConferenceState.emit(conference)
        }
    }
}
