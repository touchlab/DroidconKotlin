package co.touchlab.droidcon.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItemColors
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRailItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.PaneScaffoldDirective
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.window.core.layout.WindowSizeClass
import co.touchlab.droidcon.domain.entity.Conference
import co.touchlab.droidcon.ui.session.SessionListView
import co.touchlab.droidcon.ui.settings.SettingsView
import co.touchlab.droidcon.ui.sponsors.SponsorsView
import co.touchlab.droidcon.ui.util.observeAsState
import co.touchlab.droidcon.ui.venue.VenueView
import co.touchlab.droidcon.viewmodel.ApplicationViewModel

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun BottomNavigationView(
    viewModel: ApplicationViewModel,
    currentConference: Conference,
    modifier: Modifier = Modifier,
) {
    val selectedTab by viewModel.observeSelectedTab.observeAsState()
    val navigator = rememberListDetailPaneScaffoldNavigator<Nothing>()

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
    ListDetailPaneScaffold(
        modifier = modifier,
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = {
            Box(Modifier.background(Color.Red)) {
                NavigationSuiteScaffold(
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
                                )
                            )
                        }
                    },
                    content = {
                        Box {
                            when (selectedTab) {
                                ApplicationViewModel.Tab.Schedule -> SessionListView(
                                    viewModel = viewModel.schedule,
                                    title = currentConference.name,
                                    emptyText = "Sessions could not be loaded.",
                                )

                                ApplicationViewModel.Tab.MyAgenda -> SessionListView(
                                    viewModel = viewModel.agenda,
                                    title = "Agenda",
                                    emptyText = "Add sessions to your agenda from session detail in schedule.",
                                )

                                ApplicationViewModel.Tab.Venue -> VenueView(currentConference.venueMap)
                                ApplicationViewModel.Tab.Sponsors -> SponsorsView(viewModel.sponsors)
                                ApplicationViewModel.Tab.Settings -> SettingsView(viewModel.settings)
                            }
                        }
                    }
                )
            }
        },
        detailPane = {},
    )

    val feedback by viewModel.observePresentedFeedback.observeAsState()
    feedback?.let {
        FeedbackDialog(it)
    }
}/*
    ListDetailPaneScaffold(
        modifier = modifier,
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = {
            NavigationSuiteScaffold(
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
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = MaterialTheme.colorScheme.primary,
                                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                            ),
                        )
                    }
                }
            )
        })
    {
                    innerPadding ->
                Box(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
                    when (selectedTab) {
                        ApplicationViewModel.Tab.Schedule -> SessionListView(
                            viewModel = viewModel.schedule,
                            title = currentConference.name,
                            emptyText = "Sessions could not be loaded.",
                        )

                        ApplicationViewModel.Tab.MyAgenda -> SessionListView(
                            viewModel = viewModel.agenda,
                            title = "Agenda",
                            emptyText = "Add sessions to your agenda from session detail in schedule.",
                        )

                        ApplicationViewModel.Tab.Venue -> VenueView(currentConference.venueMap)
                        ApplicationViewModel.Tab.Sponsors -> SponsorsView(viewModel.sponsors)
                        ApplicationViewModel.Tab.Settings -> SettingsView(viewModel.settings)
                    }
                }
            }
        },
        detailPane = {
            Text("World")

        },
        extraPane = {
            Text("Etc")

        },
    )*/
/*
    Scaffold(
        modifier = modifier,
        bottomBar = {
            NavigationBar {
                viewModel.listTabs(currentConference).forEach { tab ->
                    val (title, icon) = when (tab) {
                        ApplicationViewModel.Tab.Schedule -> "Schedule" to Icons.Filled.CalendarMonth
                        // FIXME: Was originally "My agenda" but then it doesn't seem to fit.
                        ApplicationViewModel.Tab.MyAgenda -> "Agenda" to Icons.Filled.Schedule
                        ApplicationViewModel.Tab.Venue -> "Venue" to Icons.Filled.Map
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
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                        ),
                    )
                }
            }
        },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())) {
            when (selectedTab) {
                ApplicationViewModel.Tab.Schedule -> SessionListView(
                    viewModel = viewModel.schedule,
                    title = currentConference.name,
                    emptyText = "Sessions could not be loaded.",
                )

                ApplicationViewModel.Tab.MyAgenda -> SessionListView(
                    viewModel = viewModel.agenda,
                    title = "Agenda",
                    emptyText = "Add sessions to your agenda from session detail in schedule.",
                )

                ApplicationViewModel.Tab.Venue -> VenueView(currentConference.venueMap)
                ApplicationViewModel.Tab.Sponsors -> SponsorsView(viewModel.sponsors)
                ApplicationViewModel.Tab.Settings -> SettingsView(viewModel.settings)
            }
        }
    }
*/
