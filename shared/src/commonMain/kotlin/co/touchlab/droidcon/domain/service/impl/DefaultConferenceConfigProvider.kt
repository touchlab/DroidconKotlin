package co.touchlab.droidcon.domain.service.impl

import co.touchlab.droidcon.domain.entity.Conference
import co.touchlab.droidcon.domain.repository.ConferenceRepository
import co.touchlab.droidcon.domain.service.ConferenceConfigProvider
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.datetime.TimeZone

class DefaultConferenceConfigProvider(
    private val conferenceRepository: ConferenceRepository,
    initialConference: Conference? = null
) : ConferenceConfigProvider {
    private val log = Logger.withTag("DefaultConferenceConfigProvider")
    private val _currentConferenceState = MutableStateFlow<Conference?>(initialConference)
    val currentConferenceState: StateFlow<Conference?> = _currentConferenceState

    private val currentConference: Conference?
        get() = currentConferenceState.value

    override fun getConferenceId(): Long? {
        return currentConference?.id
    }

    override fun getConferenceTimeZone(): TimeZone? = currentConference?.timeZone

    override fun getProjectId(): String? = "droidcon-148cc"

    override fun getCollectionName(): String? = currentConference?.collectionName

    override fun getApiKey(): String? {
        log.i { "Getting API Key $currentConference" }
        return currentConference?.apiKey
    }

    override fun getScheduleId(): String? = currentConference?.scheduleId

    override fun showVenueMap(): Boolean? = true // Default to true, will be configurable per conference later

    override fun observeChanges(): Flow<Conference?> = conferenceRepository.observeSelected()
        .map<Conference, Conference?> { it }
        .catch { emit(null) }

    // Implementation of the interface method to get the currently selected conference
    override suspend fun getSelectedConference(): Conference? = try {
        conferenceRepository.getSelected()
    } catch (e: Exception) {
        log.w { "No conference selected: ${e.message}" }
        null
    }

    // Implementation of the interface method to load the conference asynchronously
    // Also sets up continuous observation of conference changes
    override suspend fun loadSelectedConference() {
        log.i { "DefaultConferenceConfigProvider: loadSelectedConference" }
        conferenceRepository.observeSelected()
            .map<Conference, Conference?> { it }
            .catch { e ->
                log.w(e) { "Error observing selected conference, emitting null" }
                emit(null)
            }
            .collect { conference ->
                log.i { "loadSelectedConference: Emitting Conference! $conference" }
                _currentConferenceState.value = conference
            }
    }
}
