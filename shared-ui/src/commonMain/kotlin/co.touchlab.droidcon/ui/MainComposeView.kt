package co.touchlab.droidcon.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import co.touchlab.droidcon.domain.entity.Conference
import co.touchlab.droidcon.ui.theme.DroidconTheme
import co.touchlab.droidcon.ui.util.InitImageLoader
import co.touchlab.droidcon.ui.util.observeAsState
import co.touchlab.droidcon.viewmodel.ApplicationViewModel
import co.touchlab.droidcon.viewmodel.WaitForLoadedContextModel

@Composable
internal fun MainComposeView(waitForLoadedContextModel: WaitForLoadedContextModel, modifier: Modifier = Modifier) {
    InitImageLoader()

    LaunchedEffect(Unit) {
        waitForLoadedContextModel.watchConferenceChanges()
    }

    LaunchedEffect(Unit) {
        waitForLoadedContextModel.monitorConferenceChanges()
    }

    val loadingState by waitForLoadedContextModel.state.collectAsState()

    DroidconTheme {
        when (val state = loadingState) {
            WaitForLoadedContextModel.State.Loading -> CircularProgressIndicator("Updating Droidcon Events!")
            is WaitForLoadedContextModel.State.Ready -> MainAppBody(waitForLoadedContextModel, state.conference, modifier)
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

        print("isFirstRun")
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
