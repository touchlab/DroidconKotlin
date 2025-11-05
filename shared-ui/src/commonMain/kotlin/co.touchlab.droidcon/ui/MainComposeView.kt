package co.touchlab.droidcon.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
internal fun MainComposeView(waitForLoadedContextModel: WaitForLoadedContextModel, modifier: Modifier = Modifier) {
    setSingletonImageLoaderFactory { context ->
        dcImageLoader(context, true)
    }

    LaunchedEffect(Unit) {
        waitForLoadedContextModel.watchConferenceChanges()
    }

    LaunchedEffect(Unit) {
        waitForLoadedContextModel.monitorConferenceChanges()
    }

    val loadingState by waitForLoadedContextModel.state.collectAsState()

    DroidconTheme {
        when (val state = loadingState) {
            WaitForLoadedContextModel.State.Loading -> LoadingScreen()
            is WaitForLoadedContextModel.State.Ready -> MainAppBody(waitForLoadedContextModel, state.conference, modifier)
        }
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Updating Droidcon Events!")
        }
    }
}

@Composable
private fun MainAppBody(waitForLoadedContextModel: WaitForLoadedContextModel, selectedConference: Conference, modifier: Modifier) {
    LaunchedEffect(selectedConference) {
        waitForLoadedContextModel.applicationViewModel.runAllLiveTasks(selectedConference)
    }

    val viewModel = waitForLoadedContextModel.applicationViewModel

    // Get state from viewModel directly
    val isFirstRun by viewModel.isFirstRun.observeAsState()

    // Show first run dialog if needed
    if (isFirstRun) {
        val conferences = viewModel.allConferences.value

        val onConferenceSelected: (Conference) -> Unit = { conference ->
            viewModel.selectConference(conference.id)
            // Navigate to the schedule tab after selection
            viewModel.selectedTab = ApplicationViewModel.Tab.Schedule
        }

        if (conferences.size == 1) {
            LaunchedEffect(conferences) {
                onConferenceSelected(conferences.first())
            }
        } else if (conferences.size > 1) {
            FirstRunConferenceSelector(
                conferences = conferences,
                selectedConference = selectedConference,
                onConferenceSelected = onConferenceSelected,
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
    }

    BottomNavigationView(viewModel = viewModel, currentConference = selectedConference, modifier = modifier)
}
