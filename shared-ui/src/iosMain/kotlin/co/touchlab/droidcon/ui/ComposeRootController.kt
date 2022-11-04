package co.touchlab.droidcon.ui

import androidx.compose.ui.window.Application
import co.touchlab.droidcon.viewmodel.ApplicationViewModel

fun getRootController(viewModel: ApplicationViewModel) = Application("MainComposeView") {
    MainComposeView(viewModel)
}
