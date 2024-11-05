package co.touchlab.droidcon.domain.service.impl

import co.touchlab.droidcon.domain.service.UserIdProvider
import com.benasher44.uuid.uuid4
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.get
import com.russhwolf.settings.set

@OptIn(ExperimentalSettingsApi::class)
class DefaultUserIdProvider(private val observableSettings: ObservableSettings) : UserIdProvider {
    companion object {
        const val USER_ID_KEY = "USER_ID_KEY"
    }

    override suspend fun getId(): String {
        var id = observableSettings.get<String>(USER_ID_KEY)
        if (id == null) {
            id = uuid4().toString()
            observableSettings[USER_ID_KEY] = id
        }
        return id
    }
}
