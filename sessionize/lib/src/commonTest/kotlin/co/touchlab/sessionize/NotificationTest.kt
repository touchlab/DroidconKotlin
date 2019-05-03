package co.touchlab.sessionize

import co.touchlab.droidcon.db.MyPastSession
import co.touchlab.droidcon.db.MySessions
import co.touchlab.sessionize.api.FeedbackApi
import co.touchlab.sessionize.api.NotificationsApi
import co.touchlab.sessionize.api.notificationFeedbackTag
import co.touchlab.sessionize.api.notificationReminderTag

import co.touchlab.sessionize.platform.TestConcurrent
import co.touchlab.sessionize.platform.feedbackEnabled
import co.touchlab.sessionize.platform.notificationsEnabled
import co.touchlab.sessionize.platform.reminderNotificationsEnabled
import co.touchlab.sessionize.platform.setNotificationsEnabled
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

    val sessionId = "67316"
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
        }, {s: String -> Unit})

        AppContext.initAppContext()

        AppContext.seedFileLoad()

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
        assertTrue { notificationsApiMock.feedbackNotifications.contains(sessionIdInt) }
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
        assertTrue { notificationsApiMock.reminderNotifications.contains(sessionIdInt) }
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
        settingsModel.setRemindersSettingEnabled(true)
        notificationsApiMock.initializeNotifications { success ->
        }
        var sessions = listOf<MySessions>()
        createReminderNotificationsForSessions(sessions)
        settingsModel.setRemindersSettingEnabled(false)

        // TODO Add a wait timer so feedback notifications are correct
        assertTrue { notificationsApiMock.notificationCalled }
        assertTrue { notificationsApiMock.reminderNotifications.isEmpty() }
    }

    @Test
    fun testDisablingFeedback() = runTest {
        setNotificationsEnabled(true)
        settingsModel.setFeedbackSettingEnabled(true)
        notificationsApiMock.initializeNotifications { success ->
        }
        var sessions = listOf<MySessions>()
        createFeedbackNotificationsForSessions(sessions)
        settingsModel.setFeedbackSettingEnabled(false)

        // TODO Add a wait timer so feedback notifications are correct
        assertTrue { notificationsApiMock.notificationCalled }
        //assertTrue { notificationsApiMock.feedbackNotifications.isEmpty() }
    }

    @Test
    fun testDisablingRemindersWithFeedback() = runTest {
        setNotificationsEnabled(true)
        settingsModel.setRemindersSettingEnabled(true)
        settingsModel.setFeedbackSettingEnabled(true)

        notificationsApiMock.initializeNotifications { success ->
        }
        var sessions = listOf<MySessions>()
        createReminderNotificationsForSessions(sessions)
        createFeedbackNotificationsForSessions(sessions)

        settingsModel.setRemindersSettingEnabled(false)

        assertTrue { notificationsApiMock.notificationCalled }
        assertTrue { notificationsApiMock.reminderNotifications.isEmpty() }
        assertTrue { !notificationsApiMock.feedbackNotifications.isEmpty() }
    }

    @Test
    fun testDisablingFeedbackWithReminders() = runTest {
        setNotificationsEnabled(true)
        settingsModel.setFeedbackSettingEnabled(true)
        settingsModel.setRemindersSettingEnabled(true)

        notificationsApiMock.initializeNotifications { success ->
        }
        var sessions = listOf<MySessions>()
        createFeedbackNotificationsForSessions(sessions)
        createReminderNotificationsForSessions(sessions)

        settingsModel.setFeedbackSettingEnabled(false)

        // TODO Add a wait timer so feedback notifications are correct
        assertTrue { notificationsApiMock.notificationCalled }
        assertTrue { notificationsApiMock.feedbackNotifications.isEmpty() }
        assertTrue { !notificationsApiMock.reminderNotifications.isEmpty() }
    }

    fun createReminderNotificationsForSessions(sessions:List<MySessions>){
        if(reminderNotificationsEnabled() && notificationsEnabled()) {
            notificationsApiMock.createLocalNotification("Room Name", "Title", 0, 67316, notificationReminderTag)
        }
    }

    fun createFeedbackNotificationsForSessions(sessions:List<MySessions>){
        if (feedbackEnabled() && notificationsEnabled()) {
            notificationsApiMock.createLocalNotification("RoomName", "Title", 0, 67316, notificationFeedbackTag)
        }
    }

    fun cancelReminderNotificationsForSessions(sessions:List<MySessions>){
        if(!reminderNotificationsEnabled() || !notificationsEnabled()) {
            notificationsApiMock.cancelLocalNotification(67316, notificationReminderTag)
        }
    }

    fun cancelFeedbackNotificationsForSessions(sessions:List<MySessions>){
        if(!feedbackEnabled() || !notificationsEnabled()) {
            notificationsApiMock.cancelLocalNotification(67316, notificationFeedbackTag)
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
    var reminderNotifications:MutableList<Int> = mutableListOf()
    var feedbackNotifications:MutableList<Int> = mutableListOf()

    override fun createLocalNotification(title:String, message:String, timeInMS:Long, notificationId: Int, notificationTag: String){
        notificationCalled = true
        if(notificationTag == notificationFeedbackTag){
            if(!feedbackNotifications.contains(notificationId)){
                feedbackNotifications.add(notificationId)
            }
        }else{
            if(!reminderNotifications.contains(notificationId)){
                reminderNotifications.add(notificationId)
            }
        }
    }

    override fun cancelLocalNotification(notificationId: Int, notificationTag: String){
        notificationCancelled = true
        if(notificationTag == notificationFeedbackTag){
            if(feedbackNotifications.contains(notificationId)){
                feedbackNotifications.remove(notificationId)
            }
        }else{
            if(reminderNotifications.contains(notificationId)){
                reminderNotifications.remove(notificationId)
            }
        }
    }

    override fun initializeNotifications(onSuccess: (Boolean) -> Unit){
        onSuccess(shouldInitialize)
    }

    override fun deinitializeNotifications(){
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}