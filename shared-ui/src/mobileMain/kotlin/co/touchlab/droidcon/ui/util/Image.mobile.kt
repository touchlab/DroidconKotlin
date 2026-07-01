package co.touchlab.droidcon.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import co.touchlab.kermit.Logger
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImage
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.crossfade
import coil3.util.DebugLogger

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

@OptIn(ExperimentalCoilApi::class)
@Composable
actual fun InitImageLoader() {
    setSingletonImageLoaderFactory { context ->
        dcImageLoader(context, true)
    }
}

private fun dcImageLoader(context: PlatformContext, debug: Boolean = false): ImageLoader = ImageLoader.Builder(context)
    .crossfade(true)
    .apply {
        if (debug) {
            logger(DebugLogger())
        }
    }
    .build()
