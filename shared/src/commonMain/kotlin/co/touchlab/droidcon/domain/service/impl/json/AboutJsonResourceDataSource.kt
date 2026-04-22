package co.touchlab.droidcon.domain.service.impl.json

import co.touchlab.droidcon.domain.service.impl.dto.AboutDto
import kotlinx.serialization.builtins.ListSerializer

class AboutJsonResourceDataSource(private val jsonResourceReader: JsonResourceReader) {

    suspend fun getAboutItems(): List<AboutDto.AboutItemDto> =
        jsonResourceReader.readAndDecodeResource("files/about.json", ListSerializer(AboutDto.AboutItemDto.serializer()))
}
