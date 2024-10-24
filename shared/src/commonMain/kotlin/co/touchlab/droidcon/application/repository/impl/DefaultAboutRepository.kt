package co.touchlab.droidcon.application.repository.impl

import co.touchlab.droidcon.application.composite.AboutItem
import co.touchlab.droidcon.application.repository.AboutRepository
import co.touchlab.droidcon.domain.service.impl.json.AboutJsonResourceDataSource

class DefaultAboutRepository(private val aboutJsonResourceDataSource: AboutJsonResourceDataSource) : AboutRepository {

    override suspend fun getAboutItems(): List<AboutItem> = aboutJsonResourceDataSource.getAboutItems().map {
        AboutItem(
            icon = it.icon,
            title = it.title,
            detail = it.detail,
        )
    }
}
