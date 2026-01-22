package co.touchlab.droidcon.web

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import co.touchlab.droidcon.ui.MainView
import co.touchlab.droidcon.viewmodel.WaitForLoadedContextModel
import org.koin.compose.koinInject

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val koinApplication = startKoin()

    ComposeViewport {
        val viewModel:WaitForLoadedContextModel = koinInject() // Works
        MainView(viewModel)

    }
}
