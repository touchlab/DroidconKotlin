package co.touchlab.sessionize

import co.touchlab.sessionize.api.AnalyticsApi
import co.touchlab.sessionize.api.SessionizeApi
import co.touchlab.sessionize.db.DateAdapter
import co.touchlab.sessionize.db.SessionizeDbHelper
import co.touchlab.sessionize.mocks.FeedbackApiMock
import co.touchlab.sessionize.mocks.NotificationsApiMock
import kotlinx.coroutines.Dispatchers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

abstract class EventModelTest {
    private val sessionizeApiMock = SessionizeApiMock()
    private val analyticsApiMock = AnalyticsApiMock()
    private val notificationsApiMock = NotificationsApiMock()
    //private val feedbackApiMock = FeedbackApiMock()

    private val timeZone = "-0800"

    @BeforeTest
    fun setup() {
        ServiceRegistry.initServiceRegistry(testDbConnection(),
                TestSettings(), sessionizeApiMock, analyticsApiMock, notificationsApiMock, timeZone)

        ServiceRegistry.initLambdas({ filePrefix, fileType ->
            when (filePrefix) {
                "sponsors" -> SPONSORS
                "speakers" -> SPEAKERS
                "schedule" -> SCHEDULE
                else -> SCHEDULE
            }
        }, { s: String -> Unit }, {e:Throwable, message:String -> println(message)})

        AppContext.initAppContext()

    }

    @Test
    fun testRsvpAndAnalytics() = runTest {
        val eventModel = EventModel("67316")
        val sessions = SessionizeDbHelper.sessionQueries.allSessions().executeAsList()
        if(sessions.isNotEmpty()) {
            val session = sessions.first()
            val si = collectSessionInfo(session)
            eventModel.toggleRsvpSuspend(si)
            assertTrue { sessionizeApiMock.rsvpCalled }
            assertTrue { analyticsApiMock.logCalled }
            assertTrue { notificationsApiMock.reminderCalled.value }
        }
    }

    /*@Test
    fun testFeedbackModel() = runTest {
        val fbModel = feedbackApiMock.getFeedbackModel()
        fbModel.showFeedbackForPastSessions(feedbackApiMock)

        assertTrue { feedbackApiMock.feedbackError != null }
    }*/

    @Test
    fun testPSTTimeZoneCorrect(){
        val timeStr = "2019-04-12T08:00:00"
        val correctMillis = 1555084800000

        val timeStrWithZone = timeStr + timeZone

        val dateAdapter = DateAdapter()
        val timeDate = dateAdapter.decode(timeStrWithZone)
        val newTimeStr = dateAdapter.encode(timeDate)

        assertTrue { newTimeStr == timeStr }
        //assertTrue { timeDate.toLongMillis() == correctMillis }
    }
}

class AnalyticsApiMock : AnalyticsApi {
    var logCalled = false

    override fun logEvent(name: String, params: Map<String, Any>) {
        logCalled = true
    }
}

class SessionizeApiMock : SessionizeApi {
    override suspend fun sendFeedback(sessionId: String, rating: Int, comment: String?): Boolean {
        return true
    }

    var rsvpCalled = false
    override suspend fun getSpeakersJson(): String {
        return ""
    }

    override suspend fun getSessionsJson(): String {
        return ""
    }

    override suspend fun getSponsorSessionJson(): String {
        return ""
    }

    override suspend fun recordRsvp(methodName: String, sessionId: String): Boolean {
        rsvpCalled = true
        return true
    }
}