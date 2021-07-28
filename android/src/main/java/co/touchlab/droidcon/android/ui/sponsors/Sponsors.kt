package co.touchlab.droidcon.android.ui.sponsors

import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import co.touchlab.droidcon.R
import co.touchlab.droidcon.android.ui.theme.Toolbar

@Composable
fun Sponsors(navController: NavHostController) {
    Scaffold(topBar = {
        Toolbar(titleRes = R.string.sponsors_title, navController = navController)
    }) {
        Text("Sponsors body")
    }
}