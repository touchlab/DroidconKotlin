package co.touchlab.droidcon.domain.service

import co.touchlab.droidcon.UserContext

interface UserIdProvider {
    suspend fun getId(): String

    fun saveUserContext(userContext: UserContext)
}
