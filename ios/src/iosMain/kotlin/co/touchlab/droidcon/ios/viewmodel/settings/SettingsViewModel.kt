package co.touchlab.droidcon.ios.viewmodel.settings

import co.touchlab.droidcon.application.gateway.SettingsGateway
import org.brightify.hyperdrive.multiplatformx.BaseViewModel

class SettingsViewModel(
    settingsGateway: SettingsGateway,
    private val aboutFactory: AboutViewModel.Factory,
): BaseViewModel() {

    var isFeedbackEnabled by binding(
        settingsGateway.settings(),
        mapping = { it.isFeedbackEnabled },
        set = { newValue ->
            // TODO: Remove when `binding` supports suspend closures.
            instanceLock.runExclusively {
                settingsGateway.setFeedbackEnabled(newValue)
            }
        }
    )
    val observeIsFeedbackEnabled by observe(::isFeedbackEnabled)

    var isRemindersEnabled by binding(
        settingsGateway.settings(),
        mapping = { it.isRemindersEnabled },
        set = { newValue ->
            // TODO: Remove when `binding` supports suspend closures.
            instanceLock.runExclusively {
                settingsGateway.setRemindersEnabled(newValue)
            }
        }
    )
    val observeIsRemindersEnabled by observe(::isRemindersEnabled)

	val about by managed(aboutFactory.create())

    class Factory(
        private val settingsGateway: SettingsGateway,
        private val aboutFactory: AboutViewModel.Factory,
    ) {
        fun create() = SettingsViewModel(settingsGateway, aboutFactory)
    }
}
