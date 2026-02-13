package co.touchlab.droidcon.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import co.touchlab.kermit.Logger
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
actual fun DcAsyncImage(logTag: String, url: String?, contentDescription: String?, modifier: Modifier) {
    KamelImage(
        resource = {
            asyncPainterResource(data = "url")
        },
        contentDescription = "Profile",
        modifier = Modifier,
        onFailure = {
            Logger.e(
                tag = logTag,
                throwable = it,
                messageString = "AsyncImage OnError Request = ${it}\n",
            )
        },
    )
}
