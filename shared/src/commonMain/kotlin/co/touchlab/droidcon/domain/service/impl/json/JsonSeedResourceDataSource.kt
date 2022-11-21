package co.touchlab.droidcon.domain.service.impl.json

import co.touchlab.droidcon.domain.service.impl.DefaultSyncService
import co.touchlab.droidcon.domain.service.impl.dto.ScheduleDto
import co.touchlab.droidcon.domain.service.impl.dto.SpeakersDto
import co.touchlab.droidcon.domain.service.impl.dto.SponsorSessionsDto
import co.touchlab.droidcon.domain.service.impl.dto.SponsorsDto
import kotlinx.serialization.builtins.ListSerializer

class JsonSeedResourceDataSource(
    private val jsonResourceReader: JsonResourceReader
) : DefaultSyncService.DataSource {

    override suspend fun getSpeakers(): List<SpeakersDto.SpeakerDto> {
        return jsonResourceReader.readAndDecodeResource("speakers.json", ListSerializer(SpeakersDto.SpeakerDto.serializer()))
    }

    override suspend fun getSchedule(): List<ScheduleDto.DayDto> {
        return jsonResourceReader.readAndDecodeResource("schedule.json", ListSerializer(ScheduleDto.DayDto.serializer()))
    }

    override suspend fun getSponsorSessions(): List<SponsorSessionsDto.SessionGroupDto> {
        return jsonResourceReader.readAndDecodeResource(
            "sponsor_sessions.json",
            ListSerializer(SponsorSessionsDto.SessionGroupDto.serializer())
        )
    }

    override suspend fun getSponsors(): SponsorsDto.SponsorCollectionDto {
        return jsonResourceReader.readAndDecodeResource(
            "sponsors.json",
            SponsorsDto.SponsorCollectionDto.serializer(),
        )
    }
}
