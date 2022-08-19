package co.touchlab.droidcon.viewmodel.settings

import co.touchlab.droidcon.application.gateway.SettingsGateway
import co.touchlab.droidcon.decompose.interfaceLock
import co.touchlab.droidcon.decompose.whileStarted
import co.touchlab.droidcon.util.DcDispatchers
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.reduce

class SettingsComponent(
    componentContext: ComponentContext,
    dispatchers: DcDispatchers,
    private val settingsGateway: SettingsGateway,
    aboutFactory: AboutComponent.Factory,
): ComponentContext by componentContext {

    private val instanceLock = interfaceLock(dispatchers.main)

    private val _model = MutableValue(Model())
    val model: Value<Model> get() = _model

    val about = aboutFactory.create(componentContext = childContext(key = "about"))

    init {
        whileStarted(dispatchers.main) {
            settingsGateway.settings().collect {
                _model.value =
                    Model(
                        isFeedbackEnabled = it.isFeedbackEnabled,
                        isRemindersEnabled = it.isRemindersEnabled,
                        useComposeForIos = it.useComposeForIos
                    )
            }
        }
    }

    fun setFeedbackEnabled(enabled: Boolean) {
        _model.reduce { it.copy(isFeedbackEnabled = enabled) }

        instanceLock.runExclusively {
            settingsGateway.setFeedbackEnabled(enabled)
        }
    }

    fun setRemindersEnabled(enabled: Boolean) {
        _model.reduce { it.copy(isRemindersEnabled = enabled) }

        instanceLock.runExclusively {
            settingsGateway.setRemindersEnabled(enabled)
        }
    }

    fun setUseComposeForIos(enabled: Boolean) {
        _model.reduce { it.copy(useComposeForIos = enabled) }

        instanceLock.runExclusively {
            settingsGateway.setUseComposeForIos(enabled)
        }
    }

    data class Model(
        val isFeedbackEnabled: Boolean = false,
        val isRemindersEnabled: Boolean = false,
        val useComposeForIos: Boolean = false,
    )

    class Factory(
        private val dispatchers: DcDispatchers,
        private val settingsGateway: SettingsGateway,
        private val aboutFactory: AboutComponent.Factory,
    ) {

        fun create(componentContext: ComponentContext) = SettingsComponent(componentContext, dispatchers, settingsGateway, aboutFactory)
    }
}
