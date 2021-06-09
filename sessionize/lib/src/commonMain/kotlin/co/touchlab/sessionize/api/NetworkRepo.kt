package co.touchlab.sessionize.api

import co.touchlab.sessionize.BaseModel
import co.touchlab.sessionize.Durations
import co.touchlab.sessionize.ServiceRegistry
import co.touchlab.sessionize.SettingsKeys
import co.touchlab.sessionize.db.SessionizeDbHelper
import co.touchlab.sessionize.platform.NotificationsModel.createNotifications
import co.touchlab.sessionize.platform.NotificationsModel.notificationsEnabled
import co.touchlab.sessionize.platform.currentTimeMillis
import co.touchlab.sessionize.platform.printThrowable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.native.concurrent.ThreadLocal


@ThreadLocal
object NetworkRepo {
    fun dataCalls() = CoroutineScope(ServiceRegistry.coroutinesDispatcher).mainScope.launch {
        try {
            val api = ServiceRegistry.sessionizeApi
            val networkSpeakerJson = api.getSpeakersJson()
            val networkSessionJson = api.getSessionsJson()
            val networkSponsorSessionJson = api.getSponsorSessionJson()

            callPrimeAll(networkSpeakerJson, networkSessionJson, networkSponsorSessionJson)

            //If we do some kind of data re-load after a user logs in, we'll need to update this.
            //We assume for now that when the app first starts, you have nothing rsvp'd
            if (notificationsEnabled) {
                createNotifications()
            }
        } catch (e: Exception) {
            printThrowable(e)
        }
    }

    internal suspend fun callPrimeAll(
        networkSpeakerJson: String,
        networkSessionJson: String,
        networkSponsorSessionJson: String
    ) = withContext(ServiceRegistry.backgroundDispatcher) {
        SessionizeDbHelper.primeAll(
            networkSpeakerJson,
            networkSessionJson,
            networkSponsorSessionJson
        )
        ServiceRegistry.appSettings.putLong(SettingsKeys.KEY_LAST_LOAD, currentTimeMillis())
    }

    fun refreshData() {
        if (!ServiceRegistry.appSettings.getBoolean(SettingsKeys.KEY_FIRST_RUN, true)) {
            val lastLoad = ServiceRegistry.appSettings.getLong(SettingsKeys.KEY_LAST_LOAD)
            if (lastLoad < (currentTimeMillis() - (Durations.TWO_HOURS_MILLIS.toLong()))) {
                dataCalls()
            }
        }
    }

    fun sendFeedback() = CoroutineScope(ServiceRegistry.coroutinesDispatcher).mainScope.launch {
        try {
            SessionizeDbHelper.sendFeedback()
        } catch (e: Throwable) {
            ServiceRegistry.softExceptionCallback(e, "Feedback Send Failed")
        }
    }

    private class CoroutineScope(mainContext: CoroutineContext) : BaseModel(mainContext)
}