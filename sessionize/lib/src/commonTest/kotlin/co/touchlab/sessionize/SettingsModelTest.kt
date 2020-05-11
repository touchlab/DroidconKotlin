package co.touchlab.sessionize

import co.touchlab.sessionize.mocks.NotificationsApiMock
import co.touchlab.sessionize.platform.NotificationsModel
import kotlinx.coroutines.Dispatchers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/*abstract class SettingsModelTest {
    private val notificationsApiMock = NotificationsApiMock()
    private val notificationsModelMock = NotificationsModel

    lateinit var settingsModel: SettingsModel

    @BeforeTest
    fun setup() {
        settingsModel = SettingsModel(notificationsModelMock, notificationsApiMock, Dispatchers.Main)
    }

    @Test
    fun testFeedbackEnabled() = runTest {
        settingsModel.setFeedbackSettingEnabled(true)
        assertTrue { notificationsModelMock.calledFeedbackEnabled }
        assertFalse { notificationsApiMock.initializeCalled }
        assertTrue { notificationsModelMock.calledRecreateFeedback }
    }

    @Test
    fun testFeedbackDisabled() = runTest {
        settingsModel.setFeedbackSettingEnabled(false)
        assertTrue { notificationsModelMock.calledFeedbackDisabled }
        assertFalse { notificationsApiMock.initializeCalled }
        assertTrue { notificationsModelMock.calledRecreateFeedback }
    }

    @Test
    fun testRemindersEnabled() = runTest {
        settingsModel.setRemindersSettingEnabled(true)
        assertTrue { notificationsModelMock.calledReminderEnabled }
        assertFalse { notificationsApiMock.initializeCalled }
        assertTrue { notificationsModelMock.calledRecreateReminder }
    }

    @Test
    fun testRemindersDisabled() = runTest {
        settingsModel.setRemindersSettingEnabled(false)
        assertTrue { notificationsModelMock.calledReminderDisabled }
        assertFalse { notificationsApiMock.initializeCalled }
        assertTrue { notificationsModelMock.calledRecreateReminder }
    }

    @Test
    fun testRemindersEnabledNotificationsDisabled() = runTest {
        notificationsModelMock.notificationsEnabled = false
        settingsModel.setRemindersSettingEnabled(true)
        assertTrue { notificationsModelMock.calledReminderEnabled }
        assertTrue { notificationsApiMock.initializeCalled }
        assertTrue { notificationsModelMock.calledRecreateReminder }
    }

    @Test
    fun testReminderDisabledNotificationsDisabled() = runTest {
        notificationsModelMock.notificationsEnabled = false
        settingsModel.setRemindersSettingEnabled(false)
        assertTrue { notificationsModelMock.calledReminderDisabled }
        assertFalse { notificationsApiMock.initializeCalled }
        assertTrue { notificationsModelMock.calledRecreateReminder }
    }

    @Test
    fun testFeedbackEnabledNotificationsDisabled() = runTest {
        notificationsModelMock.notificationsEnabled = false
        settingsModel.setFeedbackSettingEnabled(true)
        assertTrue { notificationsModelMock.calledFeedbackEnabled }
        assertTrue { notificationsApiMock.initializeCalled }
        assertTrue { notificationsModelMock.calledRecreateFeedback }
    }

    @Test
    fun testFeedbackDisabledNotificationsDisabled() = runTest {
        notificationsModelMock.notificationsEnabled = false
        settingsModel.setFeedbackSettingEnabled(false)
        assertTrue { notificationsModelMock.calledFeedbackDisabled }
        assertFalse { notificationsApiMock.initializeCalled }
        assertTrue { notificationsModelMock.calledRecreateFeedback }
    }
}*/

