package co.touchlab.sessionize.api

import co.touchlab.droidcon.db.MySessions


interface NotificationsApi {

    fun scheduleReminderNotificationsForSessions(sessions:List<MySessions>)

    fun scheduleFeedbackNotificationsForSessions(sessions:List<MySessions>)

    fun cancelReminderNotifications(andDismissals: Boolean)

    fun cancelFeedbackNotifications()

    suspend fun initializeNotifications(onSuccess: suspend (Boolean) -> Unit)

    fun deinitializeNotifications()

}