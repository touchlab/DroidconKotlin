package co.touchlab.sessionize.mocks

import co.touchlab.droidcon.db.MySessions
import co.touchlab.sessionize.api.NotificationsApi

class NotificationsApiMock : NotificationsApi {
    var initializeCalled = false
    var reminderCalled = false
    var feedbackCalled = false
    var reminderCancelled = false
    var feedbackCancelled = false

    override fun scheduleReminderNotificationsForSessions(sessions: List<MySessions>) {
        reminderCalled = true
    }

    override fun scheduleFeedbackNotificationsForSessions(sessions: List<MySessions>) {
        feedbackCalled = true
    }

    override fun cancelReminderNotifications(andDismissals: Boolean) {
        reminderCancelled = true
    }

    override fun cancelFeedbackNotifications() {
        feedbackCancelled = true
    }

    override fun initializeNotifications(onSuccess: (Boolean) -> Unit) {
        initializeCalled = true
        onSuccess(true)
    }

    override fun deinitializeNotifications() {
    }
}