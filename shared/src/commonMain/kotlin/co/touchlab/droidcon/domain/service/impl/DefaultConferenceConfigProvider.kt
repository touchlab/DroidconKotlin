package co.touchlab.droidcon.domain.service.impl

import co.touchlab.droidcon.Constants
import co.touchlab.droidcon.domain.entity.Conference
import co.touchlab.droidcon.domain.repository.ConferenceRepository
import co.touchlab.droidcon.domain.service.ConferenceConfigProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.TimeZone

class DefaultConferenceConfigProvider(private val conferenceRepository: ConferenceRepository) : ConferenceConfigProvider {
    private var currentConference: Conference? = null

    init {
        // Initialize with a blocking call to get the current conference
        runBlocking {
            try {
                currentConference = conferenceRepository.getSelected()
            } catch (e: Exception) {
                // If we can't get the selected conference, fallback to Constants
            }
        }
    }

    override fun getConferenceId(): Long = currentConference?.id ?: Constants.conferenceId

    override fun getConferenceTimeZone(): TimeZone = currentConference?.timeZone ?: Constants.conferenceTimeZone

    override fun getProjectId(): String = currentConference?.projectId ?: Constants.Firestore.projectId

    override fun getCollectionName(): String = currentConference?.collectionName ?: Constants.Firestore.collectionName

    override fun getApiKey(): String = currentConference?.apiKey ?: Constants.Firestore.apiKey

    override fun getScheduleId(): String = currentConference?.scheduleId ?: Constants.Sessionize.scheduleId

    override fun observeChanges(): Flow<Conference> = conferenceRepository.observeSelected()
}
