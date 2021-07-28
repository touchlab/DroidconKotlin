package co.touchlab.droidcon.android.ui.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import co.touchlab.droidcon.R
import co.touchlab.droidcon.android.ui.agenda.MyAgenda
import co.touchlab.droidcon.android.ui.schedule.Schedule
import co.touchlab.droidcon.android.ui.settings.Settings
import co.touchlab.droidcon.android.ui.sponsors.Sponsors

sealed class Tab(val route: String, @StringRes val titleRes: Int, @DrawableRes val image: Int) {
    object Schedule: Tab("schedule", R.string.schedule_title, R.drawable.menu_schedule)
    object MyAgenda: Tab("myAgenda", R.string.my_agenda_title, R.drawable.menu_my_agenda)
    object Sponsors: Tab("sponsors", R.string.sponsors_title, R.drawable.menu_sponsor)
    object Settings: Tab("settings", R.string.settings_title, R.drawable.menu_settings)
}

val tabs: List<Tab> = listOf(Tab.Schedule, Tab.MyAgenda, Tab.Sponsors, Tab.Settings)

@Composable
fun Main() {
    val navController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                tabs.forEach { tab ->
                    BottomNavigationItem(
                        icon = { Icon(painterResource(id = tab.image), contentDescription = null) },
                        label = { Text(text = stringResource(id = tab.titleRes)) },
                        alwaysShowLabel = false,
                        selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = Tab.Schedule.route, Modifier.padding(innerPadding)) {
            composable(Tab.Schedule.route) { Schedule(navController) }
            composable(Tab.MyAgenda.route) { MyAgenda(navController) }
            composable(Tab.Sponsors.route) { Sponsors(navController) }
            composable(Tab.Settings.route) { Settings(navController) }
        }
    }
}