package co.touchlab.droidcon.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.touchlab.droidcon.ui.icons.CalendarMonth
import co.touchlab.droidcon.ui.icons.LocalFireDepartment
import co.touchlab.droidcon.ui.icons.Schedule
import co.touchlab.droidcon.ui.icons.Settings
import co.touchlab.droidcon.viewmodel.ApplicationComponent
import co.touchlab.droidcon.viewmodel.ApplicationComponent.FeedbackChild
import co.touchlab.droidcon.viewmodel.TabComponent
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState

@Composable
internal fun BottomNavigationView(component: ApplicationComponent, modifier: Modifier = Modifier) {
    Scaffold(
        modifier = modifier,
        bottomBar = {
            val tabStack by component.tabStack.subscribeAsState()

            BottomNavigation(elevation = 0.dp) {
                TabComponent.Tab.values().forEach { tab ->
                    val (title, icon) = when (tab) {
                        TabComponent.Tab.Schedule -> "Schedule" to Icons.Filled.CalendarMonth
                        TabComponent.Tab.Agenda -> "My Agenda" to Icons.Filled.Schedule
                        TabComponent.Tab.Sponsors -> "Sponsors" to Icons.Filled.LocalFireDepartment
                        TabComponent.Tab.Settings -> "Settings" to Icons.Filled.Settings
                    }
                    BottomNavigationItem(
                        icon = { Icon(imageVector = icon, contentDescription = null) },
                        label = { Text(text = title) },
                        selected = tabStack.active.instance.tab == tab,
                        onClick = { component.selectTab(tab = tab) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Children(
            stack = component.tabStack,
            modifier = Modifier.padding(innerPadding),
            animation = stackAnimation(fade()),
        ) {
            TabView(
                component = it.instance,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }

    val feedbackStack by component.feedbackStack.subscribeAsState()
    when (val instance = feedbackStack.active.instance) {
        is FeedbackChild.None -> Unit
        is FeedbackChild.Feedback -> FeedbackDialog(instance.component)
    }
}
