package co.touchlab.droidcon.ios.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import co.touchlab.droidcon.ios.viewmodel.ApplicationViewModel

@Composable
internal fun BottomNavigationView(viewModel: ApplicationViewModel) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        modifier = Modifier,
        bottomBar = {
            BottomNavigation {
                BottomNavigationItem(
                    icon = { Icon(imageVector = Icons.Default.DateRange, contentDescription = null) },
                    label = { Text(text = "Schedule") },
                    selected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                    }
                )
                BottomNavigationItem(
                    icon = { Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null) },
                    label = { Text(text = "My Agenda") },
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                    }
                )
                BottomNavigationItem(
                    icon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
                    label = { Text(text = "Sponsors") },
                    selected = selectedTab == 2,
                    onClick = {
                        selectedTab = 2
                    }
                )
                BottomNavigationItem(
                    icon = { Icon(imageVector = Icons.Default.Settings, contentDescription = null) },
                    label = { Text(text = "Settings") },
                    selected = selectedTab == 3,
                    onClick = {
                        selectedTab = 3
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> SessionListView(viewModel.schedule)
                1 -> SessionListView(viewModel.agenda)
                2 -> SponsorsView(viewModel.sponsors)
                3 -> SettingsView(viewModel.settings)
            }
        }
    }

    val feedback by viewModel.observePresentedFeedback.observeAsState()
    feedback?.let {
        FeedbackDialog(it)
    }
}
