package co.touchlab.droidcon.ui.util

import androidx.compose.runtime.Composable

@Composable
internal actual fun NavigationBackPressWrapper(content: @Composable (() -> Unit)) {
    // No back-press handling needed on web.
    content()
}
