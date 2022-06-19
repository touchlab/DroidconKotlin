package co.touchlab.droidcon.android.ui.settings

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import co.touchlab.droidcon.R
import co.touchlab.droidcon.android.ui.main.SettingsScreen
import co.touchlab.droidcon.android.ui.theme.Dimensions
import co.touchlab.droidcon.android.ui.theme.Toolbar
import co.touchlab.droidcon.android.viewModel.settings.AboutViewModel
import co.touchlab.droidcon.android.viewModel.settings.SettingsViewModel
import kotlinx.coroutines.flow.MutableStateFlow

@Composable
fun Settings(navController: NavHostController) {
    val settings = viewModel<SettingsViewModel>()

    Scaffold(topBar = {
        Toolbar(titleRes = R.string.settings_title, navController = navController)
    }) {
        val scrollState = rememberScrollState()
        Column(modifier = Modifier.fillMaxSize().padding(it).verticalScroll(scrollState)) {
            IconTextSwitchRow(
                iconRes = R.drawable.ic_baseline_feedback_24,
                textRes = R.string.settings_enable_feedback_title,
                checked = settings.isFeedbackEnabled,
            )

            IconTextSwitchRow(
                iconRes = R.drawable.ic_baseline_insert_invitation_24,
                textRes = R.string.settings_enable_reminders_title,
                checked = settings.isRemindersEnabled,
            )

            About()
        }
    }
}

@Composable
private fun IconTextSwitchRow(@DrawableRes iconRes: Int, @StringRes textRes: Int, checked: MutableStateFlow<Boolean>) {
    val isChecked by checked.collectAsState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { checked.value = !checked.value },
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
            onCheckedChange = { checked.value = it },
        )
    }
}