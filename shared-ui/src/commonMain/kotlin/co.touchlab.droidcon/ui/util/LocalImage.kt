package co.touchlab.droidcon.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineDispatcher
@Composable
internal expect fun __LocalImage(imageResourceName: String, modifier: Modifier, contentDescription: String?)

expect val IODispatcher: CoroutineDispatcher

@Composable
internal fun LocalImage(imageResourceName: String, modifier: Modifier = Modifier, contentDescription: String? = null) {
    __LocalImage(imageResourceName, modifier, contentDescription)
}
