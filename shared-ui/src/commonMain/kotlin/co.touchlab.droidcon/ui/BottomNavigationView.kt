package co.touchlab.droidcon.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRailItemColors
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import co.touchlab.droidcon.domain.entity.Conference
import co.touchlab.droidcon.ui.session.SessionListDetailPaneScaffold
import co.touchlab.droidcon.ui.settings.SettingsView
import co.touchlab.droidcon.ui.sponsors.SponsorsView
import co.touchlab.droidcon.ui.util.observeAsState
import co.touchlab.droidcon.ui.venue.VenueView
import co.touchlab.droidcon.viewmodel.ApplicationViewModel
import co.touchlab.droidcon.viewmodel.session.BaseSessionListViewModel

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun BottomNavigationView(
    viewModel: ApplicationViewModel,
    currentConference: Conference,
    modifier: Modifier = Modifier,
) {
    val selectedTab by viewModel.observeSelectedTab.observeAsState()
    val iconColor = MaterialTheme.colorScheme.onPrimary
    val textColor = MaterialTheme.colorScheme.primary
    val indicatorColor = MaterialTheme.colorScheme.primary
    val railColors = NavigationRailItemColors(
        selectedIconColor = iconColor,
        selectedTextColor = textColor,
        selectedIndicatorColor = indicatorColor,
        unselectedIconColor = iconColor,
        unselectedTextColor = textColor,
        disabledIconColor = iconColor,
        disabledTextColor = textColor,
    )
    val barColors = NavigationBarItemColors(
        selectedIconColor = iconColor,
        selectedTextColor = textColor,
        selectedIndicatorColor = indicatorColor,
        unselectedIconColor = iconColor,
        unselectedTextColor = textColor,
        disabledIconColor = iconColor,
        disabledTextColor = textColor,
    )
    val drawerColors = NavigationDrawerItemDefaults.colors(
        selectedIconColor = iconColor,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        selectedContainerColor = MaterialTheme.colorScheme.primary,
    )

    NavigationSuiteScaffold(
        modifier = modifier,
        navigationSuiteItems = {
            viewModel.listTabs(currentConference).forEach { tab ->
                val (title, icon) = when (tab) {
                    ApplicationViewModel.Tab.Schedule -> "Schedule" to Icons.Filled.CalendarMonth
                    // FIXME: Was originally "My agenda" but then it doesn't seem to fit.
                    ApplicationViewModel.Tab.MyAgenda -> "Agenda" to Icons.Filled.Schedule
                    ApplicationViewModel.Tab.Venue -> "Venue" to Icons.Filled.Map
                    ApplicationViewModel.Tab.Sponsors -> "Sponsors" to Icons.Filled.LocalFireDepartment
                    ApplicationViewModel.Tab.Settings -> "Settings" to Icons.Filled.Settings
                }
                item(
                    icon = { Icon(imageVector = icon, contentDescription = null) },
                    label = { Text(text = title) },
                    selected = selectedTab == tab,
                    onClick = {
                        viewModel.selectedTab = tab
                    },
                    colors = NavigationSuiteItemColors(
                        navigationRailItemColors = railColors,
                        navigationBarItemColors = barColors,
                        navigationDrawerItemColors = drawerColors,
                    ),
                )
            }
        },
        content = {
            when (selectedTab) {
                ApplicationViewModel.Tab.Schedule -> SessionListDetailPaneScaffold(
                    viewModel = viewModel.schedule,
                    title = currentConference.name,
                    emptyText = "Sessions could not be loaded.",
                )

                ApplicationViewModel.Tab.MyAgenda -> SessionListDetailPaneScaffold(
                    viewModel = viewModel.agenda,
                    title = "Agenda",
                    emptyText = "Add sessions to your agenda from session detail in schedule.",
                )

                ApplicationViewModel.Tab.Venue -> VenueView(currentConference.venueMap)
                ApplicationViewModel.Tab.Sponsors -> SponsorsView(viewModel.sponsors)
                ApplicationViewModel.Tab.Settings -> SettingsView(viewModel.settings)
            }
        },
    )





    val feedback by viewModel.observePresentedFeedback.observeAsState()
    feedback?.let {
        FeedbackDialog(it)
    }
}
