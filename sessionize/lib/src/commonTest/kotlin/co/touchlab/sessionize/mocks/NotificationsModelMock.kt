package co.touchlab.sessionize.mocks

import co.touchlab.droidcon.db.MySessions
import co.touchlab.sessionize.platform.INotificationsModel

class NotificationsModelMock : INotificationsModel {
    var calledReminderDisabled: Boolean = false
    var calledReminderEnabled: Boolean = false
    var calledFeedbackDisabled: Boolean = false
    var calledRecreateFeedback: Boolean = false
    var calledRecreateReminder: Boolean = false
    var calledFeedbackEnabled: Boolean = false
    var mockNotificationsEnabled = true

    override fun notificationsEnabled(): Boolean {
        return mockNotificationsEnabled
    }

    override fun feedbackEnabled(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun remindersEnabled(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setNotificationsEnabled(enabled: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setRemindersEnabled(enabled: Boolean) {
        if (enabled) calledReminderEnabled = true
        else calledReminderDisabled = true
    }

    override fun setFeedbackEnabled(enabled: Boolean) {
        if (enabled) calledFeedbackEnabled = true
        else calledFeedbackDisabled = true
    }

    override fun createNotifications() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun cancelNotifications() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun cancelFeedbackNotifications() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun cancelReminderNotifications(andDismissals: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun recreateReminderNotifications() {
        calledRecreateReminder = true
    }

    override fun recreateFeedbackNotifications() {
        calledRecreateFeedback = true
    }

    override fun getReminderTimeFromSession(session: MySessions): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getReminderNotificationTitle(session: MySessions): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getReminderNotificationMessage(session: MySessions): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getFeedbackTimeFromSession(session: MySessions): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getFeedbackNotificationTitle(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getFeedbackNotificationMessage(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}