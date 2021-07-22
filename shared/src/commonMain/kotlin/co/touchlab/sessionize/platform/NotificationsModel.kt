package co.touchlab.sessionize.platform

import co.touchlab.droidcon.db.MySessions
import co.touchlab.sessionize.BaseModel
import co.touchlab.sessionize.SettingsKeys.FEEDBACK_ENABLED
import co.touchlab.sessionize.SettingsKeys.LOCAL_NOTIFICATIONS_ENABLED
import co.touchlab.sessionize.SettingsKeys.REMINDERS_ENABLED
import co.touchlab.sessionize.api.NotificationsApi
import co.touchlab.sessionize.backgroundDispatcher
import co.touchlab.sessionize.db.SessionizeDbHelper
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.withContext

class NotificationsModel(
    private val appSettings: Settings,
    private val notificationsApi: NotificationsApi,
    private val dbHelper: SessionizeDbHelper
    ): BaseModel() {

    // Settings
    var notificationsEnabled: Boolean
    get() = appSettings.notificationsEnabled
    set(value) {
        appSettings.notificationsEnabled = value
    }

    var feedbackEnabled: Boolean
        get() = appSettings.getBoolean(FEEDBACK_ENABLED, true)
        set(value) {
            appSettings[FEEDBACK_ENABLED] = value
        }

    var remindersEnabled: Boolean
        get() = appSettings.getBoolean(LOCAL_NOTIFICATIONS_ENABLED, true) &&
                appSettings.getBoolean(REMINDERS_ENABLED, true)
        set(value) {
            appSettings[REMINDERS_ENABLED] = value
        }

    suspend fun createNotifications() {
        if (notificationsEnabled) {
            recreateReminderNotifications()
            recreateFeedbackNotifications()
        }
    }

    fun cancelNotifications() {
        cancelReminderNotifications(true)
        cancelFeedbackNotifications()
    }

    fun cancelFeedbackNotifications() =
        notificationsApi.cancelFeedbackNotifications()

    fun cancelReminderNotifications(andDismissals: Boolean) =
        notificationsApi.cancelReminderNotifications(andDismissals)

    suspend fun recreateReminderNotifications() {
        cancelReminderNotifications(false)
        if (remindersEnabled) {
            val mySessions = mySessions()
            if (mySessions.isNotEmpty()) {
                notificationsApi.scheduleReminderNotificationsForSessions(mySessions)
            }
        }
    }

    suspend fun recreateFeedbackNotifications() {
        cancelFeedbackNotifications()
        if (feedbackEnabled) {
            val mySessions = mySessions()
            if (mySessions.isNotEmpty()) {
                notificationsApi.scheduleFeedbackNotificationsForSessions(mySessions)
            }
        }
    }

    private suspend fun mySessions(): List<MySessions> =
        withContext(backgroundDispatcher) {
            dbHelper.sessionQueries.mySessions().executeAsList()
        }
}

var Settings.notificationsEnabled:Boolean
    get() = getBoolean(LOCAL_NOTIFICATIONS_ENABLED, true)
    set(value) {
        set(LOCAL_NOTIFICATIONS_ENABLED, value)
    }