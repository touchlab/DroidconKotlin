package co.touchlab.sessionize.api

import co.touchlab.sessionize.BaseModel
import co.touchlab.sessionize.Durations
import co.touchlab.sessionize.SettingsKeys
import co.touchlab.sessionize.backgroundDispatcher
import co.touchlab.sessionize.db.SessionizeDbHelper
import co.touchlab.sessionize.platform.NotificationsModel
import co.touchlab.sessionize.platform.currentTimeMillis
import co.touchlab.sessionize.softExceptionCallback
import com.russhwolf.settings.Settings
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NetworkRepo(
    private val dbHelper: SessionizeDbHelper,
    private val sessionizeApi: SessionizeApi,
    private val appSettings: Settings,
    private val notificationsModel: NotificationsModel
): BaseModel() {
    fun dataCalls() = mainScope.launch {
        try {
            val api = sessionizeApi
            val networkSpeakerJson = api.getSpeakersJson()
            val networkSessionJson = api.getSessionsJson()
            val networkSponsorSessionJson =  api.getSponsorSessionJson()

            callPrimeAll(networkSpeakerJson, networkSessionJson, networkSponsorSessionJson)

            //If we do some kind of data re-load after a user logs in, we'll need to update this.
            //We assume for now that when the app first starts, you have nothing rsvp'd
            if (notificationsModel.notificationsEnabled) {
                notificationsModel.createNotifications()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    internal suspend fun callPrimeAll(networkSpeakerJson:String,
                                      networkSessionJson:String,
                                      networkSponsorSessionJson:String
                                      ) = withContext(backgroundDispatcher){
        dbHelper.primeAll(networkSpeakerJson, networkSessionJson, networkSponsorSessionJson)
        appSettings.putLong(SettingsKeys.KEY_LAST_LOAD, currentTimeMillis())
    }

    fun refreshData() {
        if (!appSettings.getBoolean(SettingsKeys.KEY_FIRST_RUN, true)) {
            val lastLoad = appSettings.getLong(SettingsKeys.KEY_LAST_LOAD)
            if (lastLoad < (currentTimeMillis() - (Durations.TWO_HOURS_MILLIS.toLong()))) {
                dataCalls()
            }
        }
    }

    fun sendFeedback() = mainScope.launch {
        try {
            dbHelper.sendFeedback()
        } catch (e: Throwable) {
            softExceptionCallback(e, "Feedback Send Failed")
        }
    }
}