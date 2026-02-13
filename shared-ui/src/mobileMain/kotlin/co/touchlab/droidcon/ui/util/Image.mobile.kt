package co.touchlab.droidcon.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import co.touchlab.kermit.Logger
import coil3.compose.AsyncImage

@Composable
actual fun DcAsyncImage(logTag: String, url: String?, contentDescription: String?, modifier: Modifier) {
    AsyncImage(
        modifier = modifier,
        model = url,
        contentDescription = contentDescription,
        onError = {
            Logger.e(
                logTag,
                throwable = it.result.throwable,
                message = {
                    "AsyncImage OnError Request = ${it.result.request}\n"
                },
            )
        },
    )
}

