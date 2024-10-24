@file:Suppress("ktlint:standard:filename")

package co.touchlab.droidcon.ui.util

import android.annotation.SuppressLint
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import co.touchlab.droidcon.ui.theme.Dimensions

// Use of the function getIdentifier is discouraged, but we need to use it since the drawable names are defined in the common code for both
// platforms and on each platform we need to get the drawable according to provided name.
@SuppressLint("ComposableNaming", "DiscouragedApi")
@Composable
internal actual fun __LocalImage(imageResourceName: String, modifier: Modifier, contentDescription: String?) {
    val context = LocalContext.current
    val imageRes = context.resources.getIdentifier(imageResourceName, "drawable", context.packageName).takeIf { it != 0 }
    if (imageRes != null) {
        androidx.compose.foundation.Image(
            modifier = modifier,
            painter = painterResource(id = imageRes),
            contentDescription = contentDescription,
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
