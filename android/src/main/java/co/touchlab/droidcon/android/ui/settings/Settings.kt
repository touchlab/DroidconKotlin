package co.touchlab.droidcon.android.ui.settings

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import co.touchlab.droidcon.R
import co.touchlab.droidcon.android.ui.main.SettingsScreen
import co.touchlab.droidcon.android.ui.theme.Dimensions
import co.touchlab.droidcon.android.ui.theme.Toolbar

@Composable
fun Settings(navController: NavHostController) {
    Scaffold(topBar = {
        Toolbar(titleRes = R.string.settings_title, navController = navController)
    }) {
        Column(modifier = Modifier.fillMaxSize()) {
            val enableFeedback = remember { mutableStateOf(false) }
            IconTextSwitchRow(
                iconRes = R.drawable.ic_baseline_feedback_24,
                textRes = R.string.settings_enable_feedback_title,
                checked = enableFeedback,
            )

            val enableReminders = remember { mutableStateOf(false) }
            IconTextSwitchRow(
                iconRes = R.drawable.ic_baseline_insert_invitation_24,
                textRes = R.string.settings_enable_reminders_title,
                checked = enableReminders,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate(SettingsScreen.About.route) },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    modifier = Modifier.padding(Dimensions.Padding.default),
                    painter = painterResource(id = R.drawable.menu_info),
                    contentDescription = "About",
                )
                Text(
                    modifier = Modifier.padding(end = Dimensions.Padding.default),
                    text = stringResource(id = R.string.settings_about_title),
                )
            }
        }
    }
}

@Composable
private fun IconTextSwitchRow(@DrawableRes iconRes: Int, @StringRes textRes: Int, checked: MutableState<Boolean>) {
    var isChecked by checked
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isChecked = !isChecked },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.padding(Dimensions.Padding.default),
            painter = painterResource(id = iconRes),
            contentDescription = stringResource(id = textRes),
        )
        Text(
            modifier = Modifier.weight(1f),
            text = stringResource(id = textRes),
        )
        Switch(
            modifier = Modifier.padding(Dimensions.Padding.default),
            checked = isChecked,
            onCheckedChange = { isChecked = it },
        )
    }
}