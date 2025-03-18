package co.touchlab.droidcon.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import co.touchlab.droidcon.ui.theme.DroidconTheme
import co.touchlab.droidcon.ui.util.dcImageLoader
import co.touchlab.droidcon.viewmodel.ApplicationViewModel
import co.touchlab.droidcon.viewmodel.WaitForLoadedContextModel
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.setSingletonImageLoaderFactory

@OptIn(ExperimentalCoilApi::class)
@Composable
internal fun MainComposeView(waitForLoadedContextModel:WaitForLoadedContextModel, viewModel: ApplicationViewModel, modifier: Modifier = Modifier) {
    setSingletonImageLoaderFactory { context ->
        dcImageLoader(context, true)
    }

    LaunchedEffect(Unit){
        waitForLoadedContextModel.waitTillReady()
    }

    LaunchedEffect(Unit){
        waitForLoadedContextModel.monitorConferenceChanges()
    }

    val loadingState by waitForLoadedContextModel.state.collectAsState()

    DroidconTheme {
        when(loadingState){
            WaitForLoadedContextModel.State.Loading -> Text("Loading")
            WaitForLoadedContextModel.State.Ready -> MainAppBody(viewModel, modifier)
        }
    }
}

@Composable
private fun MainAppBody(
    viewModel: ApplicationViewModel,
    modifier: Modifier,
) {
    // Get state from viewModel directly
    val isFirstRun = viewModel.isFirstRun.value
    val conferences = viewModel.allConferences.value

    // Show first run dialog if needed
    if (isFirstRun && conferences.isNotEmpty()) {
        // Get currently selected conference if any
        val currentConference = viewModel.currentConference.value

        FirstRunConferenceSelector(
            conferences = conferences,
            selectedConference = currentConference,
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
