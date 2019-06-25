package co.touchlab.sessionize.api

import co.touchlab.sessionize.BaseModel
import co.touchlab.sessionize.Durations
import co.touchlab.sessionize.ServiceRegistry
import co.touchlab.sessionize.SettingsKeys
import co.touchlab.sessionize.db.SessionizeDbHelper
import co.touchlab.sessionize.platform.NotificationsModel.createNotificationsForSessions
import co.touchlab.sessionize.platform.NotificationsModel.notificationsEnabled
import co.touchlab.sessionize.platform.backgroundSuspend
import co.touchlab.sessionize.platform.currentTimeMillis
import co.touchlab.sessionize.platform.logException
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.native.concurrent.ThreadLocal


@ThreadLocal
object NetworkRepo {
    fun dataCalls() = CoroutineScope(ServiceRegistry.coroutinesDispatcher).launch {
        try {
            val api = ServiceRegistry.sessionizeApi
            val networkSpeakerJson = api.getSpeakersJson()
            val networkSessionJson = api.getSessionsJson()
            val networkSponsorJson = api.getSponsorJson()
            val networkSponsorSessionJson =  api.getSponsorSessionJson()

            backgroundSuspend {
                SessionizeDbHelper.primeAll(networkSpeakerJson, networkSessionJson, networkSponsorJson, networkSponsorSessionJson)
                ServiceRegistry.appSettings.putLong(SettingsKeys.KEY_LAST_LOAD, currentTimeMillis())
            }

            //If we do some kind of data re-load after a user logs in, we'll need to update this.
            //We assume for now that when the app first starts, you have nothing rsvp'd
            if (notificationsEnabled()) {
                createNotificationsForSessions()
            }
        } catch (e: Exception) {
            logException(e)
        }
    }

    fun refreshData() {
        if (!ServiceRegistry.appSettings.getBoolean(SettingsKeys.KEY_FIRST_RUN, true)) {
            val lastLoad = ServiceRegistry.appSettings.getLong(SettingsKeys.KEY_LAST_LOAD)
            if (lastLoad < (currentTimeMillis() - (Durations.TWO_HOURS_MILLIS.toLong()))) {
                dataCalls()
            }
        }
    }

    fun sendFeedback() = CoroutineScope(ServiceRegistry.coroutinesDispatcher).launch {
        try {
            SessionizeDbHelper.sendFeedback()
        } catch (e: Throwable) {
            ServiceRegistry.softExceptionCallback(e, "Feedback Send Failed")
        }
    }

    private class CoroutineScope(mainContext: CoroutineContext) : BaseModel(mainContext)
}