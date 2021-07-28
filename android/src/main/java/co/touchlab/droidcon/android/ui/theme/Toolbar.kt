package co.touchlab.droidcon.android.ui.theme

import android.app.Activity
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import co.touchlab.droidcon.R

@Composable
fun Toolbar(
    title: String,
    showBackButton: Boolean = false,
    actions: (@Composable RowScope.() -> Unit) = {},
    navController: NavHostController,
) {
    val activity = LocalContext.current as Activity
    TopAppBar(
        title = { Text(text = title) },
        // backgroundColor = MaterialTheme.colors.surface,
        // contentColor = Colors.white,
        navigationIcon = if (showBackButton) {
            {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_chevron_left_24),
                        contentDescription = "back",
                        // tint = Colors.pink
                    )
                }
            }
        } else {
            null
        },
        actions = actions
    )
}