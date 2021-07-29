package co.touchlab.droidcon.android.ui.schedule

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

data class DaySchedule(
    val dayString: String,
    val hourBlocks: List<HourBlock>,
)

data class HourBlock(
    val id: Long,
    var time: String,
    val description: String,
)

val days: List<DaySchedule> = listOf(
    DaySchedule(
        "AUG 1",
        listOf(
            HourBlock(0L, "8:00 AM", "Welcome party"),
            HourBlock(1L, "9:00 AM", "First presentation\n\nCome learn something about Compose, it's fun!"),
        ),
    ),
    DaySchedule(
        "AUG 2",
        listOf(
            HourBlock(2L, "8:00 AM", "Welcome party"),
            HourBlock(3L, "11:00 PM", "Last presentation\n\nWe will say our heartfelt goodbyes..."),
        ),
    ),
)

@Composable
fun Schedule(navController: NavHostController) {
    var selectedTabIndex by remember { mutableStateOf(0) }

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
                            text = daySchedule.dayString,
                            modifier = Modifier.padding(Dimensions.Padding.default),
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
            LazyColumn(contentPadding = PaddingValues(vertical = Dimensions.Padding.quarter)) {
                val daySchedule = days[selectedTabIndex]
                items(daySchedule.hourBlocks) { hourBlock ->
                    Box(modifier = Modifier.padding(vertical = Dimensions.Padding.quarter, horizontal = Dimensions.Padding.half)) {
                        HourBlock(hourBlock) {
                            navController.navigate(ScheduleScreen.EventDetail.createRoute(hourBlock.id))
                        }
                    }
                }
            }
        }
    }
}