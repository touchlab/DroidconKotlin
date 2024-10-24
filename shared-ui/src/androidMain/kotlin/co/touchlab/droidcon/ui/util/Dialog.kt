package co.touchlab.droidcon.ui.util

import androidx.compose.ui.window.Dialog as AndroidXComposeDialog
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal actual fun Dialog(dismiss: () -> Unit, content: @Composable () -> Unit) {
    AndroidXComposeDialog(
        onDismissRequest = dismiss,
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false, usePlatformDefaultWidth = false),
    ) {
        content()
    }
}
