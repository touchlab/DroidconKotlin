package co.touchlab.droidcon.android.ui.sessions

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import co.touchlab.droidcon.R
import co.touchlab.droidcon.android.ui.main.ScheduleScreen
import co.touchlab.droidcon.android.ui.theme.Dimensions
import co.touchlab.droidcon.android.ui.theme.Toolbar
import co.touchlab.droidcon.android.viewModel.sessions.BaseSessionsViewModel

@Composable
fun SessionList(navController: NavHostController, sessions: BaseSessionsViewModel) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val days by sessions.days.collectAsState()
    LaunchedEffect(days) {
        selectedTabIndex = selectedTabIndex.coerceIn(days.indices)
    }

    Scaffold(
        topBar = {
            Toolbar(titleRes = R.string.droidcon_title, navController = navController)
        }
    ) {
        Column {
            TabRow(selectedTabIndex = selectedTabIndex) {
                days.forEachIndexed { index, daySchedule ->
                    Tab(selected = selectedTabIndex == index, onClick = { selectedTabIndex = index }) {
                        Text(
                            text = daySchedule.day,
                            modifier = Modifier.padding(Dimensions.Padding.default),
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
            LazyColumn(contentPadding = PaddingValues(vertical = Dimensions.Padding.quarter)) {
                val daySchedule = days.getOrNull(selectedTabIndex)?.blocks ?: emptyList()
                items(daySchedule) { hourBlock ->
                    Box(modifier = Modifier.padding(vertical = Dimensions.Padding.quarter, horizontal = Dimensions.Padding.half)) {
                        SessionBlock(hourBlock) { tappedSession ->
                            navController.navigate(ScheduleScreen.EventDetail.createRoute(tappedSession.id))
                        }
                    }
                }
            }
        }
    }
}