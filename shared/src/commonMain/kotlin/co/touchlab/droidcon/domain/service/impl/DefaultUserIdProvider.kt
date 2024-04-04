package co.touchlab.droidcon.domain.service.impl

import co.touchlab.droidcon.UserContext
import co.touchlab.droidcon.domain.service.UserIdProvider
import com.benasher44.uuid.uuid4
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.get
import com.russhwolf.settings.set

@OptIn(ExperimentalSettingsApi::class)
class DefaultUserIdProvider(
    private val observableSettings: ObservableSettings,
) : UserIdProvider {
    companion object {
        const val USER_ID_KEY = "USER_ID_KEY"
        const val FIREBASE_USER_ID_KEY = "FIREBASE_USER_ID_KEY"
    }

    override suspend fun getId(): String {
        var localId = observableSettings.get<String>(USER_ID_KEY)
        val firebaseId = observableSettings.get<String>(FIREBASE_USER_ID_KEY)

        if (firebaseId != null && firebaseId != localId) {
            // TODO: Update RSVPs and Feedback calls with new ID
            observableSettings[USER_ID_KEY] = firebaseId
            localId = firebaseId
        } else if (localId == null) {
            localId = uuid4().toString()
            observableSettings[USER_ID_KEY] = localId
        }
        return localId
    }

    override fun saveUserContext(userContext: UserContext) {
        userContext.userData?.let {
            observableSettings[FIREBASE_USER_ID_KEY] = it.id
            observableSettings[USER_ID_KEY] = it.id
        }
    }
}
