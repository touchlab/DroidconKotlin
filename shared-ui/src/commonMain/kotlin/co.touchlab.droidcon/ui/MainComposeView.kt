package co.touchlab.droidcon.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import co.touchlab.droidcon.ui.theme.DroidconTheme
import co.touchlab.droidcon.viewmodel.ApplicationComponent

@Composable
internal fun MainComposeView(component: ApplicationComponent, modifier: Modifier = Modifier) {
    DroidconTheme {
        BottomNavigationView(component = component, modifier = modifier)
    }
}
