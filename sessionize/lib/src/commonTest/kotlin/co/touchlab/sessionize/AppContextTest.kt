package co.touchlab.sessionize

import kotlinx.serialization.json.Json
import kotlin.native.concurrent.ThreadLocal
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@ThreadLocal
var staticFileLoader: (filePrefix: String, fileType: String) -> String? = { _, _ ->   """
    [{"groupName":"test", "sponsors":"test"}]
""".trimIndent()}

fun kickOffTest() {
    main(emptyArray<String>())
}

class AppContextTests {


    @BeforeTest
    fun setUp() {
    }

    @AfterTest
    fun tearDown() {
    }

    @Test
    fun initPlatformClient() {
        assertNotNull(staticFileLoader, "staticFileLoader not initialized")
        val sponsors = staticFileLoader.invoke("sponsors", "json")
        sponsors?.let {
            val sponsorsJson = Json.nonstrict.parseJson(it).jsonArray
            assertNotEquals(sponsorsJson.size, 0, "empty sponsors or none found")
            //assertEquals(sponsorsJson.size, 0, "empty sponsors or none found")
            assertTrue(sponsorsJson[0].jsonObject.containsKey("groupName"))
            assertTrue(sponsorsJson[0].jsonObject.containsKey("sponsors"))
        }
    }

}

