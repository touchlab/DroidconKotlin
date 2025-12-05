package co.touchlab.droidcon.ui.util

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import co.touchlab.droidcon.ui.theme.Dimensions
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.await
import org.jetbrains.skia.Image.Companion.makeFromEncoded
import org.khronos.webgl.Int8Array

@Composable
internal actual fun __LocalImage(imageResourceName: String, modifier: Modifier, contentDescription: String?) {
    val painter: Painter? by produceState(null, imageResourceName) {
        value = try {
            val fetched = window.fetch("drawable/$imageResourceName").await()
            if (fetched.ok) {
                val bytes = Int8Array(fetched.arrayBuffer().await()).unsafeCast<ByteArray>()
                makeFromEncoded(bytes).toComposeImageBitmap().let(::BitmapPainter)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    val currentPainter = painter
    if (currentPainter != null) {
        Image(
            painter = currentPainter,
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.FillWidth,
        )
    } else {
        Row(
            modifier = modifier.background(MaterialTheme.colorScheme.primary, RoundedCornerShape(Dimensions.Padding.half)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = contentDescription,
                modifier = Modifier.padding(Dimensions.Padding.half),
                tint = Color.White,
            )
            Text("Image not supported", modifier = Modifier.padding(Dimensions.Padding.default), color = Color.White)
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

actual val IODispatcher: CoroutineDispatcher = Dispatchers.Default
