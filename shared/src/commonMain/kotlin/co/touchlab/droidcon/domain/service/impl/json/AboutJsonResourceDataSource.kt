package co.touchlab.droidcon.domain.service.impl.json

import co.touchlab.droidcon.domain.service.impl.dto.AboutDto
import kotlinx.serialization.builtins.ListSerializer

class AboutJsonResourceDataSource(private val jsonResourceReader: JsonResourceReader) {

    fun getAboutItems(): List<AboutDto.AboutItemDto> {
        return jsonResourceReader.readAndDecodeResource(
            "about.json",
            ListSerializer(AboutDto.AboutItemDto.serializer())
        )
    }
}
