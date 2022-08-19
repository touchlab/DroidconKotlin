package co.touchlab.droidcon.ui

import androidx.compose.ui.window.Application
import co.touchlab.droidcon.viewmodel.ApplicationComponent

fun getRootController(component: ApplicationComponent) = Application("MainComposeView") {
    MainComposeView(component)
}
