@file:Suppress("KDocMissingDocumentation", "unused")
package co.touchlab.sessionize



import kotlin.reflect.KClass

/*
These expect declarations allow our unit tests to run using Robolectric on the jvm. The native implementations do nothing.
 */

/*expect annotation class Config(val sdk: Int, val minSdk: Int, val maxSdk: Int, val manifest: String,
                               val application: KClass<out Application>,
                               val q: String, val p: String, val r: String, val a: String,
                               val shadows: KClass<Any>, val instrumentedPackages: Array<String>
)*/
expect annotation class RunWith(val value: KClass<out Runner>)
expect abstract class Runner
expect class AndroidJUnit4 : Runner
expect val localStaticFileLoader : ((name:String, type:String) -> String?)?
expect fun prepareApp()