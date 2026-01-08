package co.touchlab.droidcon.web

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import co.touchlab.droidcon.ui.MainView

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val koinApplication = startKoin()
    val viewModel = koinApplication.waitForLoadedContextModel
    ComposeViewport {
        MainView(viewModel)
    }
}
