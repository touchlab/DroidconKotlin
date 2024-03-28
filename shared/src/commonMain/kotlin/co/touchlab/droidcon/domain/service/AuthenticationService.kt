package co.touchlab.droidcon.domain.service

import co.touchlab.droidcon.UserContext
import co.touchlab.droidcon.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class AuthenticationService(isSignedIn: Boolean) : KoinComponent {
    private val userIdProvider: UserIdProvider by inject()

    private val _isAuthenticated = MutableStateFlow(isSignedIn)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    abstract fun performGoogleLogin(): Boolean
    open fun performLogout(): Boolean {
        updateCredentials(false, "", null, null, null)
        return true
    }

    fun updateCredentials(
        isAuthenticated: Boolean,
        id: String,
        name: String?,
        email: String?,
        pictureUrl: String?,
    ) {
        _isAuthenticated.update { isAuthenticated }
        userIdProvider.saveUserContext(
            UserContext(
                isAuthenticated = isAuthenticated,
                userData = if (isAuthenticated) {
                    UserData(
                        id = id,
                        name = name,
                        email = email,
                        pictureUrl = pictureUrl,
                    )
                } else {
                    null
                }
            )
        )
    }
}
