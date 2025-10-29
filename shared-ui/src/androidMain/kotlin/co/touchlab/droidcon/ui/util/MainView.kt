package co.touchlab.droidcon.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import co.touchlab.droidcon.ui.MainComposeView
import co.touchlab.droidcon.viewmodel.WaitForLoadedContextModel

@Composable
fun MainView(waitForLoadedContextModel: WaitForLoadedContextModel) {
    MainComposeView(waitForLoadedContextModel = waitForLoadedContextModel, modifier = Modifier)
}
