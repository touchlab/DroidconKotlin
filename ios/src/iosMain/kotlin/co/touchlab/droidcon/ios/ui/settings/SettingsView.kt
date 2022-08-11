package co.touchlab.droidcon.ios.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Aod
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import co.touchlab.droidcon.ios.ui.theme.Dimensions
import co.touchlab.droidcon.ios.ui.util.observeAsState
import co.touchlab.droidcon.ios.viewmodel.settings.SettingsViewModel
import org.brightify.hyperdrive.multiplatformx.property.MutableObservableProperty

@Composable
internal fun SettingsView(viewModel: SettingsViewModel) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Settings") }) },
    ) {
        val scrollState = rememberScrollState()
        Column(modifier = Modifier.fillMaxHeight().verticalScroll(scrollState)) {
            IconTextSwitchRow(
                text = "Enable feedback",
                image = Icons.Default.MailOutline,
                checked = viewModel.observeIsFeedbackEnabled,
            )

            Divider()

            IconTextSwitchRow(
                text = "Enable reminders",
                image = Icons.Default.Notifications,
                checked = viewModel.observeIsRemindersEnabled,
            )

            Divider()

            IconTextSwitchRow(
                text = "Use compose for iOS",
                image = Icons.Default.Aod,
                checked = viewModel.observeUseCompose,
            )

            Divider()

            AboutView(viewModel.about)
        }
    }
}

@Composable
private fun IconTextSwitchRow(text: String, image: ImageVector, checked: MutableObservableProperty<Boolean>) {
    val isChecked by checked.observeAsState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { checked.value = !checked.value },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.padding(Dimensions.Padding.default),
            imageVector = image,
            contentDescription = text,
        )
        Text(
            modifier = Modifier.weight(1f),
            text = text,
        )
        Switch(
            modifier = Modifier.padding(vertical = Dimensions.Padding.half, horizontal = 24.dp),
            checked = isChecked,
            onCheckedChange = { checked.value = it },
        )
    }
}
