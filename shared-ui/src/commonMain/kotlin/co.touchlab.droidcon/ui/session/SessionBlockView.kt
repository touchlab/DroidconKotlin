package co.touchlab.droidcon.ui.session

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.touchlab.droidcon.ui.theme.Colors
import co.touchlab.droidcon.ui.theme.Dimensions
import co.touchlab.droidcon.viewmodel.session.SessionDayComponent

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun SessionBlockView(block: SessionDayComponent.Model.Block, onSessionClick: (SessionDayComponent.Model.Item) -> Unit) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopStart) {
        Text(
            text = block.time,
            modifier = Modifier
                .width(100.dp)
                .padding(Dimensions.Padding.half),
            textAlign = TextAlign.End,
        )

        Column(modifier = Modifier.padding(start = 72.dp)) {
            block.items.forEach { session ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val badgeColor = when {
                        !session.isAttending -> Color.Transparent
                        session.isInPast -> Color.Gray
                        session.isInConflict -> Colors.orange
                        else -> Colors.skyBlue
                    }
                    Box(modifier = Modifier.padding(Dimensions.Padding.default).size(8.dp).clip(CircleShape).background(badgeColor))

                    val backgroundColor =
                        if (session.isInPast) MaterialTheme.colors.surface else MaterialTheme.colors.background
                    val isClickable = !session.isServiceSession
                    Card(
                        modifier = Modifier.weight(1f),
                        backgroundColor = backgroundColor,
                        onClick = { onSessionClick(session) },
                        elevation = 2.dp,
                        enabled = isClickable,
                        border = null,
                    ) {
                        Column {
                            Text(text = session.title, modifier = Modifier.padding(Dimensions.Padding.half), fontWeight = FontWeight.Bold)
                            Text(
                                text = session.speakers,
                                modifier = Modifier.padding(
                                    start = Dimensions.Padding.half,
                                    end = Dimensions.Padding.half,
                                    bottom = Dimensions.Padding.half,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}
