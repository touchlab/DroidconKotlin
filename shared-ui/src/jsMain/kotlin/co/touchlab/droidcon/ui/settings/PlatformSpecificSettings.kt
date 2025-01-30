package co.touchlab.droidcon.ui.settings

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import co.touchlab.droidcon.Constants
import co.touchlab.droidcon.viewmodel.settings.SettingsViewModel

// TODO: Double Check this
@Composable
internal actual fun PlatformSpecificSettingsView(viewModel: SettingsViewModel) {}

@Composable
internal actual fun PlatformSwitchApp() {
    Button(
        onClick = {},
    ) {
        Text("Open ${Constants.SisterApp.name}")
    }
}
