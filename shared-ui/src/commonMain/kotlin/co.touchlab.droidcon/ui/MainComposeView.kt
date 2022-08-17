package co.touchlab.droidcon.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import co.touchlab.droidcon.ui.theme.DroidconTheme
import co.touchlab.droidcon.viewmodel.ApplicationViewModel

@Composable
internal fun MainComposeView(viewModel: ApplicationViewModel, modifier: Modifier = Modifier) {
    DroidconTheme {
        BottomNavigationView(viewModel = viewModel, modifier = modifier)
    }
}
