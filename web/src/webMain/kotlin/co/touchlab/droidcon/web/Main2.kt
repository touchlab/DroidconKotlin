package co.touchlab.droidcon.web

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import co.touchlab.droidcon.ui.MainView
import co.touchlab.droidcon.ui.util.TimezoneInit
import co.touchlab.droidcon.viewmodel.WaitForLoadedContextModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import org.brightify.hyperdrive.multiplatformx.LifecycleGraph
import org.koin.compose.koinInject

@OptIn(ExperimentalComposeUiApi::class)
suspend fun main() {
    val koinApplication = startKoin()

    val lifecycleScope = CoroutineScope(SupervisorJob()) + Dispatchers.Main

    @Suppress("UNUSED_VARIABLE")
    val tz = TimezoneInit

    // Now set up view model lifecycle
    val root = LifecycleGraph.Root("…")

    lifecycleScope.launch {
        val cancelAttach = root.attach(lifecycleScope)
        try {
            awaitCancellation()
        } finally {
            cancelAttach.cancel()
        }
    }

    ComposeViewport {
        val viewModel: WaitForLoadedContextModel = koinInject() // Works
        viewModel.lifecycle.removeFromParent()
        root.addChild(viewModel.lifecycle)

        MainView(viewModel)
    }
}
