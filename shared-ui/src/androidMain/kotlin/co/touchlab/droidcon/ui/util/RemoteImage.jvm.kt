package co.touchlab.droidcon.ui.util

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.annotation.ExperimentalCoilApi
import coil.compose.LocalImageLoader
import coil.compose.rememberImagePainter

@OptIn(ExperimentalCoilApi::class)
@Composable
internal actual fun __RemoteImage(imageUrl: String, modifier: Modifier, contentDescription: String?) {
    val painter = rememberImagePainter(
        data = imageUrl,
        imageLoader = LocalImageLoader.current,
        builder = {
            placeholder(0)
        }
    )

    Image(
        painter = painter,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier,
    )
}
