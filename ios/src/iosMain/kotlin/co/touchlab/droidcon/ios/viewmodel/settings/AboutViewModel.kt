package co.touchlab.droidcon.ios.viewmodel.settings

import co.touchlab.droidcon.application.composite.AboutItem
import co.touchlab.droidcon.application.repository.AboutRepository
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.brightify.hyperdrive.multiplatformx.BaseViewModel

class AboutViewModel(
    private val aboutRepository: AboutRepository,
): BaseViewModel() {

    var items: List<AboutItem> by published(emptyList())
        private set

    var itemViewModels: List<AboutItemViewModel> by published(emptyList())
    val observeItemViewModels by observe(::itemViewModels)

    init {
        MainScope().launch {
            itemViewModels = aboutRepository.getAboutItems().map {
                val links = parseUrl(it.detail)
                AboutItemViewModel(it.title, it.detail, links, it.icon)
            }
        }
    }

    override suspend fun whileAttached() {
        items = aboutRepository.getAboutItems()
    }

    private fun parseUrl(text: String): List<WebLink> {
        val urlRegex =
            "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)".toRegex()
        return urlRegex.findAll(text).map { WebLink(it.range, it.value) }.toList()
    }

    class Factory(private val aboutRepository: AboutRepository) {

        fun create() = AboutViewModel(aboutRepository)
    }
}
