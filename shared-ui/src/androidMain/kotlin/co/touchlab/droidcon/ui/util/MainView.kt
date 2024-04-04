package co.touchlab.droidcon.ui.util

import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import co.touchlab.droidcon.ui.MainComposeView
import co.touchlab.droidcon.viewmodel.ApplicationViewModel

@Composable
fun MainView(
    viewModel: ApplicationViewModel,
    chatView: @Composable () -> Unit,
) {
    MainComposeView(
        viewModel = viewModel,
        chatView = chatView,
        modifier = Modifier.systemBarsPadding(),
    )
}
