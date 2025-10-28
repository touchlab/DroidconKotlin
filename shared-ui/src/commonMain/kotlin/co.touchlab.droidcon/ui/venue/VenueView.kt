package co.touchlab.droidcon.ui.venue

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import co.touchlab.droidcon.domain.entity.Conference
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.github.panpf.zoomimage.ZoomImage

@Composable
fun VenueView(venueMapUrl: String?) {
    Scaffold { paddingValues ->
        VenueBodyView(
            modifier = Modifier.padding(paddingValues),
            venueMapUrl,
        )
    }
}

@Composable
fun VenueBodyView(modifier: Modifier = Modifier, venueMapUrl: String?) {
    val painter = rememberAsyncImagePainter(venueMapUrl)
    val state by painter.state.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        when (state) {
            is AsyncImagePainter.State.Empty,
            is AsyncImagePainter.State.Loading -> {
                CircularProgressIndicator()
            }
            is AsyncImagePainter.State.Error -> {
                Text("Error loading venue map.")
            }
            is AsyncImagePainter.State.Success -> {
                ZoomImage(
                    painter = painter,
                    contentDescription = null,
                    modifier = modifier.fillMaxSize(),
                )
            }
        }
    }
}
