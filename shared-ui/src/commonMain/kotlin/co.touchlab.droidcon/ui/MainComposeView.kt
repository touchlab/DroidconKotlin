package co.touchlab.droidcon.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import co.touchlab.droidcon.ui.theme.DroidconTheme
import co.touchlab.droidcon.ui.util.dcImageLoader
import co.touchlab.droidcon.viewmodel.ApplicationViewModel
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.setSingletonImageLoaderFactory

@OptIn(ExperimentalCoilApi::class)
@Composable
internal fun MainComposeView(
    viewModel: ApplicationViewModel,
    modifier: Modifier = Modifier,
) {
    setSingletonImageLoaderFactory { context ->
        dcImageLoader(context, true)
    }
    DroidconTheme {
        BottomNavigationView(viewModel = viewModel, modifier = modifier)
    }
}
