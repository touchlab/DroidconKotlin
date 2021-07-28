package co.touchlab.droidcon.android.ui.agenda

import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import co.touchlab.droidcon.R
import co.touchlab.droidcon.android.ui.theme.Toolbar

@Composable
fun MyAgenda(navController: NavHostController) {
    Scaffold(topBar = {
        Toolbar(titleRes = R.string.my_agenda_title, navController = navController)
    }) {
        Text("My Agenda body")
    }
}