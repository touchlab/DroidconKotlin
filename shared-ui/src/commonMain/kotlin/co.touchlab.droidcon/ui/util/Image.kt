package co.touchlab.droidcon.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import co.touchlab.kermit.Logger
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.compose.AsyncImage
import coil3.request.crossfade
import coil3.util.DebugLogger

@Composable
fun DcAsyncImage(logTag: String, model: Any?, contentDescription: String?, modifier: Modifier = Modifier) {
    AsyncImage(
        modifier = modifier,
        model = model,
        contentDescription = contentDescription,
        onError = {
            Logger.e(
                messageString = logTag,
                throwable = it.result.throwable,
                tag = "AsyncImage OnError Request = ${it.result.request}\n",
            )
        },
    )
}

fun dcImageLoader(context: PlatformContext, debug: Boolean = false): ImageLoader = ImageLoader.Builder(context)
    .crossfade(true)
    .apply {
        if (debug) {
            logger(DebugLogger())
        }
    }
    .build()
