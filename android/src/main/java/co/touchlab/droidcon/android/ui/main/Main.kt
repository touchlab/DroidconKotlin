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
import androidx.navigation.navigation
import co.touchlab.droidcon.R
import co.touchlab.droidcon.android.ui.agenda.MyAgenda
import co.touchlab.droidcon.android.ui.schedule.Schedule
import co.touchlab.droidcon.android.ui.settings.About
import co.touchlab.droidcon.android.ui.settings.Settings
import co.touchlab.droidcon.android.ui.sponsors.Sponsors

sealed class MainTab(val route: String, @StringRes val titleRes: Int, @DrawableRes val image: Int) {
    object Schedule: MainTab("schedule", R.string.schedule_title, R.drawable.menu_schedule)
    object MyAgenda: MainTab("myAgenda", R.string.my_agenda_title, R.drawable.menu_my_agenda)
    object Sponsors: MainTab("sponsors", R.string.sponsors_title, R.drawable.menu_sponsor)
    object Settings: MainTab("settings", R.string.settings_title, R.drawable.menu_settings)
}

sealed class SettingsScreen(val route: String) {
    object Main: SettingsScreen("settings/main")
    object About: SettingsScreen("settings/about")
}

val tabs: List<MainTab> = listOf(MainTab.Schedule, MainTab.MyAgenda, MainTab.Sponsors, MainTab.Settings)

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
        NavHost(navController, startDestination = MainTab.Schedule.route, Modifier.padding(innerPadding)) {
            composable(MainTab.Schedule.route) { Schedule(navController) }

            composable(MainTab.MyAgenda.route) { MyAgenda(navController) }

            composable(MainTab.Sponsors.route) { Sponsors(navController) }

            navigation(SettingsScreen.Main.route, MainTab.Settings.route) {
                composable(SettingsScreen.Main.route) { Settings(navController) }
                composable(SettingsScreen.About.route) { About(navController) }
            }
        }
    }
}