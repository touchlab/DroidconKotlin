package co.touchlab.droidcon.domain.service.impl

import co.touchlab.droidcon.domain.entity.Conference
import co.touchlab.droidcon.domain.repository.ConferenceRepository
import co.touchlab.droidcon.domain.service.ConferenceConfigProvider
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.TimeZone

class DefaultConferenceConfigProvider(private val conferenceRepository: ConferenceRepository) : ConferenceConfigProvider {
    private val log = Logger.withTag("DefaultConferenceConfigProvider")
    private var currentConference: Conference? = null

    // Default fallback values (previously in Constants)
    private val fallbackConferenceId = 1L
    private val fallbackTimeZone = TimeZone.of("Europe/London")
    private val fallbackProjectId = "droidcon-148cc"
    private val fallbackDatabaseName = "(default)"
    private val fallbackCollectionName = "sponsors-london-2024"
    private val fallbackApiKey = "AIzaSyCkD5DH2rUJ8aZuJzANpIFj0AVuCNik1l0"
    private val fallbackScheduleId = "78xrdv22"

    // New property added to replace showVenueMap from Constants
    private val fallbackShowVenueMap = true

    // No blocking initialization in constructor
    // Instead we'll use fallback values until the conference is loaded asynchronously

    init {
        log.d { "Initializing with fallback values until conference is loaded" }
    }

    override fun getConferenceId(): Long = currentConference?.id ?: fallbackConferenceId

    override fun getConferenceTimeZone(): TimeZone = currentConference?.timeZone ?: fallbackTimeZone

    override fun getProjectId(): String = currentConference?.projectId ?: fallbackProjectId

    override fun getCollectionName(): String = currentConference?.collectionName ?: fallbackCollectionName

    override fun getApiKey(): String = currentConference?.apiKey ?: fallbackApiKey

    override fun getScheduleId(): String = currentConference?.scheduleId ?: fallbackScheduleId

    override fun showVenueMap(): Boolean = true // Default to true, will be configurable per conference later

    override fun observeChanges(): Flow<Conference> = conferenceRepository.observeSelected()

    // Implementation of the interface method to load the conference asynchronously
    override suspend fun loadSelectedConference() {
        try {
            currentConference = conferenceRepository.getSelected()
            log.d { "Loaded conference: ${currentConference?.name}" }
        } catch (e: Exception) {
            log.e(e) { "Error loading conference, continuing with fallback values" }
        }
    }
}
