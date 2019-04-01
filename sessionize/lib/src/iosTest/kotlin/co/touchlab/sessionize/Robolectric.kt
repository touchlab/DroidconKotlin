package co.touchlab.sessionize

import kotlin.reflect.KClass

actual annotation class RunWith(actual val value: KClass<out Runner>)
actual abstract class Runner
actual class AndroidJUnit4 : Runner()
actual val localStaticFileLoader : ((name:String, type:String) -> String?)? = null
actual fun prepareApp() {}