package co.touchlab.droidcon.ui.venue

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import com.github.panpf.zoomimage.ZoomImage
import io.kamel.core.Resource
import io.kamel.image.asyncPainterResource

@Composable
actual fun VenueBodyView(modifier: Modifier, venueMapUrl: String?){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        if (venueMapUrl != null) {
            when (val resource = asyncPainterResource(venueMapUrl)) {
                is Resource.Loading -> {
                    CircularProgressIndicator()
                }
                is Resource.Success -> {
                    val painter: Painter = resource.value
                    ZoomImage(
                        painter = painter,
                        contentDescription = null,
                        modifier = modifier.fillMaxSize(),
                    )
                }
                is Resource.Failure -> {
                    Text("Error loading venue map.")
                }
            }
        } else {
            CircularProgressIndicator()
        }
    }
}
