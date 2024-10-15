package co.touchlab.droidcon.ui

import androidx.compose.ui.window.ComposeUIViewController
import co.touchlab.droidcon.ui.venue.VenueBodyView
import co.touchlab.droidcon.viewmodel.ApplicationViewModel

@Suppress("unused")
fun getRootController(viewModel: ApplicationViewModel) = ComposeUIViewController {
    MainComposeView(viewModel)
}

@Suppress("unused")
fun venueBodyViewController() = ComposeUIViewController {
    VenueBodyView()
}
