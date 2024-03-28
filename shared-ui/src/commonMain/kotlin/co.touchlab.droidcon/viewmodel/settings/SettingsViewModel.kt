package co.touchlab.droidcon.viewmodel.settings

import co.touchlab.droidcon.application.gateway.SettingsGateway
import co.touchlab.droidcon.domain.service.AuthenticationService
import org.brightify.hyperdrive.multiplatformx.BaseViewModel

class SettingsViewModel(
    settingsGateway: SettingsGateway,
    private val authenticationService: AuthenticationService,
    private val aboutFactory: AboutViewModel.Factory,
) : BaseViewModel() {

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

    var useCompose: Boolean by binding(
        settingsGateway.settings(),
        mapping = { it.useComposeForIos },
        set = { newValue ->
            // TODO: Remove when `binding` supports suspend closures.
            instanceLock.runExclusively {
                settingsGateway.setUseComposeForIos(newValue)
            }
        }
    )
    val observeUseCompose by observe(::useCompose)

    var isAuthenticated: Boolean by binding(
        authenticationService.isAuthenticated,
        mapping = { it },
        set = { }
    )
    val observeIsAuthenticated by observe(::isAuthenticated)

    fun signIn() = authenticationService.performGoogleLogin()
    fun signOut() = authenticationService.performLogout()

    class Factory(
        private val settingsGateway: SettingsGateway,
        private val authenticationService: AuthenticationService,
        private val aboutFactory: AboutViewModel.Factory,
    ) {

        fun create() = SettingsViewModel(settingsGateway, authenticationService, aboutFactory)
    }
}
