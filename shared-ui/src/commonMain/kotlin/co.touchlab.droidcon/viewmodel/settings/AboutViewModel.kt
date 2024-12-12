package co.touchlab.droidcon.viewmodel.settings

import co.touchlab.droidcon.application.composite.AboutItem
import co.touchlab.droidcon.application.repository.AboutRepository
import co.touchlab.droidcon.service.ParseUrlViewService
import org.brightify.hyperdrive.multiplatformx.BaseViewModel

class AboutViewModel(private val aboutRepository: AboutRepository, private val parseUrlViewService: ParseUrlViewService) : BaseViewModel() {

    var items: List<AboutItem> by published(emptyList())
        private set

    var itemViewModels: List<AboutItemViewModel> by published(emptyList())
    val observeItemViewModels by observe(::itemViewModels)

    override suspend fun whileAttached() {
        items = aboutRepository.getAboutItems()
        itemViewModels = items.map {
            val links = parseUrlViewService.parse(it.detail)
            AboutItemViewModel(it.title, it.detail, links, it.icon)
        }
    }

    class Factory(private val aboutRepository: AboutRepository, private val parseUrlViewService: ParseUrlViewService) {

        fun create() = AboutViewModel(aboutRepository, parseUrlViewService)
    }
}
