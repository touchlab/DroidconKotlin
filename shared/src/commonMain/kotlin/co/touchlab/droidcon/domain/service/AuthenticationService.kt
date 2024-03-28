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
        clearCredentials()
        return true
    }

    fun setCredentials(
        id: String,
        name: String?,
        email: String?,
        pictureUrl: String?,
    ) {
        _isAuthenticated.update { true }
        userIdProvider.saveUserContext(
            UserContext(
                isAuthenticated = true,
                userData = UserData(
                    id = id,
                    name = name,
                    email = email,
                    pictureUrl = pictureUrl,
                )
            )
        )
    }

    fun clearCredentials() {
        _isAuthenticated.update { false }
        userIdProvider.saveUserContext(
            UserContext(
                isAuthenticated = false,
                userData = null
            )
        )
    }
}
