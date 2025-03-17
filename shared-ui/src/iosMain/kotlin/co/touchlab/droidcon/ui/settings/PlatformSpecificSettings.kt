package co.touchlab.droidcon.ui.settings

import androidx.compose.runtime.Composable
import co.touchlab.droidcon.viewmodel.settings.SettingsViewModel

@Composable
internal actual fun PlatformSpecificSettingsView(viewModel: SettingsViewModel) {
    // No platform-specific settings needed for iOS
}
