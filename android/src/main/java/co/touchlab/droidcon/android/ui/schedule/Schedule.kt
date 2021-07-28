package co.touchlab.droidcon.android.ui.schedule

import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import co.touchlab.droidcon.R
import co.touchlab.droidcon.android.ui.theme.Toolbar

@Composable
fun Schedule(navController: NavHostController) {
    Scaffold(topBar = {
        Toolbar(titleRes = R.string.schedule_title, navController = navController)
    }) {
        Text("Schedule body")
    }
}