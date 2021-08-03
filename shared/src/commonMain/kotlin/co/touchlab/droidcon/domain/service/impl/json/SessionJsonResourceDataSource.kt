package co.touchlab.droidcon.domain.service.impl.json

import co.touchlab.droidcon.domain.service.impl.SessionizeSyncService
import co.touchlab.droidcon.domain.service.impl.dto.ScheduleDto
import co.touchlab.droidcon.domain.service.impl.dto.SpeakersDto
import kotlinx.serialization.builtins.ListSerializer

class SessionJsonResourceDataSource(
    private val jsonResourceReader: JsonResourceReader
): SessionizeSyncService.DataSource {

    override suspend fun getSpeakers(): List<SpeakersDto.SpeakerDto> {
        return jsonResourceReader.readAndDecodeResource("speakers.json", ListSerializer(SpeakersDto.SpeakerDto.serializer()))
    }

    override suspend fun getSchedule(): List<ScheduleDto.DayDto> {
        return jsonResourceReader.readAndDecodeResource("schedule.json", ListSerializer(ScheduleDto.DayDto.serializer()))
    }
}