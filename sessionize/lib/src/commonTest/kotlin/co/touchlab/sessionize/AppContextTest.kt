package co.touchlab.sessionize

import co.touchlab.sessionize.AppContext.loadAbout
import co.touchlab.sessionize.AppContext.loadSchedule
import co.touchlab.sessionize.AppContext.loadSpeakers
import co.touchlab.sessionize.AppContext.loadSponsors
import co.touchlab.sessionize.platform.TestConcurrent
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

abstract class AppContextTests {

    fun setUp() {
        ServiceRegistry.initServiceRegistry(testDbConnection(),
                Dispatchers.Main, TestSettings(), TestConcurrent, SessionizeApiMock(), AnalyticsApiMock(), "-0400")
    }

    @AfterTest
    fun tearDown() {
    }

    @Test
    fun testSponsors() {
        val sponsors = loadSponsors()
        sponsors?.let {
            val sponsorsJson = Json.nonstrict.parseJson(it).jsonArray
            assertNotEquals(sponsorsJson.size, 0, "empty sponsors.json or none found")
            assertTrue(sponsorsJson[0].jsonObject.containsKey("groupName"))
            assertTrue(sponsorsJson[0].jsonObject.containsKey("sponsors"))
        }
    }

    @Test
    fun testAbout() {
        val about = loadAbout()
        about?.let {

            val aboutJson = Json.nonstrict.parseJson(it).jsonArray
            assertNotEquals(aboutJson.size, 0, "empty about.json or none found")
            assertTrue(aboutJson[0].jsonObject.containsKey("icon"))
            assertTrue(aboutJson[0].jsonObject.containsKey("title"))
            assertTrue(aboutJson[0].jsonObject.containsKey("detail"))

        }
    }

    @Test
    fun testSchedule() {
        val schedule = loadSchedule()
        schedule?.let {
            val scheduleJson = Json.nonstrict.parseJson(it).jsonArray
        assertNotEquals(scheduleJson.size, 0, "empty schedule.json or none found")
        assertTrue(scheduleJson[0].jsonObject.containsKey("date"))
        assertTrue(scheduleJson[0].jsonObject.containsKey("rooms"))
        }
    }

    @Test
    fun testSpeakers() {
        val speakers = loadSpeakers()
        speakers?.let {
            val speakersJson = Json.nonstrict.parseJson(it).jsonArray
            assertNotEquals(speakersJson.size, 0, "empty speakers.json or none found")
            assertTrue(speakersJson[0].jsonObject.containsKey("id"))
            assertTrue(speakersJson[0].jsonObject.containsKey("firstName"))
            assertTrue(speakersJson[0].jsonObject.containsKey("lastName"))
        }
    }

}

