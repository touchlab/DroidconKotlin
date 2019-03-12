package co.touchlab.sessionize.platform

import co.touchlab.stately.annotation.Throws
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.parseList
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue


@UseExperimental(ImplicitReflectionSerializer::class)
@Throws
fun testSponsorSeedFile() {
    val sponsors = loadAssetFromDefault("sponsors", "json")
    sponsors?.let {
        val sponsorsJson = JsonArray(Json.nonstrict.parseList(sponsors))
        try {
            assertNotEquals(sponsorsJson.size, 0, "empty sponsors or none found")
            assertTrue(sponsorsJson[0].jsonObject.containsKey("groupName"))
            assertTrue(sponsorsJson[0].jsonObject.containsKey("sponsors"))
        } catch (e: AssertionError) { // Runtime exceptions and Errors don't propagate to ios
            throw Exception(e.message, e)
        }
    }
}
