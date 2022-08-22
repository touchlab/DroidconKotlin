package co.touchlab.droidcon.ui.settings

import androidx.compose.runtime.Composable
import co.touchlab.droidcon.viewmodel.settings.SettingsComponent

@Composable
internal expect fun PlatformSpecificSettingsView(component: SettingsComponent)
