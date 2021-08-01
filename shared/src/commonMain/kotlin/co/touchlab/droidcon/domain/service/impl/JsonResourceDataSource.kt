package co.touchlab.droidcon.domain.service.impl

import co.touchlab.droidcon.domain.service.impl.dto.ScheduleDto
import co.touchlab.droidcon.domain.service.impl.dto.SpeakersDto
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

class JsonResourceDataSource(
    private val resourceReader: ResourceReader,
    private val json: Json,
): SessionizeSyncService.DataSource {

    override suspend fun getSpeakers(): List<SpeakersDto.SpeakerDto> {
        return decodeResource("speakers.json", ListSerializer(SpeakersDto.SpeakerDto.serializer()))
    }

    override suspend fun getSchedule(): List<ScheduleDto.DayDto> {
        return decodeResource("schedule.json", ListSerializer(ScheduleDto.DayDto.serializer()))
    }

    private fun <T> decodeResource(name: String, strategy: DeserializationStrategy<T>): T {
        val text = resourceReader.readResource(name)
        return json.decodeFromString(strategy, text)
    }
}