package co.touchlab.droidcon.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.request.crossfade
import coil3.util.DebugLogger

@Composable
expect fun DcAsyncImage(logTag: String, url: String?, contentDescription: String?, modifier: Modifier = Modifier)

fun dcImageLoader(context: PlatformContext, debug: Boolean = false): ImageLoader = ImageLoader.Builder(context)
    .crossfade(true)
    .apply {
        if (debug) {
            logger(DebugLogger())
        }
    }
    .build()

