package co.touchlab.droidcon.ui.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Aod
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import co.touchlab.droidcon.viewmodel.settings.SettingsViewModel

@Composable
internal actual fun PlatformSpecificSettingsView(viewModel: SettingsViewModel) {
    IconTextSwitchRow(
        text = "Use compose for iOS",
        image = Icons.Default.Aod,
        checked = viewModel.observeUseCompose,
    )

    Divider()
}
