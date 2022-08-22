package co.touchlab.droidcon.ui.session

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import co.touchlab.droidcon.ui.theme.Dimensions
import co.touchlab.droidcon.viewmodel.session.SessionDayComponent
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState

@Composable
internal fun SessionDayView(day: SessionDayComponent, modifier: Modifier = Modifier) {
    val model by day.model.subscribeAsState()

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(vertical = Dimensions.Padding.quarter),
    ) {
        items(model.blocks) { block ->
            Box(
                modifier = Modifier.padding(
                    vertical = Dimensions.Padding.quarter,
                    horizontal = Dimensions.Padding.half,
                ),
            ) {
                SessionBlockView(block = block, onSessionClick = day::itemSelected)
            }
        }
    }
}
