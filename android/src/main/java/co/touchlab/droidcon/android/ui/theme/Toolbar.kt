package co.touchlab.droidcon.android.ui.theme

import android.app.Activity
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import co.touchlab.droidcon.R

@Composable
fun Toolbar(
    @StringRes
    titleRes: Int,
    showBackButton: Boolean = false,
    actions: (@Composable RowScope.() -> Unit) = {},
    navController: NavHostController,
) {
    val activity = LocalContext.current as Activity
    TopAppBar(
        title = { Text(text = stringResource(id = titleRes)) },
        // backgroundColor = MaterialTheme.colors.surface,
        // contentColor = Colors.white,
        navigationIcon = if (showBackButton) {
            {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_arrow_back_24),
                        contentDescription = "Back",
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