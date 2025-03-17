package co.touchlab.droidcon.ui.settings

import androidx.compose.runtime.Composable
import co.touchlab.droidcon.viewmodel.settings.SettingsViewModel

@Composable
internal expect fun PlatformSpecificSettingsView(viewModel: SettingsViewModel)
