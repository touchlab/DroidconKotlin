package co.touchlab.sessionize

import co.touchlab.droidcon.db.MyPastSession
import co.touchlab.droidcon.db.MySessions
import co.touchlab.sessionize.api.FeedbackApi
import co.touchlab.sessionize.api.NotificationsApi
import co.touchlab.sessionize.api.notificationFeedbackId
import co.touchlab.sessionize.api.notificationReminderId
import co.touchlab.sessionize.platform.NotificationsModel.feedbackEnabled
import co.touchlab.sessionize.platform.NotificationsModel.notificationsEnabled
import co.touchlab.sessionize.platform.NotificationsModel.reminderNotificationsEnabled
import co.touchlab.sessionize.platform.NotificationsModel.setFeedbackEnabled
import co.touchlab.sessionize.platform.NotificationsModel.setNotificationsEnabled
import co.touchlab.sessionize.platform.NotificationsModel.setRemindersEnabled

import co.touchlab.sessionize.platform.TestConcurrent
import kotlinx.coroutines.Dispatchers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue


abstract class NotificationTest {
    private val sessionizeApiMock = SessionizeApiMock()
    private val analyticsApiMock = AnalyticsApiMock()
    private val notificationsApiMock = NotificationsApiMock()
    private val feedbackApiMock = FeedbackApiMock()

    lateinit var settingsModel:SettingsModel

    private val sessionIdInt = 67316

    @BeforeTest
    fun setup() {
        ServiceRegistry.initServiceRegistry(testDbConnection(),
                Dispatchers.Main, TestSettings(), TestConcurrent, sessionizeApiMock, analyticsApiMock, notificationsApiMock, "-0400")

        ServiceRegistry.initLambdas({filePrefix, fileType ->
            when(filePrefix){
                "sponsors" -> SPONSORS
                "speakers" -> SPEAKERS
                "schedule" -> SCHEDULE
                else -> SCHEDULE
            }
        }, {s: String -> Unit},{e:Throwable, message:String ->
        })

//        AppContext.initAppContext()

        settingsModel = SettingsModel()
    }

    @Test
    fun testRsvp() = runTest {

    }
    @Test
    fun testNotificationInitSuccess() = runTest {
        notificationsApiMock.shouldInitialize = true
        notificationsApiMock.initializeNotifications { success ->
            assertTrue(success, "Notifications Should always be enabled in Android")
        }
    }

    @Test
    fun testNotificationInitFailure() = runTest {
        notificationsApiMock.shouldInitialize = false
        notificationsApiMock.initializeNotifications { success ->
            assertTrue(!success, "Notifications Should always be enabled in Android")
        }
    }

    @Test
    fun testFeedbackEnabled() = runTest {
        settingsModel.setFeedbackSettingEnabled(true)
        notificationsApiMock.initializeNotifications { success ->
        }

        var sessions = listOf<MySessions>()
        createFeedbackNotificationsForSessions(sessions)
        assertTrue { notificationsApiMock.notificationCalled }
        assertTrue { notificationsApiMock.feedbackNotification == notificationFeedbackId }
    }

    @Test
    fun testFeedbackDisabled() = runTest {
        settingsModel.setFeedbackSettingEnabled(false)
        notificationsApiMock.initializeNotifications { success ->
        }

        var sessions = listOf<MySessions>()
        createFeedbackNotificationsForSessions(sessions)
        assertTrue { !notificationsApiMock.notificationCalled }
    }

    @Test
    fun testRemindersEnabled() = runTest {
        settingsModel.setRemindersSettingEnabled(true)
        notificationsApiMock.initializeNotifications { success ->
        }

        var sessions = listOf<MySessions>()
        createReminderNotificationsForSessions(sessions)
        assertTrue { notificationsApiMock.notificationCalled }
        assertTrue { notificationsApiMock.reminderNotification == notificationReminderId }
    }

    @Test
    fun testRemindersDisabled() = runTest {
        settingsModel.setRemindersSettingEnabled(false)
        notificationsApiMock.initializeNotifications { success ->
        }

        var sessions = listOf<MySessions>()
        createReminderNotificationsForSessions(sessions)
        assertTrue { !notificationsApiMock.notificationCalled }
    }


    @Test
    fun testRemindersEnabledNotificationsDisabled() = runTest {
        setNotificationsEnabled(false)
        settingsModel.setRemindersSettingEnabled(true)

        notificationsApiMock.initializeNotifications { success ->
        }

        var sessions = listOf<MySessions>()
        createReminderNotificationsForSessions(sessions)
        assertTrue { !notificationsApiMock.notificationCalled }
    }

    @Test
    fun testFeedbackEnabledNotificationsDisabled() = runTest {
        setNotificationsEnabled(false)
        settingsModel.setFeedbackSettingEnabled(true)


        notificationsApiMock.initializeNotifications { success ->
        }

        var sessions = listOf<MySessions>()
        createReminderNotificationsForSessions(sessions)
        assertTrue { !notificationsApiMock.notificationCalled }
    }


    @Test
    fun testDisablingReminders() = runTest {
        setNotificationsEnabled(true)
        setRemindersEnabled(true)
        notificationsApiMock.initializeNotifications { success ->
        }

        var sessions = listOf<MySessions>()
        createReminderNotificationsForSessions(sessions)

        runTest {
            settingsModel.setRemindersSettingEnabled(false)
        }

        assertTrue { notificationsApiMock.notificationCalled }
        assertTrue { notificationsApiMock.reminderNotification == -1 }
    }

    @Test
    fun testDisablingFeedback() = runTest {
        setNotificationsEnabled(true)
        settingsModel.setFeedbackSettingEnabled(true)
        notificationsApiMock.initializeNotifications { success ->
        }
        var sessions = listOf<MySessions>()
        createFeedbackNotificationsForSessions(sessions)
        runTest {
            settingsModel.setFeedbackSettingEnabled(false)
        }
        assertTrue { notificationsApiMock.notificationCalled }
        //assertTrue { notificationsApiMock.feedbackNotifications.isEmpty() }
    }

    @Test
    fun testDisablingRemindersWithFeedback() = runTest {
        setNotificationsEnabled(true)
        setRemindersEnabled(true)
        setFeedbackEnabled(true)

        notificationsApiMock.initializeNotifications { success ->
        }
        var sessions = listOf<MySessions>()
        createReminderNotificationsForSessions(sessions)
        createFeedbackNotificationsForSessions(sessions)

        runTest {
            settingsModel.setRemindersSettingEnabled(false)
        }
        assertTrue { notificationsApiMock.notificationCalled }
        assertTrue { notificationsApiMock.reminderNotification == -1}
        assertTrue { notificationsApiMock.feedbackNotification != -1 }
    }

    @Test
    fun testDisablingFeedbackWithReminders() = runTest {
        setNotificationsEnabled(true)
        setFeedbackEnabled(true)
        setRemindersEnabled(true)

        notificationsApiMock.initializeNotifications { success ->
        }
        var sessions = listOf<MySessions>()
        createFeedbackNotificationsForSessions(sessions)
        createReminderNotificationsForSessions(sessions)
        runTest {
            settingsModel.setFeedbackSettingEnabled(false)
        }
        assertTrue { notificationsApiMock.notificationCalled }
        assertTrue { notificationsApiMock.feedbackNotification == -1 }
        assertTrue { notificationsApiMock.reminderNotification != -1 }
    }

    private fun createReminderNotificationsForSessions(sessions:List<MySessions>){
        if(reminderNotificationsEnabled() && notificationsEnabled()) {
            notificationsApiMock.createLocalNotification("Room Name", "Title", 0, notificationReminderId)
        }
    }

    private fun createFeedbackNotificationsForSessions(sessions:List<MySessions>){
        if (feedbackEnabled() && notificationsEnabled()) {
            notificationsApiMock.createLocalNotification("RoomName", "Title", 0, notificationFeedbackId)
        }
    }

    fun cancelReminderNotificationsForSessions(sessions:List<MySessions>){
        if(!reminderNotificationsEnabled() || !notificationsEnabled()) {
            notificationsApiMock.cancelLocalNotification(notificationReminderId)
        }
    }

    fun cancelFeedbackNotificationsForSessions(sessions:List<MySessions>){
        if(!feedbackEnabled() || !notificationsEnabled()) {
            notificationsApiMock.cancelLocalNotification(notificationFeedbackId)
        }
    }
}






class FeedbackApiMock : FeedbackApi {

    var generatingFeedbackDialog:Boolean = false
    var feedbackError : FeedbackApi.FeedBackError? = null


    private var feedbackModel:FeedbackModel = FeedbackModel()

    fun getFeedbackModel(): FeedbackModel {
        return feedbackModel
    }
    override fun generateFeedbackDialog(session: MyPastSession){
        generatingFeedbackDialog = true
        feedbackModel.finishedFeedback("1234",1,"This is a comment")
    }

    override fun onError(error: FeedbackApi.FeedBackError){
        feedbackError = error
    }
}
class NotificationsApiMock : NotificationsApi {

    var shouldInitialize = true
    var notificationCalled = false
    var notificationCancelled = false
    var reminderNotification:Int = -1
    var feedbackNotification:Int = -1

    override fun createLocalNotification(title:String, message:String, timeInMS:Long, notificationId: Int){
        notificationCalled = true
        if(notificationId == notificationFeedbackId){
            feedbackNotification = notificationId
        }else{
            reminderNotification = notificationId
        }
    }

    override fun cancelLocalNotification(notificationId: Int){
        notificationCancelled = true
        if(notificationId == notificationFeedbackId){
            feedbackNotification = -1
        }else{
            reminderNotification = -1
        }
    }

    override fun initializeNotifications(onSuccess: (Boolean) -> Unit){
        onSuccess(shouldInitialize)
    }

    override fun deinitializeNotifications(){
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}