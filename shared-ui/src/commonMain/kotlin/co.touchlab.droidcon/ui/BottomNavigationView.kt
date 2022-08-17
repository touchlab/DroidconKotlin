package co.touchlab.droidcon.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.touchlab.droidcon.ui.icons.CalendarMonth
import co.touchlab.droidcon.ui.icons.LocalFireDepartment
import co.touchlab.droidcon.ui.icons.Schedule
import co.touchlab.droidcon.ui.icons.Settings
import co.touchlab.droidcon.ui.session.SessionListView
import co.touchlab.droidcon.ui.settings.SettingsView
import co.touchlab.droidcon.ui.sponsors.SponsorsView
import co.touchlab.droidcon.ui.util.observeAsState
import co.touchlab.droidcon.viewmodel.ApplicationViewModel

@Composable
internal fun BottomNavigationView(viewModel: ApplicationViewModel, modifier: Modifier = Modifier) {
    val selectedTab by viewModel.observeSelectedTab.observeAsState()

    Scaffold(
        modifier = modifier,
        bottomBar = {
            BottomNavigation(elevation = 0.dp) {
                viewModel.tabs.forEach { tab ->
                    val (title, icon) = when (tab) {
                        ApplicationViewModel.Tab.Schedule -> "Schedule" to Icons.Filled.CalendarMonth
                        ApplicationViewModel.Tab.MyAgenda -> "My Agenda" to Icons.Filled.Schedule
                        ApplicationViewModel.Tab.Sponsors -> "Sponsors" to Icons.Filled.LocalFireDepartment
                        ApplicationViewModel.Tab.Settings -> "Settings" to Icons.Filled.Settings
                    }
                    BottomNavigationItem(
                        icon = { Icon(imageVector = icon, contentDescription = null) },
                        label = { Text(text = title) },
                        selected = selectedTab == tab,
                        onClick = {
                            viewModel.selectedTab = tab
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                ApplicationViewModel.Tab.Schedule -> SessionListView(viewModel.schedule)
                ApplicationViewModel.Tab.MyAgenda -> SessionListView(viewModel.agenda)
                ApplicationViewModel.Tab.Sponsors -> SponsorsView(viewModel.sponsors)
                ApplicationViewModel.Tab.Settings -> SettingsView(viewModel.settings)
            }
        }
    }

    val feedback by viewModel.observePresentedFeedback.observeAsState()
    feedback?.let {
        FeedbackDialog(it)
    }
}
