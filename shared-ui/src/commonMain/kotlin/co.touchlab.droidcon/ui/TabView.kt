package co.touchlab.droidcon.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import co.touchlab.droidcon.ui.session.SessionDetailView
import co.touchlab.droidcon.ui.session.SessionListView
import co.touchlab.droidcon.ui.session.SpeakerDetailView
import co.touchlab.droidcon.ui.settings.SettingsView
import co.touchlab.droidcon.ui.sponsors.SponsorDetailView
import co.touchlab.droidcon.ui.sponsors.SponsorsView
import co.touchlab.droidcon.viewmodel.TabComponent
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.scale
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation

@Composable
internal fun TabView(component: TabComponent, modifier: Modifier = Modifier) {
    Children(
        stack = component.stack,
        modifier = modifier,
        animation = stackAnimation(fade() + scale()),
    ) {
        when (val child = it.instance) {
            is TabComponent.Child.Main.Schedule -> SessionListView(child.component)
            is TabComponent.Child.Main.Agenda -> SessionListView(child.component)
            is TabComponent.Child.Main.Sponsors -> SponsorsView(child.component)
            is TabComponent.Child.Main.Settings -> SettingsView(child.component)
            is TabComponent.Child.Session -> SessionDetailView(child.component)
            is TabComponent.Child.Sponsor -> SponsorDetailView(child.component)
            is TabComponent.Child.Speaker -> SpeakerDetailView(child.component)
        }
    }
}
