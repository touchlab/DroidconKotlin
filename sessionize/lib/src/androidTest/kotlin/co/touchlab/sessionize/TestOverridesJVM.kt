package co.touchlab.sessionize

import android.content.Context
import android.util.Log
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.touchlab.sessionize.platform.AndroidAppContext
import org.junit.runner.RunWith
import kotlin.test.BeforeTest

@RunWith(AndroidJUnit4::class)
class StaticFileLoaderTestJVM : StaticFileLoaderTest() {
    @BeforeTest
    fun androidSetup() {

        ServiceRegistry.initLambdas({ name, type ->
            loadAsset("$name.$type")
        }, { s: String -> Unit }, {e:Throwable, message:String ->
            Log.e("StaticFileLoaderTest", message, e)
        })

        setUp()

        AndroidAppContext.app = ApplicationProvider.getApplicationContext()
    }
}

@RunWith(AndroidJUnit4::class)
class EventModelTestJVM : EventModelTest()

private fun loadAsset(name: String) = AndroidAppContext.app.assets
        .open(name, Context.MODE_PRIVATE)
        .bufferedReader()
        .use { it.readText() }

@RunWith(AndroidJUnit4::class)
class SettingsModelTestJVM : SettingsModelTest()