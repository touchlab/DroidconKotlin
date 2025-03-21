package co.touchlab.droidcon.domain.service.impl

import co.touchlab.droidcon.composite.Url
import co.touchlab.droidcon.db.DroidconDatabase
import co.touchlab.droidcon.domain.entity.Conference
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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus

class DefaultSyncService(
    private val log: Logger,
    private val dateTimeService: DateTimeService,
    private val profileRepository: ProfileRepository,
    private val sessionRepository: SessionRepository,
    private val roomRepository: RoomRepository,
    private val sponsorRepository: SponsorRepository,
    private val sponsorGroupRepository: SponsorGroupRepository,
    private val apiDataSource: DataSource,
    private val serverApi: ServerApi,
    private val db: DroidconDatabase,
    private val conferenceRepository: ConferenceRepository,
) : SyncService {
    private val testNotificationTimes = false

    private companion object {
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

    override suspend fun runSynchronization(conference: Conference) {
        coroutineScope {
            launch {
                var lastSessionizeSyncThisLoop: Instant = dateTimeService.now().minus(3, DateTimeUnit.HOUR)
                while (isActive) {
                    val lastSessionizeSync = lastSessionizeSyncThisLoop

                    val timeToSync =
                        lastSessionizeSync <= dateTimeService.now().minus(SESSIONIZE_SYNC_SINCE_LAST_MINUTES, DateTimeUnit.MINUTE)

                    log.w("DATASYNC runSynchronization called with $conference")
                    try { // Run sync if either condition is true
                        if (timeToSync) {
                            try {
                                runApiDataSourcesSynchronization(conference)
                                lastSessionizeSyncThisLoop = dateTimeService.now()
                            } catch (e: Exception) {
                                delay(SESSIONIZE_SYNC_POLL_DELAY)
                                continue
                            }
                            delay(SESSIONIZE_SYNC_NEXT_DELAY)
                        } else {
                            delay(SESSIONIZE_SYNC_POLL_DELAY)
                        }
                    } catch (e: Exception) {
                        log.w("DATASYNC runSynchronization exiting with $conference")
                        throw e
                    }
                    log.w("DATASYNC runSynchronization looped with $conference")
                }
            }

            launch {
                val conferenceId = conference.id
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
                                            sessionRepository.setRsvpSent(
                                                sessionId,
                                                isAttending,
                                                conferenceId,
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
                val conferenceId = conference.id
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
                                            sessionRepository.setFeedbackSent(
                                                sessionId,
                                                isFeedbackSent,
                                                conferenceId,
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

    override suspend fun forceSynchronize(conference: Conference): Boolean = try {
        runApiDataSourcesSynchronization(conference)
        syncConferences() // Also sync conferences when forced
        true
    } catch (e: Exception) {
        log.e(e) { "Failed to update repositories from API data source." }
        false
    }

    private suspend fun runApiDataSourcesSynchronization(conference: Conference) {
        val currentConferenceId = conference.id
        log.d { "Will sync all repositories from API data source for conference ID: $currentConferenceId" }

        // Update the repositories
        updateRepositoriesFromDataSource(apiDataSource, conference)
    }

    private suspend fun updateRepositoriesFromDataSource(dataSource: DataSource, conference: Conference) {
        val speakerDtos = dataSource.getSpeakers()
        val days = dataSource.getSchedule()
        val sponsorSessionsGroups = dataSource.getSponsorSessions()

        // DB Transactions for db mods are ridiculously faster than non-trans changes. Also, if something fails, thd db will roll back.
        // The repo architecture will likely need to change. Everything is suspend and unconcerned with thread, but that's not good practice.
        db.transaction {
            updateSpeakersFromDataSource(speakerDtos, conference)
            updateScheduleFromDataSource(days, conference)
        }

        // Sponsors may fail due to firebase errors, so we'll do this separate
        val sponsors = dataSource.getSponsors()
        db.transaction {
            updateSponsorsFromDataSource(sponsorSessionsGroups, sponsors, conference)
        }
    }

    /**
     * Synchronizes conference data from Firestore with the local database
     */
    override suspend fun syncConferences() {
        log.d { "Syncing conferences from Firestore" }
        try {
            val apiDataSource = apiDataSource as? DefaultApiDataSource
                ?: throw IllegalStateException("apiDataSource is not DefaultApiDataSource")

            // Get conferences from Firestore
            val conferencesFromFirestore = apiDataSource.getConferences()

            // Get all local conferences (need to collect from Flow first)
            val localConferences = conferenceRepository.observeAll().first()

            // Map conferences by name for easy lookup
            val conferencesMap = localConferences.associateBy { it.name }
            val firestoreConferenceNames = mutableSetOf<String>()

            // Process each conference from Firestore
            conferencesFromFirestore.conferences.forEach { conferenceDto ->
                val conferenceFields = conferenceDto.fields
                val conferenceName = conferenceFields.conferenceName.stringValue
                firestoreConferenceNames.add(conferenceName)

                // Check if conference exists locally by name
                val existingConference = conferencesMap[conferenceName]

                if (existingConference != null) {
                    // Check if there are any actual changes before updating
                    val timeZone = TimeZone.of(conferenceFields.conferenceTimeZone.stringValue)
                    val projectId = conferenceFields.projectId.stringValue
                    val collectionName = conferenceFields.collectionName.stringValue
                    val apiKey = conferenceFields.apiKey.stringValue
                    val scheduleId = conferenceFields.scheduleId.stringValue

                    // Only update if any field has changed
                    val needsUpdate = existingConference.timeZone != timeZone ||
                        existingConference.projectId != projectId ||
                        existingConference.collectionName != collectionName ||
                        existingConference.apiKey != apiKey ||
                        existingConference.scheduleId != scheduleId

                    if (needsUpdate) {
                        val updatedConference = Conference(
                            _id = existingConference.id,
                            name = conferenceName,
                            timeZone = timeZone,
                            projectId = projectId,
                            collectionName = collectionName,
                            apiKey = apiKey,
                            scheduleId = scheduleId,
                            selected = existingConference.selected,
                            active = existingConference.active,
                        )
                        conferenceRepository.update(updatedConference)
                        log.d { "Updated conference: $conferenceName (fields changed)" }
                    } else {
                        log.d { "Skipped updating conference: $conferenceName (no changes)" }
                    }
                } else {
                    // Add new conference as active
                    val newConference = Conference(
                        name = conferenceName,
                        timeZone = TimeZone.of(conferenceFields.conferenceTimeZone.stringValue),
                        projectId = conferenceFields.projectId.stringValue,
                        collectionName = conferenceFields.collectionName.stringValue,
                        apiKey = conferenceFields.apiKey.stringValue,
                        scheduleId = conferenceFields.scheduleId.stringValue,
                        selected = false,
                        active = true,
                    )
                    conferenceRepository.add(newConference)
                    log.d { "Added new conference: $conferenceName" }
                }
            }

            // Mark conferences that don't exist in Firestore as inactive,
            // but only if they're currently active
            conferencesMap.forEach { (name, conference) ->
                if (name !in firestoreConferenceNames && conference.active) {
                    val deactivatedConference = Conference(
                        _id = conference.id,
                        name = conference.name,
                        timeZone = conference.timeZone,
                        projectId = conference.projectId,
                        collectionName = conference.collectionName,
                        apiKey = conference.apiKey,
                        scheduleId = conference.scheduleId,
                        selected = conference.selected,
                        active = false,
                    )
                    conferenceRepository.update(deactivatedConference)
                    log.d { "Marked conference as inactive: $name" }
                }
            }

            log.d { "Conference sync completed successfully" }
        } catch (e: Exception) {
            log.e(e) { "Error during conference sync" }
            throw e
        }
    }

    private fun updateSpeakersFromDataSource(speakerDtos: List<SpeakersDto.SpeakerDto>, conference: Conference) {
        val profiles = speakerDtos.map(::profileFactory)
        val conferenceId = conference.id

        // Remove deleted speakers.
        profileRepository.allSync(conferenceId).map { it.id }
            .subtract(profiles.map { it.id }.toSet())
            .forEach { profileRepository.remove(it, conferenceId) }

        profiles.forEach {
            profileRepository.addOrUpdate(it, conferenceId)
        }
    }

    private fun dateFromString(dateTimeString: String): String = dateTimeString.split("T")[0]
    private fun timeFromString(dateTimeString: String): String = dateTimeString.split("T")[1]

    private fun updateScheduleFromDataSource(_days: List<ScheduleDto.DayDto>, conference: Conference) {
        val originalToAdjustedDateMap = _days.flatMap { dayDto ->
            dayDto.rooms.flatMap { roomDto -> roomDto.sessions }
        }.map { sessionDto -> dateFromString(sessionDto.startsAt) }.toSet().toList().sorted().mapIndexed { index, date ->
            val adjustedInstant = dateTimeService.now().plus(index * 24, DateTimeUnit.HOUR)
            Pair(date, dateFromString(adjustedInstant.toString()))
        }.toMap()

        fun updateDateTimeString(dateTimeString: String): String {
            return originalToAdjustedDateMap.get(dateFromString(dateTimeString)) + "T" + timeFromString(dateTimeString)
        }

        val days = kotlin.runCatching { if(testNotificationTimes) {
            _days.map { originalDay ->
                ScheduleDto.DayDto(
                    originalToAdjustedDateMap.get(dateFromString(originalDay.date))!!,
                    originalDay.rooms.map { room ->
                        ScheduleDto.RoomDto(
                            room.id, room.name,
                            room.sessions.map { originalSession ->
                                ScheduleDto.SessionDto(
                                    id = originalSession.id,
                                    title = originalSession.title,
                                    description = originalSession.description,
                                    startsAt = updateDateTimeString(originalSession.startsAt),
                                    endsAt = updateDateTimeString(originalSession.endsAt),
                                    isServiceSession = originalSession.isServiceSession,
                                    isPlenumSession = originalSession.isPlenumSession,
                                    speakers = originalSession.speakers,
                                    categories = originalSession.categories,
                                    roomID = originalSession.roomID,
                                    room = originalSession.room,
                                )
                            },
                        )
                    },
                )
            }
        } else {
            _days
        }}.let { result ->
            result.getOrThrow()
        }

        val roomDtos = days.flatMap { it.rooms }
        val conferenceId = conference.id

        val rooms = roomDtos.map { room ->
            Room(
                id = Room.Id(room.id),
                name = room.name,
            )
        }

        var sessionsAndSpeakers = roomDtos.flatMap { room ->
            room.sessions.map { dto ->
                Session(
                    dateTimeService = dateTimeService,
                    id = Session.Id(dto.id),
                    title = dto.title,
                    description = dto.description,
                    startsAt = LocalDateTime.parse(dto.startsAt).fromConferenceDateTime(dateTimeService, conference.timeZone),
                    endsAt = LocalDateTime.parse(dto.endsAt).fromConferenceDateTime(dateTimeService, conference.timeZone),
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
        conference: Conference,
    ): String {
        val sponsorSessions = sponsorSessionsGroups.flatMap { it.sessions }.associateBy { it.id }
        val conferenceId = conference.id

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
