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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import co.touchlab.droidcon.R
import co.touchlab.droidcon.android.ui.ProfileDetail
import co.touchlab.droidcon.android.ui.agenda.MyAgenda
import co.touchlab.droidcon.android.ui.feedback.Feedback
import co.touchlab.droidcon.android.ui.schedule.Schedule
import co.touchlab.droidcon.android.ui.sessions.SessionDetail
import co.touchlab.droidcon.android.ui.settings.About
import co.touchlab.droidcon.android.ui.settings.Settings
import co.touchlab.droidcon.android.ui.sponsors.SponsorDetail
import co.touchlab.droidcon.android.ui.sponsors.SponsorList
import co.touchlab.droidcon.android.viewModel.MainViewModel
import co.touchlab.droidcon.domain.entity.Profile
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.entity.Sponsor
import com.google.accompanist.insets.systemBarsPadding

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

sealed class ScheduleScreen(val route: String) {
    object Main: ScheduleScreen("schedule/main")
    object SessionDetail: ScheduleScreen("schedule/sessionDetail-{sessionId}") {

        fun createRoute(sessionId: Session.Id) = "schedule/sessionDetail-${sessionId.value}"
    }

    object SpeakerDetail: ScheduleScreen("schedule/speakerDetail-{speakerId}") {

        fun createRoute(speakerId: Profile.Id) = "schedule/speakerDetail-${speakerId.value}"
    }
}

sealed class SponsorsScreen(val route: String) {
    object Main: SponsorsScreen("sponsors/main")
    object Detail: SponsorsScreen("sponsors/detail/{sponsorGroup}-{sponsorName}") {

        fun createRoute(sponsorId: Sponsor.Id) = "sponsors/detail/${sponsorId.group}-${sponsorId.name}"
    }

    object RepresentativeDetail: ScheduleScreen("sponsors/representativeDetail-{representativeId}") {

        fun createRoute(representativeId: Profile.Id) = "sponsors/representativeDetail-${representativeId.value}"
    }
}

val tabs: List<MainTab> = listOf(MainTab.Schedule, MainTab.MyAgenda, MainTab.Sponsors, MainTab.Settings)

@Composable
fun Main() {
    val main = viewModel<MainViewModel>()
    
    val feedback by main.showFeedback.collectAsState()
    feedback?.let {
        Feedback(it)
    }

    val navController = rememberNavController()
    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                tabs.forEach { tab ->
                    BottomNavigationItem(
                        icon = { Icon(painterResource(id = tab.image), contentDescription = null) },
                        label = { Text(text = stringResource(id = tab.titleRes)) },
                        selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true,
                        onClick = {
                            if (currentDestination?.hierarchy?.any { it.route == tab.route } == true) {
                                return@BottomNavigationItem
                            }
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
            navigation(ScheduleScreen.Main.route, MainTab.Schedule.route) {
                composable(ScheduleScreen.Main.route) { Schedule(navController) }
                composable(ScheduleScreen.SessionDetail.route) { backStackEntry ->
                    val sessionId = backStackEntry.arguments?.getString("sessionId")
                    requireNotNull(sessionId) { "Parameter sessionId not found." }
                    SessionDetail(navController, Session.Id(sessionId))
                }
                composable(ScheduleScreen.SpeakerDetail.route) { backStackEntry ->
                    val speakerId = backStackEntry.arguments?.getString("speakerId")
                    requireNotNull(speakerId) { "Parameter speakerId not found." }
                    ProfileDetail(navController, Profile.Id(speakerId))
                }
            }

            composable(MainTab.MyAgenda.route) { MyAgenda(navController) }

            navigation(SponsorsScreen.Main.route, MainTab.Sponsors.route) {
                composable(SponsorsScreen.Main.route) { SponsorList(navController) }
                composable(SponsorsScreen.Detail.route) { backStackEntry ->
                    val sponsorName = requireNotNull(backStackEntry.arguments?.getString("sponsorName")) {
                        "Parameter sponsorName not found."
                    }
                    val sponsorGroup = requireNotNull(backStackEntry.arguments?.getString("sponsorGroup")) {
                        "Parameter sponsorGroup not found."
                    }

                    SponsorDetail(navController, Sponsor.Id(name = sponsorName, group = sponsorGroup))
                }
                composable(SponsorsScreen.RepresentativeDetail.route) { backStackEntry ->
                    val representativeId = backStackEntry.arguments?.getString("representativeId")
                    requireNotNull(representativeId) { "Parameter representativeId not found." }
                    ProfileDetail(navController, Profile.Id(representativeId))
                }
            }

            navigation(SettingsScreen.Main.route, MainTab.Settings.route) {
                composable(SettingsScreen.Main.route) { Settings(navController) }
                composable(SettingsScreen.About.route) { About(navController) }
            }
        }
    }
}