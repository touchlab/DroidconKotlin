package co.touchlab.droidcon.android.ui.schedule

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import co.touchlab.droidcon.android.ui.sessions.SessionList
import co.touchlab.droidcon.android.viewModel.sessions.ScheduleViewModel

@Composable
fun Schedule(navController: NavHostController) {
    val schedule = viewModel<ScheduleViewModel>()

    SessionList(navController = navController, sessions = schedule)
}
