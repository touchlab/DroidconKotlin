package co.touchlab.droidcon.domain.service.impl

import co.touchlab.droidcon.composite.Url
import co.touchlab.droidcon.domain.entity.Profile
import co.touchlab.droidcon.domain.entity.Room
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.repository.ProfileRepository
import co.touchlab.droidcon.domain.repository.RoomRepository
import co.touchlab.droidcon.domain.repository.SessionRepository
import co.touchlab.droidcon.domain.service.DateTimeService
import co.touchlab.droidcon.domain.service.SyncService
import co.touchlab.droidcon.domain.service.fromConferenceDateTime
import co.touchlab.droidcon.domain.service.impl.dto.ScheduleDto
import co.touchlab.droidcon.domain.service.impl.dto.SpeakersDto
import co.touchlab.droidcon.domain.service.impl.dto.SpeakersDto.LinkType
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import kotlinx.datetime.LocalDateTime

@OptIn(ExperimentalSettingsApi::class)
class SessionizeSyncService(
    private val settings: ObservableSettings,
    private val dateTimeService: DateTimeService,
    private val profileRepository: ProfileRepository,
    private val sessionRepository: SessionRepository,
    private val roomRepository: RoomRepository,
    private val seedDataSource: DataSource,
    private val apiDataSource: DataSource,
): SyncService {
    private companion object {
        private const val LOCAL_REPOSITORIES_SEEDED_KEY = "LOCAL_REPOSITORIES_SEEDED"
    }

    private var isLocalRepositoriesSeeded: Boolean
        get() = settings[LOCAL_REPOSITORIES_SEEDED_KEY, false]
        set(value) {
            settings[LOCAL_REPOSITORIES_SEEDED_KEY] = value
        }

    override suspend fun runSynchronization() {
        seedLocalRepositoriesIfNeeded()

        // TODO: Keep local database synchronized with Sessionize.

        // TODO: Send RSVP to a remote server.

        // TODO: Send Feedback to a remote server.
    }

    private suspend fun seedLocalRepositoriesIfNeeded() {
        if (isLocalRepositoriesSeeded) {
            return
        }

        updateRepositoriesFromDataSource(seedDataSource)

        isLocalRepositoriesSeeded = true
    }

    private suspend fun updateRepositoriesFromDataSource(dataSource: DataSource) {
        updateSpeakersFromDataSource(dataSource)
        updateScheduleFromDataSource(dataSource)
    }

    private suspend fun updateSpeakersFromDataSource(dataSource: DataSource) {
        val speakerDtos = dataSource.getSpeakers()
        val profiles = speakerDtos.map(::profileFactory)

        profiles.forEach {
            profileRepository.addOrUpdate(it)
        }
    }

    private suspend fun updateScheduleFromDataSource(dataSource: DataSource) {
        val days = dataSource.getSchedule()
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
                    isAttending = false,
                    feedback = null,
                ) to dto.speakers.map { Profile.Id(it.id) }
            }
        }

        rooms.forEach { room ->
            roomRepository.addOrUpdate(room)
        }

        sessionsAndSpeakers.forEach { (updatedSession, speakers) ->
            val existingSession = sessionRepository.find(updatedSession.id)
            if (existingSession != null) {
                updatedSession.isAttending = existingSession.isAttending
                updatedSession.feedback = existingSession.feedback
                sessionRepository.update(updatedSession)
            } else {
                sessionRepository.add(updatedSession)
            }

            profileRepository.setSessionSpeakers(updatedSession, speakers)
        }
    }

    private fun profileFactory(speakerDto: SpeakersDto.SpeakerDto): Profile {
        val groupedLinks = speakerDto.links.filter { it.url.isNotBlank() } .groupBy { it.linkType }
        return Profile(
            id = Profile.Id(speakerDto.id),
            fullName = speakerDto.fullName,
            bio = speakerDto.bio,
            tagLine = speakerDto.tagLine,
            profilePicture = speakerDto.profilePicture.let(::Url),
            twitter = groupedLinks[LinkType.Twitter]?.firstOrNull()?.url?.let(::Url),
            linkedIn = groupedLinks[LinkType.LinkedIn]?.firstOrNull()?.url?.let(::Url),
            website = (
                groupedLinks[LinkType.CompanyWebsite] ?: groupedLinks[LinkType.Blog] ?: groupedLinks[LinkType.Other]
            )?.firstOrNull()?.url?.let(::Url),
        )
    }

    interface DataSource {
        enum class Kind {
            Api, Seed
        }

        suspend fun getSpeakers(): List<SpeakersDto.SpeakerDto>

        suspend fun getSchedule(): List<ScheduleDto.DayDto>
    }
}
