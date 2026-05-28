package co.touchlab.droidcon.web

import co.touchlab.droidcon.platformModule
import com.russhwolf.settings.ExperimentalSettingsApi
import kotlin.test.Test
import org.koin.dsl.koinApplication
import org.koin.test.KoinTest
import org.koin.test.check.checkModules

class WebKoinTest : KoinTest {

    @OptIn(ExperimentalSettingsApi::class)
    @Test
    fun `should inject web app components`() {
        koinApplication {
            modules(webAppModule(), platformModule)
            checkModules()
        }
    }
}
