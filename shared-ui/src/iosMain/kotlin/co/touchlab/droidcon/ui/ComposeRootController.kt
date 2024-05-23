package co.touchlab.droidcon.ui

import androidx.compose.ui.window.ComposeUIViewController
import co.touchlab.droidcon.viewmodel.ApplicationViewModel

fun getRootController(
    viewModel: ApplicationViewModel,
) =
    ComposeUIViewController {
        MainComposeView(viewModel)
    }
