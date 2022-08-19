package co.touchlab.droidcon.viewmodel.settings

import co.touchlab.droidcon.application.repository.AboutRepository
import co.touchlab.droidcon.decompose.whileStarted
import co.touchlab.droidcon.dto.WebLink
import co.touchlab.droidcon.service.ParseUrlViewService
import co.touchlab.droidcon.util.DcDispatchers
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.reduce

class AboutComponent(
    componentContext: ComponentContext,
    dispatchers: DcDispatchers,
    private val aboutRepository: AboutRepository,
    private val parseUrlViewService: ParseUrlViewService,
): ComponentContext by componentContext {

    private val _model = MutableValue(Model())
    val model: Value<Model> get() = _model

    init {
        whileStarted(dispatchers.main) {
            val items =
                aboutRepository
                    .getAboutItems()
                    .map { item -> Model.Item(item.title, item.detail, parseUrlViewService.parse(item.detail), item.icon) }

            _model.reduce { it.copy(items = items) }
        }
    }

    data class Model(
        val items: List<Item> = emptyList(),
    ) {

        data class Item(
            val title: String,
            val detail: String,
            val webLinks: List<WebLink>,
            val icon: String,
        )
    }

    class Factory(
        private val dispatchers: DcDispatchers,
        private val aboutRepository: AboutRepository,
        private val parseUrlViewService: ParseUrlViewService,
    ) {

        fun create(componentContext: ComponentContext) =
            AboutComponent(componentContext, dispatchers, aboutRepository, parseUrlViewService)
    }
}
