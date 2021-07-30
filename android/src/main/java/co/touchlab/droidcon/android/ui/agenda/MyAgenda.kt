package co.touchlab.droidcon.android.ui.agenda

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import co.touchlab.droidcon.R
import co.touchlab.droidcon.android.ui.sessions.SessionList
import co.touchlab.droidcon.android.viewModel.sessions.AgendaViewModel

@Composable
fun MyAgenda(navController: NavHostController) {
    val agenda = viewModel<AgendaViewModel>()
    SessionList(navController = navController, sessions = agenda, emptyRes = R.string.my_agenda_empty)
}