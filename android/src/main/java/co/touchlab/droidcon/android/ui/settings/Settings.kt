package co.touchlab.droidcon.android.ui.settings

import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import co.touchlab.droidcon.R
import co.touchlab.droidcon.android.ui.theme.Toolbar

@Composable
fun Settings(navController: NavHostController) {
    Scaffold(topBar = {
        Toolbar(titleRes = R.string.settings_title, navController = navController)
    }) {
        Text("Settings body")
    }
}