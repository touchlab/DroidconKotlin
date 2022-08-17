package co.touchlab.droidcon.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal expect fun __RemoteImage(imageUrl: String, modifier: Modifier, contentDescription: String?)

@Composable
internal fun RemoteImage(imageUrl: String, modifier: Modifier = Modifier, contentDescription: String? = null) {
    __RemoteImage(imageUrl, modifier, contentDescription)
}
