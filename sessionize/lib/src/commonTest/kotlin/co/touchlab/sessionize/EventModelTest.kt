package co.touchlab.sessionize

import co.touchlab.sessionize.api.AnalyticsApi
import co.touchlab.sessionize.api.SessionizeApi
import co.touchlab.sessionize.api.NotificationsApi
import co.touchlab.sessionize.platform.TestConcurrent
import kotlinx.coroutines.Dispatchers
import kotlin.test.BeforeTest

import kotlin.test.Test
import kotlin.test.assertTrue

class EventModelTest {
    private val sessionizeApiMock = SessionizeApiMock()
    private val analyticsApiMock = AnalyticsApiMock()
    private val notificationsApiMock = NotificationsApiMock()

    @BeforeTest
    fun setup() {
        ServiceRegistry.initServiceRegistry(testDbConnection(),
                Dispatchers.Main, TestSettings(), TestConcurrent, sessionizeApiMock, analyticsApiMock, notificationsApiMock, "-0400")

        AppContext.initAppContext({filePrefix, fileType ->
            when(filePrefix){
                "sponsors" -> SPONSORS
                "speakers" -> SPEAKERS
                "schedule" -> SCHEDULE
                else -> SCHEDULE
            }
        }, {s: String -> Unit})

        AppContext.seedFileLoad()
    }

    @Test
    fun testRsvpAndAnalytics() = runTest {
        val eventModel = EventModel("67316")
        val session = AppContext.sessionQueries.sessionById("67316").executeAsOne()
        val si = collectSessionInfo(session)
        eventModel.toggleRsvpSuspend(si)
        assertTrue { sessionizeApiMock.rsvpCalled }
        assertTrue { analyticsApiMock.logCalled }
        assertTrue { notificationsApiMock.notificationCalled }
    }
}

class AnalyticsApiMock : AnalyticsApi {
    var logCalled = false

    override fun logEvent(name: String, params: Map<String, Any>) {
        logCalled = true
    }

}

class SessionizeApiMock : SessionizeApi {
    var rsvpCalled = false
    override suspend fun getSpeakersJson(): String {
        return ""
    }

    override suspend fun getSessionsJson(): String {
        return ""
    }

    override suspend fun getSponsorJson(): String {
        return ""
    }

    override suspend fun recordRsvp(methodName: String, sessionId: String): Boolean {
        rsvpCalled = true
        return true
    }
}

class NotificationsApiMock : NotificationsApi {

    var notificationCalled = false
    override fun createLocalNotification(title:String, message:String, timeInMS:Long, notificationId: Int){
        notificationCalled = true;
    }

    override fun cancelLocalNotification(notificationId: Int){
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initializeNotifications(){
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deinitializeNotifications(){
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}