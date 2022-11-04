package co.touchlab.droidcon.domain.service.impl

import co.touchlab.droidcon.composite.Url
import co.touchlab.droidcon.db.DroidconDatabase
import co.touchlab.droidcon.domain.entity.Profile
import co.touchlab.droidcon.domain.entity.Room
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.entity.Sponsor
import co.touchlab.droidcon.domain.entity.SponsorGroup
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
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.minus

@OptIn(ExperimentalSettingsApi::class)
class DefaultSyncService(
    private val log: Logger,
    private val settings: ObservableSettings,
    private val dateTimeService: DateTimeService,
    private val profileRepository: ProfileRepository,
    private val sessionRepository: SessionRepository,
    private val roomRepository: RoomRepository,
    private val sponsorRepository: SponsorRepository,
    private val sponsorGroupRepository: SponsorGroupRepository,
    private val seedDataSource: DataSource,
    private val apiDataSource: DataSource,
    private val serverApi: ServerApi,
    private val db: DroidconDatabase,
) : SyncService {
    private companion object {
        // MARK: Settings keys
        private const val LOCAL_REPOSITORIES_SEEDED_KEY = "LOCAL_REPOSITORIES_SEEDED"
        private const val LAST_SESSIONIZE_SYNC_KEY = "LAST_SESSIONIZE_SYNC_TIME"

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

    private var isLocalRepositoriesSeeded: Boolean
        get() = settings[LOCAL_REPOSITORIES_SEEDED_KEY, false]
        set(value) {
            settings[LOCAL_REPOSITORIES_SEEDED_KEY] = value
        }

    private var lastSessionizeSync: Instant?
        get() = settings.getLongOrNull(LAST_SESSIONIZE_SYNC_KEY)?.let { Instant.fromEpochMilliseconds(it) }
        set(value) {
            settings[LAST_SESSIONIZE_SYNC_KEY] = value?.toEpochMilliseconds()
        }

    @OptIn(FlowPreview::class)
    override suspend fun runSynchronization() {
        seedLocalRepositoriesIfNeeded()

        coroutineScope {
            launch {
                while (isActive) {
                    val lastSessionizeSync = lastSessionizeSync
                    // If this is the first Sessionize sync or if the last sync occurred more than 2 hours ago.
                    if (lastSessionizeSync == null || lastSessionizeSync <= dateTimeService.now().minus(SESSIONIZE_SYNC_SINCE_LAST_MINUTES, DateTimeUnit.MINUTE)) {
                        log.d { "Will sync all repositories from API data source." }
                        try {
                            updateRepositoriesFromDataSource(apiDataSource)
                        } catch (e: Exception) {
                            log.w(e) { "Failed to update repositories from API data source." }
                            delay(SESSIONIZE_SYNC_POLL_DELAY)
                            continue
                        }
                        log.d { "Sync successful, waiting for next sync in $SESSIONIZE_SYNC_NEXT_DELAY ms." }
                        this@DefaultSyncService.lastSessionizeSync = dateTimeService.now()
                        delay(SESSIONIZE_SYNC_NEXT_DELAY)
                    } else {
                        log.d { "The sync didn't happen, so we'll try again in a short while ($SESSIONIZE_SYNC_POLL_DELAY ms)." }
                        delay(SESSIONIZE_SYNC_POLL_DELAY)
                    }
                }
            }

            launch {
                sessionRepository.observeAll()
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
                                            sessionRepository.setRsvpSent(sessionId, isAttending)
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
                sessionRepository.observeAll()
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
                                            sessionRepository.setFeedbackSent(sessionId, isFeedbackSent)
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

    private suspend fun seedLocalRepositoriesIfNeeded() {
        if (isLocalRepositoriesSeeded) {
            return
        }

        updateRepositoriesFromDataSource(seedDataSource)

        isLocalRepositoriesSeeded = true
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

        // Remove deleted speakers.
        profileRepository.allSync().map { it.id }
            .subtract(profiles.map { it.id })
            .forEach { profileRepository.remove(it) }

        profiles.forEach {
            profileRepository.addOrUpdate(it)
        }
    }

    private fun updateScheduleFromDataSource(days: List<ScheduleDto.DayDto>) {
        val roomDtos = days.flatMap { it.rooms }

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
        roomRepository.allSync().map { it.id }
            .subtract(rooms.map { it.id })
            .forEach { roomRepository.remove(it) }

        rooms.forEach { room ->
            roomRepository.addOrUpdate(room)
        }

        // Remove deleted sessions.
        sessionRepository.allSync()
            .map { it.id }
            .subtract(sessionsAndSpeakers.map { it.first.id })
            .forEach { sessionRepository.remove(it) }

        sessionsAndSpeakers.forEach { (updatedSession, speakers) ->
            val existingSession = sessionRepository.findSync(updatedSession.id)
            if (existingSession != null) {
                updatedSession.rsvp = existingSession.rsvp
                updatedSession.feedback = existingSession.feedback
                sessionRepository.update(updatedSession)
            } else {
                sessionRepository.add(updatedSession)
            }

            profileRepository.setSessionSpeakers(updatedSession, speakers)
        }
    }

    private fun updateSponsorsFromDataSource(sponsorSessionsGroups: List<SponsorSessionsDto.SessionGroupDto>, sponsors: SponsorsDto.SponsorCollectionDto) {
        val sponsorSessions = sponsorSessionsGroups.flatMap { it.sessions }.associateBy { it.id }
        val sponsorGroupsToSponsorDtos = sponsors.groups.map { group ->
            val groupName = (group.name.split('/').lastOrNull() ?: group.name)
                .split(' ').joinToString(" ") {
                    it.replaceFirstChar { s -> if (s.isLowerCase()) s.titlecase() else s.toString() }
                }

            SponsorGroup(
                id = SponsorGroup.Id(groupName),
                displayPriority = group.fields.displayOrder.integerValue.toInt(),
                isProminent = group.fields.prominent?.booleanValue ?: false
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

        sponsorRepository.allSync().map { it.id }
            .subtract(sponsorsAndRepresentativeIds.map { it.first.id })
            .forEach { sponsorRepository.remove(it) }

        sponsorGroupRepository.allSync().map { it.id }
            .subtract(sponsorGroupsToSponsorDtos.map { it.first.id })
            .forEach { sponsorGroupRepository.remove(it) }

        sponsorGroupsToSponsorDtos.forEach { (group, _) ->
            sponsorGroupRepository.addOrUpdate(group)
        }

        sponsorsAndRepresentativeIds.forEach { (sponsor, representativeIds) ->
            sponsorRepository.addOrUpdate(sponsor)

            profileRepository.setSponsorRepresentatives(sponsor, representativeIds)
        }
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
            Seed, Api
        }

        suspend fun getSpeakers(): List<SpeakersDto.SpeakerDto>

        suspend fun getSchedule(): List<ScheduleDto.DayDto>

        suspend fun getSponsorSessions(): List<SponsorSessionsDto.SessionGroupDto>

        suspend fun getSponsors(): SponsorsDto.SponsorCollectionDto
    }
}
