package co.touchlab.sessionize.mocks

import co.touchlab.droidcon.db.MySessions
import co.touchlab.sessionize.api.NotificationsApi
import co.touchlab.stately.concurrency.AtomicBoolean

class NotificationsApiMock : NotificationsApi {
    private var initializeCalled:AtomicBoolean = AtomicBoolean(false)
    var reminderCalled:AtomicBoolean = AtomicBoolean( false)
    private var feedbackCalled:AtomicBoolean = AtomicBoolean( false)
    private var reminderCancelled:AtomicBoolean = AtomicBoolean(false)
    private var feedbackCancelled:AtomicBoolean = AtomicBoolean(false)

    override fun scheduleReminderNotificationsForSessions(sessions: List<MySessions>) {
        reminderCalled.value = true
    }

    override fun scheduleFeedbackNotificationsForSessions(sessions: List<MySessions>) {
        feedbackCalled.value = true
    }

    override fun cancelReminderNotifications(andDismissals: Boolean) {
        reminderCancelled.value = true
    }

    override fun cancelFeedbackNotifications() {
        feedbackCancelled.value = true
    }

    override fun initializeNotifications(onSuccess: (Boolean) -> Unit) {
        initializeCalled.value = true
        onSuccess(true)
    }

    override fun deinitializeNotifications() {
    }
}