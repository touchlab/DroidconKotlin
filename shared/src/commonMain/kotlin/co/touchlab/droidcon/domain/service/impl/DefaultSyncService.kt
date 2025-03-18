package co.touchlab.droidcon.domain.service.impl

import co.touchlab.droidcon.composite.Url
import co.touchlab.droidcon.db.DroidconDatabase
import co.touchlab.droidcon.domain.entity.Profile
import co.touchlab.droidcon.domain.entity.Room
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.entity.Sponsor
import co.touchlab.droidcon.domain.entity.SponsorGroup
import co.touchlab.droidcon.domain.repository.ConferenceRepository
import co.touchlab.droidcon.domain.repository.ProfileRepository
import co.touchlab.droidcon.domain.repository.RoomRepository
import co.touchlab.droidcon.domain.repository.SessionRepository
import co.touchlab.droidcon.domain.repository.SponsorGroupRepository
import co.touchlab.droidcon.domain.repository.SponsorRepository
import co.touchlab.droidcon.domain.service.ConferenceConfigProvider
import co.touchlab.droidcon.domain.service.DateTimeService
import co.touchlab.droidcon.domain.service.ServerApi
import co.touchlab.droidcon.domain.service.SyncService
import co.touchlab.droidcon.domain.service.fromConferenceDateTime
import co.touchlab.droidcon.domain.service.impl.dto.ScheduleDto
import co.touchlab.droidcon.domain.service.impl.dto.SpeakersDto
import co.touchlab.droidcon.domain.service.impl.dto.SpeakersDto.LinkType
import co.touchlab.droidcon.domain.service.impl.dto.SponsorSessionsDto
import co.touchlab.droidcon.domain.service.impl.dto.SponsorsDto
import co.touchlab.kermit.Logger
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.minus

class DefaultSyncService(
    private val log: Logger,
    private val settings: ObservableSettings,
    private val dateTimeService: DateTimeService,
    private val conferenceRepository: ConferenceRepository,
    private val profileRepository: ProfileRepository,
    private val sessionRepository: SessionRepository,
    private val roomRepository: RoomRepository,
    private val sponsorRepository: SponsorRepository,
    private val sponsorGroupRepository: SponsorGroupRepository,
    private val apiDataSource: DataSource,
    private val serverApi: ServerApi,
    private val db: DroidconDatabase,
    private val conferenceConfigProvider: ConferenceConfigProvider,
) : SyncService {
    private companion object {
        // MARK: Settings keys
        private const val LAST_SESSIONIZE_SYNC_KEY = "LAST_SESSIONIZE_SYNC_TIME"
        private const val LAST_CONFERENCE_ID_KEY = "LAST_CONFERENCE_ID"

        // MARK: Delays
        // 5 minutes
        private const val SESSIONIZE_SYNC_POLL_DELAY: Long = 5L * 60L * 1000L

        private const val SESSIONIZE_SYNC_SINCE_LAST_MINUTES = 15

        private const val SESSIONIZE_SYNC_NEXT_DELAY: Long = 1L * 60L * 60L * 1000L

        // 5 minutes
        private const val RSVP_SYNC_DELAY: Long = 5L * 60L * 1000L

        // 5 minutes
        private const val FEEDBACK_SYNC_DELAY: Long = 5L * 60L * 1000L
    }

    private var lastSessionizeSync: Instant?
        get() = settings.getLongOrNull(LAST_SESSIONIZE_SYNC_KEY)?.let { Instant.fromEpochMilliseconds(it) }
        set(value) {
            settings[LAST_SESSIONIZE_SYNC_KEY] = value?.toEpochMilliseconds()
        }

    private var lastConferenceId: Long?
        get() = settings.getLongOrNull(LAST_CONFERENCE_ID_KEY)
        set(value) {
            if (value != null) {
                settings[LAST_CONFERENCE_ID_KEY] = value
            } else {
                settings.remove(LAST_CONFERENCE_ID_KEY)
            }
        }

    override suspend fun runSynchronization() {
        coroutineScope {
            // Start monitoring for conference changes directly
            launch {
                monitorConferenceChanges()
            }
            launch {
                while (isActive) {
                    val lastSessionizeSync = lastSessionizeSync

                    // Check if conference has changed or if it's time for a scheduled sync
                    val conferenceChanged = hasConferenceChanged()
                    val timeToSync = lastSessionizeSync == null ||
                        lastSessionizeSync <= dateTimeService.now().minus(SESSIONIZE_SYNC_SINCE_LAST_MINUTES, DateTimeUnit.MINUTE)

                    // Run sync if either condition is true
                    if (conferenceChanged || timeToSync) {
                        try {
                            if (conferenceChanged) {
                                log.d { "Conference changed - running sync" }
                            } else if (timeToSync) {
                                log.d { "Scheduled sync time reached - running sync" }
                            }

                            runApiDataSourcesSynchronization()
                        } catch (e: Exception) {
                            log.w(e) { "Failed to update repositories from API data source." }
                            delay(SESSIONIZE_SYNC_POLL_DELAY)
                            continue
                        }

                        // After successful sync
                        if (conferenceChanged) {
                            log.d { "Conference change sync completed, starting normal sync cycles" }
                            delay(SESSIONIZE_SYNC_POLL_DELAY) // Shorter delay after conference change
                        } else {
                            log.d { "Sync successful, waiting for next sync in $SESSIONIZE_SYNC_NEXT_DELAY ms." }
                            delay(SESSIONIZE_SYNC_NEXT_DELAY)
                        }
                    } else {
                        log.d { "The sync didn't happen, so we'll try again in a short while ($SESSIONIZE_SYNC_POLL_DELAY ms)." }
                        delay(SESSIONIZE_SYNC_POLL_DELAY)
                    }
                }
            }

            launch {
                // Use the conferenceConfigProvider to get the current conference ID
                val conferenceId = conferenceConfigProvider.getConferenceId()
                sessionRepository.observeAll(conferenceId)
                    .collect { sessions ->
                        sessions
                            .mapNotNull { session ->
                                val rsvp = session.rsvp
                                return@mapNotNull if (rsvp.isAttending != rsvp.isSent) {
                                    session.id to rsvp.isAttending
                                } else {
                                    null
                                }
                            }
                            .forEach { (sessionId, isAttending) ->
                                while (isActive) {
                                    try {
                                        val isRsvpSent = serverApi.setRsvp(sessionId, isAttending)
                                        if (isRsvpSent) {
                                            // Use the conferenceConfigProvider to get the current conference ID
                                            sessionRepository.setRsvpSent(
                                                sessionId,
                                                isAttending,
                                                conferenceConfigProvider.getConferenceId(),
                                            )
                                        }
                                        break
                                    } catch (e: Exception) {
                                        log.w(e) { "Couldn't send RSVP." }
                                        delay(RSVP_SYNC_DELAY)
                                    }
                                }
                            }
                    }
            }

            launch {
                // Use the conferenceConfigProvider to get the current conference ID
                val conferenceId = conferenceConfigProvider.getConferenceId()
                sessionRepository.observeAll(conferenceId)
                    .collect { sessions ->
                        sessions
                            .mapNotNull { session ->
                                val feedback = session.feedback
                                return@mapNotNull if (feedback != null && !feedback.isSent) {
                                    session.id to feedback
                                } else {
                                    null
                                }
                            }
                            .forEach { (sessionId, feedback) ->
                                while (isActive) {
                                    try {
                                        val isFeedbackSent = serverApi.setFeedback(sessionId, feedback.rating, feedback.comment)
                                        if (isFeedbackSent) {
                                            // Use the conferenceConfigProvider to get the current conference ID
                                            sessionRepository.setFeedbackSent(
                                                sessionId,
                                                isFeedbackSent,
                                                conferenceConfigProvider.getConferenceId(),
                                            )
                                        }
                                        break
                                    } catch (e: Exception) {
                                        log.w(e) { "Couldn't send feedback." }
                                        delay(FEEDBACK_SYNC_DELAY)
                                    }
                                }
                            }
                    }
            }
        }
    }

    override suspend fun forceSynchronize(): Boolean = try {
        runApiDataSourcesSynchronization()
        true
    } catch (e: Exception) {
        log.e(e) { "Failed to update repositories from API data source." }
        false
    }

    private suspend fun runApiDataSourcesSynchronization() {
        val currentConferenceId = conferenceConfigProvider.getConferenceId()
        log.d { "Will sync all repositories from API data source for conference ID: $currentConferenceId" }

        // Track the conference ID change
        val conferenceChanged = lastConferenceId != currentConferenceId
        if (conferenceChanged) {
            log.d { "Conference changed from ${lastConferenceId ?: "unknown"} to $currentConferenceId - forcing sync" }
        }

        // Update the repositories
        updateRepositoriesFromDataSource(apiDataSource)

        // Update the tracking info
        lastSessionizeSync = dateTimeService.now()
        lastConferenceId = currentConferenceId
    }

    /**
     * Checks if the conference has changed since the last sync
     */
    private fun hasConferenceChanged(): Boolean {
        val currentConferenceId = conferenceConfigProvider.getConferenceId()
        val changed = lastConferenceId != null && lastConferenceId != currentConferenceId

        if (changed) {
            log.d { "Conference changed from $lastConferenceId to $currentConferenceId" }
        }

        return changed
    }

    /**
     * Monitor conference changes directly from the repository.
     * This ensures we detect changes even if the normal sync cycles miss them.
     */
    private suspend fun monitorConferenceChanges() {
        try {
            log.d { "Starting direct monitoring of conference changes" }

            // Get initial conference ID to compare against
            var previousConferenceId = conferenceConfigProvider.getConferenceId()
            log.d { "Initial conference ID: $previousConferenceId" }

            // Observe conference changes
            conferenceConfigProvider.observeChanges().collect { conference ->
                if (previousConferenceId != conference.id) {
                    log.d { "Conference change detected through direct monitoring: $previousConferenceId -> ${conference.id}" }

                    // Force synchronization when conference changes
                    try {
                        runApiDataSourcesSynchronization()
                        log.d { "Forced sync completed due to conference change" }
                    } catch (e: Exception) {
                        log.e(e) { "Error during forced sync after conference change" }
                    }

                    // Update previous ID for next comparison
                    previousConferenceId = conference.id
                }
            }
        } catch (e: Exception) {
            log.e(e) { "Error monitoring conference changes" }
        }
    }

    private suspend fun updateRepositoriesFromDataSource(dataSource: DataSource) {
        val speakerDtos = dataSource.getSpeakers()
        val days = dataSource.getSchedule()
        val sponsorSessionsGroups = dataSource.getSponsorSessions()

        // DB Transactions for db mods are ridiculously faster than non-trans changes. Also, if something fails, thd db will roll back.
        // The repo architecture will likely need to change. Everything is suspend and unconcerned with thread, but that's not good practice.
        db.transaction {
            updateSpeakersFromDataSource(speakerDtos)
            updateScheduleFromDataSource(days)
        }

        // Sponsors may fail due to firebase errors, so we'll do this separate
        val sponsors = dataSource.getSponsors()
        db.transaction {
            updateSponsorsFromDataSource(sponsorSessionsGroups, sponsors)
        }
    }

    private fun updateSpeakersFromDataSource(speakerDtos: List<SpeakersDto.SpeakerDto>) {
        val profiles = speakerDtos.map(::profileFactory)
        val conferenceId = conferenceConfigProvider.getConferenceId()

        // Remove deleted speakers.
        profileRepository.allSync(conferenceId).map { it.id }
            .subtract(profiles.map { it.id }.toSet())
            .forEach { profileRepository.remove(it, conferenceId) }

        profiles.forEach {
            profileRepository.addOrUpdate(it, conferenceId)
        }
    }

    private fun updateScheduleFromDataSource(days: List<ScheduleDto.DayDto>) {
        val roomDtos = days.flatMap { it.rooms }
        val conferenceId = conferenceConfigProvider.getConferenceId()

        val rooms = roomDtos.map { room ->
            Room(
                id = Room.Id(room.id),
                name = room.name,
            )
        }

        val sessionsAndSpeakers = roomDtos.flatMap { room ->
            room.sessions.map { dto ->
                Session(
                    dateTimeService = dateTimeService,
                    id = Session.Id(dto.id),
                    title = dto.title,
                    description = dto.description,
                    startsAt = LocalDateTime.parse(dto.startsAt).fromConferenceDateTime(dateTimeService),
                    endsAt = LocalDateTime.parse(dto.endsAt).fromConferenceDateTime(dateTimeService),
                    isServiceSession = dto.isServiceSession,
                    room = Room.Id(dto.roomID),
                    rsvp = Session.RSVP(
                        isAttending = false,
                        isSent = false,
                    ),
                    feedback = null,
                ) to dto.speakers.map { Profile.Id(it.id) }
            }
        }

        // Remove deleted rooms.
        roomRepository.allSync(conferenceId).map { it.id }
            .subtract(rooms.map { it.id }.toSet())
            .forEach { roomRepository.remove(it, conferenceId) }

        rooms.forEach { room ->
            roomRepository.addOrUpdate(room, conferenceId)
        }

        // Remove deleted sessions.
        sessionRepository.allSync(conferenceId)
            .map { it.id }
            .subtract(sessionsAndSpeakers.map { it.first.id }.toSet())
            .forEach { sessionRepository.remove(it, conferenceId) }

        sessionsAndSpeakers.forEach { (updatedSession, speakers) ->
            val existingSession = sessionRepository.findSync(updatedSession.id, conferenceId)
            if (existingSession != null) {
                updatedSession.rsvp = existingSession.rsvp
                updatedSession.feedback = existingSession.feedback
                sessionRepository.update(updatedSession, conferenceId)
            } else {
                sessionRepository.add(updatedSession, conferenceId)
            }

            profileRepository.setSessionSpeakers(updatedSession, speakers, conferenceId)
        }
    }

    private fun updateSponsorsFromDataSource(
        sponsorSessionsGroups: List<SponsorSessionsDto.SessionGroupDto>,
        sponsors: SponsorsDto.SponsorCollectionDto,
    ): String {
        val sponsorSessions = sponsorSessionsGroups.flatMap { it.sessions }.associateBy { it.id }
        val conferenceId = conferenceConfigProvider.getConferenceId()

        val sponsorGroupsToSponsorDtos = sponsors.groups.map { group ->
            val groupName = (group.name.split('/').lastOrNull() ?: group.name)
                .split(' ').joinToString(" ") {
                    it.replaceFirstChar { s -> if (s.isLowerCase()) s.titlecase() else s.toString() }
                }

            SponsorGroup(
                id = SponsorGroup.Id(groupName),
                displayPriority = group.fields.displayOrder.integerValue.toInt(),
                isProminent = group.fields.prominent?.booleanValue ?: false,
            ) to group.fields.sponsors.arrayValue.values.map { it.mapValue.fields }
        }

        val sponsorsAndRepresentativeIds = sponsorGroupsToSponsorDtos.flatMap { (group, sponsorDtos) ->
            sponsorDtos.map { sponsorDto ->
                val sponsorSession = sponsorDto.sponsorId?.stringValue?.let(sponsorSessions::get)
                val representativeIds = sponsorSession?.speakers?.map { Profile.Id(it.id) } ?: emptyList()

                Sponsor(
                    id = Sponsor.Id(sponsorDto.name.stringValue, group.name),
                    hasDetail = sponsorSession != null,
                    description = sponsorSession?.description,
                    icon = Url(sponsorDto.icon.stringValue),
                    url = Url(sponsorDto.url.stringValue),
                ) to representativeIds
            }
        }

        sponsorRepository.allSync(conferenceId).map { it.id }
            .subtract(sponsorsAndRepresentativeIds.map { it.first.id }.toSet())
            .forEach { sponsorRepository.remove(it, conferenceId) }

        sponsorGroupRepository.allSync(conferenceId).map { it.id }
            .subtract(sponsorGroupsToSponsorDtos.map { it.first.id }.toSet())
            .forEach { sponsorGroupRepository.remove(it, conferenceId) }

        sponsorGroupsToSponsorDtos.forEach { (group, _) ->
            sponsorGroupRepository.addOrUpdate(group, conferenceId)
        }

        sponsorsAndRepresentativeIds.forEach { (sponsor, representativeIds) ->
            sponsorRepository.addOrUpdate(sponsor, conferenceId)

            profileRepository.setSponsorRepresentatives(sponsor, representativeIds, conferenceId)
        }
        return ""
    }

    private fun profileFactory(speakerDto: SpeakersDto.SpeakerDto): Profile {
        val groupedLinks = speakerDto.links.filter { it.url.isNotBlank() }.groupBy { it.linkType }
        return Profile(
            id = Profile.Id(speakerDto.id),
            fullName = speakerDto.fullName,
            bio = speakerDto.bio,
            tagLine = speakerDto.tagLine,
            profilePicture = speakerDto.profilePicture?.let(::Url),
            twitter = groupedLinks[LinkType.Twitter]?.firstOrNull()?.url?.let(::Url),
            linkedIn = groupedLinks[LinkType.LinkedIn]?.firstOrNull()?.url?.let(::Url),
            website = (
                groupedLinks[LinkType.CompanyWebsite] ?: groupedLinks[LinkType.Blog] ?: groupedLinks[LinkType.Other]
                )?.firstOrNull()?.url?.let(::Url),
        )
    }

    interface DataSource {
        enum class Kind {
            Api,
        }

        suspend fun getSpeakers(): List<SpeakersDto.SpeakerDto>

        suspend fun getSchedule(): List<ScheduleDto.DayDto>

        suspend fun getSponsorSessions(): List<SponsorSessionsDto.SessionGroupDto>

        suspend fun getSponsors(): SponsorsDto.SponsorCollectionDto
    }
}
