package co.touchlab.sessionize

import co.touchlab.droidcon.db.MyPastSession
import co.touchlab.sessionize.api.AnalyticsApi
import co.touchlab.sessionize.api.FeedbackApi
import co.touchlab.sessionize.api.NotificationsApi
import co.touchlab.sessionize.api.SessionizeApi
import co.touchlab.sessionize.db.DateAdapter
import co.touchlab.sessionize.platform.TestConcurrent
import kotlinx.coroutines.Dispatchers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

abstract class EventModelTest {
    private val sessionizeApiMock = SessionizeApiMock()
    private val analyticsApiMock = AnalyticsApiMock()
    private val notificationsApiMock = NotificationsApiMock()
    private val feedbackApiMock = FeedbackApiMock()

    private val timeZone = "-0400"

    @BeforeTest
    fun setup() {
        ServiceRegistry.initServiceRegistry(testDbConnection(),
                Dispatchers.Main, TestSettings(), TestConcurrent, sessionizeApiMock, analyticsApiMock, notificationsApiMock, timeZone)

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

    @Test
    fun testFeedbackModel() = runTest {
        val fbModel = feedbackApiMock.getFeedbackModel()
        fbModel.showFeedbackForPastSessions(feedbackApiMock)

        assertTrue { feedbackApiMock.feedbackError != null }
    }

    @Test
    fun testEDTTimeZoneCorrect(){
        val timeStr = "2019-04-12T08:00:00"
        val correctMillis = 1555070400000

        val timeStrWithZone = timeStr + timeZone

        val dateAdapter = DateAdapter("GMT$timeZone")
        val timeDate = dateAdapter.decode(timeStrWithZone)
        val newTimeStr = dateAdapter.encode(timeDate)

        assertTrue { newTimeStr == timeStrWithZone }
        assertTrue { timeDate.toLongMillis() == correctMillis }
    }

    @Test
    fun testTimeZoneIncorrect(){
        // Using Japanese Time Zone because they don't use daylight savings time
        var timeZoneJST = "+0900"
        val timeStr = "2019-04-12T08:00:00"
        val correctMillis = 1555070400000

        val timeStrWithZone = timeStr + timeZoneJST

        val dateAdapter = DateAdapter("GMT$timeZoneJST")
        val timeDate = dateAdapter.decode(timeStrWithZone)
        val newTimeStr = dateAdapter.encode(timeDate)

        assertTrue { newTimeStr == timeStrWithZone }
        assertTrue { timeDate.toLongMillis() != correctMillis }
    }

    @Test
    fun testTimeZonePST(){
        var timeZonePST = "-0800"
        val timeStr = "2019-04-12T08:00:00"
        val correctMillis = 1555084800000

        val timeStrWithZone = timeStr + timeZonePST

        val dateAdapter = DateAdapter("GMT$timeZonePST")
        val timeDate = dateAdapter.decode(timeStrWithZone)
        val newTimeStr = dateAdapter.encode(timeDate)

        print(timeDate.toLongMillis())
        assertTrue { newTimeStr == timeStrWithZone }
        assertEquals(timeDate.toLongMillis(), correctMillis,  timeDate.toLongMillis().toString() + " does not equal $correctMillis")
    }

    @Test
    fun testTimeZoneConversion(){
        var timeZonePST = "-0800"
        val timeStr = "2019-04-12T08:00:00"
        val correctMillis = 1555084800000

        val timeStrWithZone = timeStr + timeZonePST

        val dateAdapter = DateAdapter("GMT$timeZonePST")
        val timeDate = dateAdapter.decode(timeStrWithZone)
        val newTimeStr = dateAdapter.encode(timeDate)


        val dateAdapter2 = DateAdapter("GMT$timeZone")
        val timeDate2 = dateAdapter.decode(newTimeStr)
        val newTimeStr2:String = dateAdapter2.encode(timeDate2)

        assertTrue { newTimeStr != newTimeStr2 }
        assertTrue { newTimeStr.contains(timeZonePST) }
        assertTrue { newTimeStr2.contains(timeZone) }
        assertEquals(timeDate.toLongMillis(), timeDate2.toLongMillis(),
                timeDate.toLongMillis().toString() + " does not equal to " + timeDate2.toLongMillis().toString())

        assertEquals(correctMillis, timeDate2.toLongMillis(),
                correctMillis.toString() + " does not equal to " + timeDate2.toLongMillis().toString())
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


    var notificationCalled = false

    override fun createLocalNotification(title: String, message: String, timeInMS: Long, notificationId: Int, notificationTag: String) {
        notificationCalled = true
    }

    override fun cancelLocalNotification(notificationId: Int, notificationTag: String) {
    }

    override fun initializeNotifications(){
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun deinitializeNotifications(){
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
