package co.touchlab.sessionize

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import co.touchlab.sessionize.platform.AndroidAppContext
import org.junit.runner.RunWith
import kotlin.test.BeforeTest

@RunWith(AndroidJUnit4::class)
class AppContextTestJVM : AppContextTests() {
    @BeforeTest
    fun androidSetup() {

        ServiceRegistry.initLambdas({ name, type ->
            loadAsset("$name.$type")
        }, { s: String -> Unit })

        setUp()

        AppContext.initAppContext()
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
class NotificationTestJVM : NotificationTest()