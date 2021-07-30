package co.touchlab.droidcon.android.ui.sessions

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import co.touchlab.droidcon.R
import co.touchlab.droidcon.android.ui.main.ScheduleScreen
import co.touchlab.droidcon.android.ui.theme.Colors
import co.touchlab.droidcon.android.ui.theme.Dimensions
import co.touchlab.droidcon.android.ui.theme.Toolbar
import co.touchlab.droidcon.android.viewModel.sessions.BaseSessionsViewModel

@Composable
fun SessionList(navController: NavHostController, sessions: BaseSessionsViewModel, @StringRes emptyRes: Int) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val days by sessions.days.collectAsState()
    LaunchedEffect(days) {
        selectedTabIndex = selectedTabIndex.coerceIn(days.indices.takeUnless { it.isEmpty() } ?: IntRange(0, 0))
    }

    Scaffold(
        topBar = {
            Toolbar(titleRes = R.string.droidcon_title, navController = navController)
        }
    ) {
        Column {
            if (days.isEmpty()) {
                Empty(emptyRes)
            } else {
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
                                navController.navigate(ScheduleScreen.SessionDetail.createRoute(tappedSession.id))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Empty(@StringRes textRes: Int) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_baseline_calendar_today_24),
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .padding(Dimensions.Padding.default),
            tint = Colors.lightYellow,
        )

        Text(
            text = stringResource(id = textRes),
            modifier = Modifier.padding(Dimensions.Padding.default),
            textAlign = TextAlign.Center,
        )
    }
}