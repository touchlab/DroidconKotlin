package co.touchlab.sessionize

import kotlinx.serialization.json.Json
import kotlin.native.concurrent.ThreadLocal
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.native.internal.test.testLauncherEntryPoint

@ThreadLocal
var staticFileLoader: (filePrefix: String, fileType: String) -> String? = { _, _ ->   """
    [{"groupName":"test", "sponsors":"test"}]
""".trimIndent()}

fun kickOffTest():Int {
    return testLauncherEntryPoint(emptyArray<String>())
}

class AppContextTests {


    @BeforeTest
    fun setUp() {
        assertNotNull(staticFileLoader, "staticFileLoader not initialized")
    }

    @AfterTest
    fun tearDown() {
    }

    @Test
    fun testSponsors() {
        val sponsors = staticFileLoader.invoke("sponsors", "json")
        sponsors?.let {
            val sponsorsJson = Json.nonstrict.parseJson(it).jsonArray
            assertNotEquals(sponsorsJson.size, 0, "empty sponsors.json or none found")
            assertTrue(sponsorsJson[0].jsonObject.containsKey("groupName"))
            assertTrue(sponsorsJson[0].jsonObject.containsKey("sponsors"))
        }
    }

    @Test
    fun testAbout() {
        val about = staticFileLoader.invoke("about", "json")
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
        val schedule = staticFileLoader.invoke("schedule", "json")
        schedule?.let {
            val scheduleJson = Json.nonstrict.parseJson(it).jsonArray
            assertNotEquals(scheduleJson.size, 0, "empty schedule.json or none found")
            assertTrue(scheduleJson[0].jsonObject.containsKey("date"))
            assertTrue(scheduleJson[0].jsonObject.containsKey("rooms"))
        }
    }


    @Test
    fun testSpeakers() {
        val speakers = staticFileLoader.invoke("speakers", "json")
        speakers?.let {
            val speakersJson = Json.nonstrict.parseJson(it).jsonArray
            assertNotEquals(speakersJson.size, 0, "empty speakers.json or none found")
            assertTrue(speakersJson[0].jsonObject.containsKey("id"))
            assertTrue(speakersJson[0].jsonObject.containsKey("firstName"))
            assertTrue(speakersJson[0].jsonObject.containsKey("lastName"))
        }
    }

}

