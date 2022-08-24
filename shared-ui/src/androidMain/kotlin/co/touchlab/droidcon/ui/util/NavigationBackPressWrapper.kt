package co.touchlab.droidcon.ui.util

import androidx.compose.runtime.Composable

@Composable
internal actual fun NavigationBackPressWrapper(content: @Composable () -> Unit) {
    // For now no back press wrapping is needed on Android.
    content()
}
