package co.touchlab.droidcon.ui

import androidx.compose.ui.window.ComposeUIViewController
import co.touchlab.droidcon.ui.venue.VenueBodyView
import co.touchlab.droidcon.ui.venue.VenueView
import co.touchlab.droidcon.viewmodel.ApplicationViewModel
import droidcon.shared_ui.generated.resources.venue_map_1

@Suppress("unused")
fun getRootController(viewModel: ApplicationViewModel) = ComposeUIViewController {
    MainComposeView(viewModel)
}

@Suppress("unused")
fun venueBodyViewController() = ComposeUIViewController {
    VenueBodyView()
}
