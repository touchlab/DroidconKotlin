package co.touchlab.sessionize

import co.touchlab.droidcon.db.MySessions
import co.touchlab.sessionize.api.AnalyticsApi
import co.touchlab.sessionize.api.SessionizeApi
import co.touchlab.sessionize.api.FeedbackApi
import co.touchlab.sessionize.platform.TestConcurrent
import kotlinx.coroutines.Dispatchers
import kotlin.test.BeforeTest

import kotlin.test.Test
import kotlin.test.assertTrue

class EventModelTest {

    private val sessionizeApiMock = SessionizeApiMock()
    private val analyticsApiMock = AnalyticsApiMock()
    private val feedbackApiMock = FeedbackApiMock()

    @BeforeTest
    fun setup() {
        ServiceRegistry.initServiceRegistry(testDbConnection(),
                Dispatchers.Main, TestSettings(), TestConcurrent, SessionizeApiMock(), AnalyticsApiMock(), "-0400")

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
        /*eventModel.toggleRsvpSuspend(si)
        assertTrue { sessionizeApiMock.rsvpCalled }
        assertTrue { analyticsApiMock.logCalled }*/
    }

    @Test
    fun testFeedbackModel() = runTest {
        val fbModel = feedbackApiMock.getFeedbackModel()
        fbModel.showFeedbackForPastSessions(feedbackApiMock)

        assertTrue { feedbackApiMock.feedbackError != null }
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getSessionsJson(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getSponsorJson(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
    override fun generateFeedbackDialog(session: MySessions){
        generatingFeedbackDialog = true
        //feedbackModel.finishedFeedback("1234",1,"This is a comment")
    }

    override fun onError(error: FeedbackApi.FeedBackError){
        feedbackError = error
    }

}