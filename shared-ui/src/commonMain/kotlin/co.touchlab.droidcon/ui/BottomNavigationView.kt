package co.touchlab.droidcon.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BottomNavigationView(viewModel: ApplicationViewModel, modifier: Modifier = Modifier) {
    val selectedTab by viewModel.observeSelectedTab.observeAsState()

    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar {
                viewModel.tabs.forEach { tab ->
                    val (title, icon) = when (tab) {
                        ApplicationViewModel.Tab.Schedule -> "Schedule" to Icons.Filled.CalendarMonth
                        ApplicationViewModel.Tab.MyAgenda -> "My Agenda" to Icons.Filled.Schedule
                        ApplicationViewModel.Tab.Sponsors -> "Sponsors" to Icons.Filled.LocalFireDepartment
                        ApplicationViewModel.Tab.Settings -> "Settings" to Icons.Filled.Settings
                    }
                    NavigationBarItem(
                        icon = { Icon(imageVector = icon, contentDescription = null) },
                        label = { Text(text = title) },
                        selected = selectedTab == tab,
                        onClick = {
                            viewModel.selectedTab = tab
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = MaterialTheme.colorScheme.primary,
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding())) {
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
