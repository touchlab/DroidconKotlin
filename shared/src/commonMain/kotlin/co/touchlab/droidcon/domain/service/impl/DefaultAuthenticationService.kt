package co.touchlab.droidcon.domain.service.impl

import co.touchlab.droidcon.UserContext
import co.touchlab.droidcon.UserData
import co.touchlab.droidcon.domain.service.AuthenticationService
import co.touchlab.droidcon.domain.service.UserIdProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DefaultAuthenticationService : AuthenticationService, KoinComponent {

    private val userIdProvider: UserIdProvider by inject()

    private val _isAuthenticated = MutableStateFlow(false)
    override val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    private val _email = MutableStateFlow<String?>(null)
    override val email: StateFlow<String?> = _email

    override fun setCredentials(
        id: String,
        name: String?,
        email: String?,
        pictureUrl: String?,
    ) {
        _isAuthenticated.update { true }
        _email.update { email }
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

    override fun clearCredentials() {
        _isAuthenticated.update { false }
        _email.update { null }
        userIdProvider.saveUserContext(
            UserContext(
                isAuthenticated = false,
                userData = null
            )
        )
    }
}