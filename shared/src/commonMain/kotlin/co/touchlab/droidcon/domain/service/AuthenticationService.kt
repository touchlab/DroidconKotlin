package co.touchlab.droidcon.domain.service

import kotlinx.coroutines.flow.StateFlow

interface AuthenticationService {
    val isAuthenticated: StateFlow<Boolean>
    val email: StateFlow<String?>

    fun setCredentials(
        id: String,
        name: String?,
        email: String?,
        pictureUrl: String?,
    )
    fun clearCredentials()
}
