package co.touchlab.sessionize

import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import co.touchlab.sessionize.platform.testSponsorSeedFile
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
        assertEquals("co.touchlab.sessionize", appContext.packageName)
    }

    @Test
    fun sponsorSeedFile() {
        testSponsorSeedFile()
        /*val sponsorsJSONArray = JSONArray(loadAssetFromDefault("sponsors", "json"))
        assertFalse(sponsorsJSONArray.length() == 0)
        assertTrue(sponsorsJSONArray.getJSONObject(0).has("groupName"))
        assertTrue(sponsorsJSONArray.getJSONObject(0).has("sponsors"))*/
    }
}
