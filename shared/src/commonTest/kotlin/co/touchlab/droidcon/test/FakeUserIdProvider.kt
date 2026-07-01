package co.touchlab.droidcon.test

import co.touchlab.droidcon.domain.service.UserIdProvider

class FakeUserIdProvider(private val userId: String) : UserIdProvider {
    override suspend fun getId(): String = userId
}
