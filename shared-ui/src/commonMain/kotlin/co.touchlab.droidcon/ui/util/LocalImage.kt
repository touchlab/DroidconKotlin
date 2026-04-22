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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ContentScale.Companion
import co.touchlab.droidcon.ui.theme.Dimensions
import droidcon.shared_ui.generated.resources.Res
import droidcon.shared_ui.generated.resources.about_droidcon
import droidcon.shared_ui.generated.resources.about_kotlin
import droidcon.shared_ui.generated.resources.about_touchlab
import droidcon.shared_ui.generated.resources.linkedin
import droidcon.shared_ui.generated.resources.twitter
import droidcon.shared_ui.generated.resources.venue_map_1
import kotlinx.coroutines.CoroutineDispatcher
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun LocalImage(imageResourceName: String, modifier: Modifier = Modifier, contentDescription: String? = null, contentScale: ContentScale = ContentScale.FillWidth) {
    val imageRes = when(imageResourceName.lowercase()){
        "about_droidcon" -> Res.drawable.about_droidcon
        "about_touchlab" -> Res.drawable.about_touchlab
        "about_kotlin" -> Res.drawable.about_kotlin
        "linkedin" -> Res.drawable.linkedin
        "twitter" -> Res.drawable.twitter
        "venue-map-1" -> Res.drawable.venue_map_1
        else -> null
    }
    if (imageRes != null) {
        Image(
            modifier = modifier,
            painter = painterResource(imageRes),
            contentDescription = contentDescription,
            contentScale = contentScale,
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
