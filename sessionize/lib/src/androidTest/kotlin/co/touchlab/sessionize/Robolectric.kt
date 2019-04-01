package co.touchlab.sessionize

import android.content.Context
import co.touchlab.sessionize.platform.AndroidAppContext
import org.robolectric.RuntimeEnvironment

actual typealias RunWith = org.junit.runner.RunWith
actual typealias Runner = org.junit.runner.Runner
actual typealias AndroidJUnit4 = androidx.test.ext.junit.runners.AndroidJUnit4
actual fun prepareApp() {
    AndroidAppContext.app = RuntimeEnvironment.application
    //println(loadAsset("sponsors.json"))
}

actual val localStaticFileLoader : ((name:String, type:String) -> String?)?  = { name, type ->
    loadAsset("$name.$type")
}

private fun loadAsset(name:String) = AndroidAppContext.app.assets
        .open(name, Context.MODE_PRIVATE)
        .bufferedReader()
        .use { it.readText() }
