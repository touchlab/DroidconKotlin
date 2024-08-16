package co.touchlab.droidcon.ui.venue

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import co.touchlab.droidcon.util.NavigationStack
import com.github.panpf.zoomimage.ZoomImage
import com.github.panpf.zoomimage.compose.ZoomState
import com.github.panpf.zoomimage.compose.rememberZoomState
import droidcon.shared_ui.generated.resources.venue_map_1
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VenueView() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Venue Map") },
            )
        },
    ) { paddingValues ->
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
