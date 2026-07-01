package co.touchlab.droidcon.ui.venue

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

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
expect fun VenueBodyView(modifier: Modifier = Modifier, venueMapUrl: String?)
