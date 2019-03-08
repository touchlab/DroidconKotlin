package co.touchlab.sessionize

import co.touchlab.sessionize.api.AnalyticsApi
import co.touchlab.sessionize.api.SessionizeApi
import kotlin.test.Test
import kotlin.test.assertTrue

class EventModelTest {
    @Test
    fun testsRunTest() {
        assertTrue { true }
    }

    @Test
    fun testRsvpAndAnalytics() {
        val eventModel = EventModel("1", AnalyticsApiMock(), SessionizeApiMock())
        //eventModel.toggleRsvp(true)
    }
}

class AnalyticsApiMock : AnalyticsApi {
    override fun logEvent(name: String, params: Map<String, Any>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

class SessionizeApiMock : SessionizeApi {
    override suspend fun getSpeakersJson(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getSessionsJson(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getSponsorJson(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun recordRsvp(methodName: String, sessionId: String, userUuid: String): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}