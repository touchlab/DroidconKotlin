package co.touchlab.droidcon.ui.util

import androidx.compose.runtime.Composable

@Composable
internal expect fun Dialog(dismiss: () -> Unit, content: @Composable () -> Unit)
