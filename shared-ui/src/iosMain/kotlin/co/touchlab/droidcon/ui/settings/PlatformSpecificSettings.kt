package co.touchlab.droidcon.ui.settings

import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import co.touchlab.droidcon.ui.icons.Aod
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
