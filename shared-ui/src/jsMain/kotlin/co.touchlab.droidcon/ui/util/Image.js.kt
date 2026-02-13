package co.touchlab.droidcon.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import co.touchlab.kermit.Logger
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
actual fun DcAsyncImage(logTag: String, url: String?, contentDescription: String?, modifier: Modifier) {
    if(url != null) {
        KamelImage(
            resource = { asyncPainterResource(data = url) },
            modifier = modifier,
            contentDescription = "Profile",

            onFailure = { exception ->
                Logger.e(
                    logTag,
                    throwable = exception,
                    message = {
                        "AsyncImage OnError Request = ${exception}\n"
                    },
                )
            },
        )
    }
}

@Composable
actual fun InitImageLoader() {
}
