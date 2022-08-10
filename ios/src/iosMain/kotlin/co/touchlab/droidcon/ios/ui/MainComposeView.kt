package co.touchlab.droidcon.ios.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Application
import co.touchlab.droidcon.ios.viewmodel.ApplicationViewModel

@Composable
internal fun MainComposeView(viewModel: ApplicationViewModel) {
    DroidconTheme {
        BottomNavigationView(viewModel = viewModel)
    }
}

fun getRootController(viewModel: ApplicationViewModel) = Application("MainComposeView") {
    MainComposeView(viewModel)
}
