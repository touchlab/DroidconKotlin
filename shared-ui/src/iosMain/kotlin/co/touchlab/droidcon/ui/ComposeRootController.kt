package co.touchlab.droidcon.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitViewController
import androidx.compose.ui.window.ComposeUIViewController
import co.touchlab.droidcon.viewmodel.ApplicationViewModel
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIViewController

@OptIn(ExperimentalForeignApi::class)
fun getRootController(
    viewModel: ApplicationViewModel,
    createUIViewController: () -> UIViewController,
) =
    ComposeUIViewController {
        MainComposeView(viewModel, {
            UIKitViewController(
                factory = createUIViewController,
                modifier = Modifier,
            )
        })
    }