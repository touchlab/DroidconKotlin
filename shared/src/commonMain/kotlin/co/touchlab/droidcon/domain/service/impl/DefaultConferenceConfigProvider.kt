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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.TimeZone

class DefaultConferenceConfigProvider(private val conferenceRepository: ConferenceRepository, initialConference: Conference? = null) :
    ConferenceConfigProvider {
    private val log = Logger.withTag("DefaultConferenceConfigProvider")
    private val conferenceMutex = Mutex()
    private val _currentConferenceState = MutableStateFlow(initialConference)
    val currentConferenceState: StateFlow<Conference?> = _currentConferenceState

    private val currentConference: Conference?
        get() = currentConferenceState.value

    override suspend fun getConferenceId(): Long = getConference().id

    override fun getConferenceTimeZone(): TimeZone? = currentConference?.timeZone

    override fun getProjectId(): String = "droidcon-148cc"

    override suspend fun getCollectionName(): String = getConference().collectionName

    override suspend fun getApiKey(): String = getConference().apiKey

    override suspend fun getScheduleId(): String = getConference().scheduleId

    override fun observeChanges(): Flow<Conference> = conferenceRepository.observeSelected()

    // Implementation of the interface method to get the currently selected conference
    override suspend fun getSelectedConference(): Conference = conferenceRepository.getSelected()

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

    private suspend fun getConference(): Conference {
        currentConference?.let { return it }
        return conferenceMutex.withLock {
            currentConference ?: conferenceRepository.getSelected().also { conference ->
                _currentConferenceState.value = conference
            }
        }
    }
}
