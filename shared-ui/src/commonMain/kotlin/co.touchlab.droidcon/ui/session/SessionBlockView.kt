package co.touchlab.droidcon.ui.session

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.touchlab.droidcon.ui.theme.Dimensions
import co.touchlab.droidcon.ui.util.observeAsState
import co.touchlab.droidcon.viewmodel.session.SessionBlockViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SessionBlockView(sessionsBlock: SessionBlockViewModel) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopStart) {
        Text(
            text = sessionsBlock.time,
            modifier = Modifier
                .width(100.dp)
                .padding(Dimensions.Padding.half),
            textAlign = TextAlign.End,
        )

        Column(modifier = Modifier.padding(start = 72.dp), verticalArrangement = Arrangement.spacedBy(Dimensions.Padding.quarter)) {
            sessionsBlock.sessions.forEach { session ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val isInPast by session.observeIsInPast.observeAsState()
                    val badgeColor = when {
                        !session.isAttending -> Color.Transparent
                        isInPast -> MaterialTheme.colorScheme.outlineVariant
                        session.isInConflict -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.primary
                    }
                    Box(modifier = Modifier.padding(Dimensions.Padding.default).size(8.dp).clip(CircleShape).background(badgeColor))

                    val isClickable = !session.isServiceSession
                    Card(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            session.selected()
                        },
                        enabled = isClickable,
                    ) {
                        Column {
                            Text(
                                text = session.title,
                                modifier = Modifier.padding(Dimensions.Padding.half),
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.titleMedium,
                            )
                            session.room?.let { roomName ->
                                Text(
                                    text = "in $roomName",
                                    modifier = Modifier.padding(
                                        start = Dimensions.Padding.half,
                                        bottom = Dimensions.Padding.half,
                                    ),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                )
                            }
                            Text(
                                text = session.speakers,
                                modifier = Modifier.padding(
                                    start = Dimensions.Padding.half,
                                    end = Dimensions.Padding.half,
                                    bottom = Dimensions.Padding.half,
                                ),
                                style = MaterialTheme.typography.titleSmall,
                            )
                        }
                    }
                }
            }
        }
    }
}
