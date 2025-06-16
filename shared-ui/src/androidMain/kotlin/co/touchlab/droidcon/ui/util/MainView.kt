package co.touchlab.droidcon.ui.util

import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import co.touchlab.droidcon.ui.MainComposeView
import co.touchlab.droidcon.ui.theme.DroidconTheme
import co.touchlab.droidcon.viewmodel.WaitForLoadedContextModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun MainView(waitForLoadedContextModel: WaitForLoadedContextModel) {
    DroidconTheme {
        val systemUiController = rememberSystemUiController()
        systemUiController.setSystemBarsColor(color = MaterialTheme.colorScheme.surface)
        MainComposeView(waitForLoadedContextModel = waitForLoadedContextModel, modifier = Modifier.systemBarsPadding())
    }
}
