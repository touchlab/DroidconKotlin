package co.touchlab.droidcon.android.ui.agenda

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import co.touchlab.droidcon.android.ui.schedule.AgendaViewModel
import co.touchlab.droidcon.android.ui.schedule.SessionList

@Composable
fun MyAgenda(navController: NavHostController) {
    val agenda = viewModel<AgendaViewModel>()
    SessionList(navController = navController, sessions = agenda)
}