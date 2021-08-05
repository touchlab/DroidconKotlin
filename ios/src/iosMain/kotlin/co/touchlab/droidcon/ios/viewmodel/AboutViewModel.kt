package co.touchlab.droidcon.ios.viewmodel

import co.touchlab.droidcon.application.composite.AboutItem
import co.touchlab.droidcon.application.repository.AboutRepository
import org.brightify.hyperdrive.multiplatformx.BaseViewModel

class AboutViewModel(
    private val aboutRepository: AboutRepository,
): BaseViewModel() {
    var items: List<AboutItem> by published(emptyList())
        private set

    override suspend fun whileAttached() {
        items = aboutRepository.getAboutItems()
    }

    class Factory(private val aboutRepository: AboutRepository) {
        fun create() = AboutViewModel(aboutRepository)
    }
}