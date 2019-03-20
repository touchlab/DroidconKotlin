package co.touchlab.sessionize

import co.touchlab.sessionize.api.AnalyticsApi
import co.touchlab.sessionize.api.SessionizeApi
import co.touchlab.stately.concurrency.value
import co.touchlab.stately.freeze
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay

import kotlin.test.Test
import kotlin.test.assertTrue

class EventModelTest {
    @Test
    fun testsRunTest() = runTest{
        testFunction()
    }

    @Test
    fun testRsvpAndAnalytics() = runTest {
        ServiceRegistry.dbDriver = testDbConnection()
        ServiceRegistry.appSettings = TestSettings()
        ServiceRegistry.coroutinesDispatcher = Dispatchers.Main
        ServiceRegistry.concurrent = co.touchlab.sessionize.platform.TestConcurrent
        initPlatformClientTest({filePrefix, fileType ->
            when(filePrefix){
                "sponsors" -> SPONSORS
                "speakers" -> SPEAKERS
                "schedule" -> SCHEDULE
                else -> SCHEDULE
            }
        }, {s: String -> Unit})

        val analyticsApiMock = AnalyticsApiMock()
        val sessionizeApiMock = SessionizeApiMock()

        val eventModel = EventModel("67316", analyticsApiMock, sessionizeApiMock)
        eventModel.toggleRsvpSuspend(true)
        assertTrue { sessionizeApiMock.rsvpCalled }
        assertTrue { analyticsApiMock.logCalled }
    }

    fun initPlatformClientTest(
            staticFileLoader: (filePrefix: String, fileType: String) -> String?,
            clLogCallback: (s: String) -> Unit) {

        AppContext.appSettings.value = ServiceRegistry.appSettings.freeze()
        AppContext.dbHelper.initDatabase(ServiceRegistry.dbDriver)

        AppContext.lambdas.value = AppContext.PlatformLambdas(
                staticFileLoader,
                clLogCallback).freeze()

        AppContext.seedFileLoad()

        AppContext.dispatcherLocal.value = ServiceRegistry.coroutinesDispatcher
        AppContext.coroutineScope.value = AppContextCoroutineScope(ServiceRegistry.coroutinesDispatcher)
        ServiceRegistry.sessionizeApi = SessionizeApiMock()
        ServiceRegistry.analyticsApi = AnalyticsApiMock()
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

    override suspend fun recordRsvp(methodName: String, sessionId: String, userUuid: String): Boolean {
        rsvpCalled = true
        return true
    }
}

suspend fun testFunction() {
    delay(500)
    println("Testing stuff")
}