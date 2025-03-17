package co.touchlab.droidcon.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import co.touchlab.droidcon.ui.theme.DroidconTheme
import co.touchlab.droidcon.ui.util.dcImageLoader
import co.touchlab.droidcon.viewmodel.ApplicationViewModel
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.setSingletonImageLoaderFactory

@OptIn(ExperimentalCoilApi::class)
@Composable
internal fun MainComposeView(viewModel: ApplicationViewModel, modifier: Modifier = Modifier) {
    setSingletonImageLoaderFactory { context ->
        dcImageLoader(context, true)
    }

    // Check if this is the first run
    LaunchedEffect(Unit) {
        viewModel.checkFirstRun()
    }

    // Get state from viewModel directly
    val isFirstRun = viewModel.isFirstRun.value
    val conferences = viewModel.allConferences.value

    DroidconTheme {
        // Show first run dialog if needed
        if (isFirstRun && conferences.isNotEmpty()) {
            FirstRunConferenceSelector(
                conferences = conferences,
                onConferenceSelected = { conference ->
                    viewModel.selectConference(conference.id)
                    // Navigate to the schedule tab after selection
                    viewModel.selectedTab = ApplicationViewModel.Tab.Schedule
                },
                onDismiss = {
                    // Use the first conference as default if user dismisses
                    if (conferences.isNotEmpty()) {
                        viewModel.selectConference(conferences.first().id)
                    }
                    // Navigate to the schedule tab
                    viewModel.selectedTab = ApplicationViewModel.Tab.Schedule
                },
            )
        }

        BottomNavigationView(viewModel = viewModel, modifier = modifier)
    }
}
