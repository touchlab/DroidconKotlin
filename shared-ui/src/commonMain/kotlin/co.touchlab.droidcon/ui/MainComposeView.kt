package co.touchlab.droidcon.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import co.touchlab.droidcon.domain.entity.Conference
import co.touchlab.droidcon.ui.theme.DroidconTheme
import co.touchlab.droidcon.ui.util.dcImageLoader
import co.touchlab.droidcon.ui.util.observeAsState
import co.touchlab.droidcon.viewmodel.ApplicationViewModel
import co.touchlab.droidcon.viewmodel.WaitForLoadedContextModel
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.setSingletonImageLoaderFactory

@OptIn(ExperimentalCoilApi::class)
@Composable
internal fun MainComposeView(waitForLoadedContextModel:WaitForLoadedContextModel, modifier: Modifier = Modifier) {
    setSingletonImageLoaderFactory { context ->
        dcImageLoader(context, true)
    }

    LaunchedEffect(Unit){
        waitForLoadedContextModel.watchConferenceChanges(this)
    }

    LaunchedEffect(Unit){
        waitForLoadedContextModel.monitorConferenceChanges()
    }

    val loadingState by waitForLoadedContextModel.state.collectAsState()

    DroidconTheme {
        when(val state = loadingState){
            WaitForLoadedContextModel.State.Loading -> Text("Loading")
            is WaitForLoadedContextModel.State.Ready -> MainAppBody(waitForLoadedContextModel, state.conference, modifier)
        }
    }
}

@Composable
private fun MainAppBody(
    waitForLoadedContextModel:WaitForLoadedContextModel,
    selectedConference: Conference,
    modifier: Modifier,
) {
    LaunchedEffect(selectedConference){
        waitForLoadedContextModel.applicationViewModel.runAllLiveTasks(selectedConference)
    }

    val viewModel = waitForLoadedContextModel.applicationViewModel

    // Get state from viewModel directly
    val isFirstRun by viewModel.isFirstRun.observeAsState()
    val conferences = viewModel.allConferences.value

    // Show first run dialog if needed
    if (isFirstRun && conferences.isNotEmpty()) {
        // Get currently selected conference if any
        FirstRunConferenceSelector(
            conferences = conferences,
            selectedConference = selectedConference,
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

    BottomNavigationView(viewModel = viewModel, currentConference = selectedConference, modifier = modifier)
}
