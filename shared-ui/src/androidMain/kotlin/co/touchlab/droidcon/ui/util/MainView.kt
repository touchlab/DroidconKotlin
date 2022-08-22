package co.touchlab.droidcon.ui.util

import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import co.touchlab.droidcon.ui.MainComposeView
import co.touchlab.droidcon.viewmodel.ApplicationComponent

@Composable
fun MainView(component: ApplicationComponent) {
    MainComposeView(component = component, modifier = Modifier.systemBarsPadding())
}
