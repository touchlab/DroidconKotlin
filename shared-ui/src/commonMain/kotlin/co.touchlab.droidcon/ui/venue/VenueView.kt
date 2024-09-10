package co.touchlab.droidcon.ui.venue

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.panpf.zoomimage.ZoomImage
import droidcon.shared_ui.generated.resources.venue_map_1
import org.jetbrains.compose.resources.painterResource

@Composable
fun VenueView() {
    Scaffold { paddingValues ->
        VenueBodyView(
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun VenueBodyView(modifier: Modifier = Modifier) {
    ZoomImage(
        painter = painterResource(droidcon.shared_ui.generated.resources.Res.drawable.venue_map_1),
        contentDescription = null,
        modifier = modifier.fillMaxSize(),
    )
}
