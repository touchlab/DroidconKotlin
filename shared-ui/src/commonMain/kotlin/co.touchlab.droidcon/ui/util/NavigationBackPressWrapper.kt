package co.touchlab.droidcon.ui.util

import androidx.compose.runtime.Composable

@Composable
internal expect fun NavigationBackPressWrapper(content: @Composable () -> Unit)
