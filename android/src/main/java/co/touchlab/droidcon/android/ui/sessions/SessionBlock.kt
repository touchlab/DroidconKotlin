package co.touchlab.droidcon.android.ui.sessions

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.BadgeBox
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.touchlab.droidcon.android.ui.theme.Colors
import co.touchlab.droidcon.android.ui.theme.Dimensions
import co.touchlab.droidcon.android.viewModel.sessions.SessionViewModel
import co.touchlab.droidcon.android.viewModel.sessions.SessionsBlockViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SessionBlock(sessionsBlock: SessionsBlockViewModel, sessionTapped: (SessionViewModel) -> Unit) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopStart) {
        Text(
            text = sessionsBlock.time,
            modifier = Modifier
                .width(100.dp)
                .padding(Dimensions.Padding.half),
            textAlign = TextAlign.End,
        )

        val hasEnded by sessionsBlock.hasEnded.collectAsState(initial = false)

        Column(modifier = Modifier.padding(start = 72.dp)) {
            sessionsBlock.sessions.forEach { session ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val badgeColor = when {
                        !session.isInAttending -> Color.Transparent
                        hasEnded -> Colors.grey
                        session.isColliding -> Colors.orange
                        else -> Colors.skyBlue
                    }
                    BadgeBox(
                        modifier = Modifier.padding(Dimensions.Padding.default),
                        backgroundColor = badgeColor,
                    ) { }

                    val backgroundColor = if (hasEnded) Colors.lightGrey222 else Color.White
                    Card(
                        modifier = Modifier.weight(1f),
                        backgroundColor = backgroundColor,
                        onClick = {
                            sessionTapped(session)
                        },
                        elevation = 2.dp,
                    ) {
                        Column {
                            Text(text = session.title, modifier = Modifier.padding(Dimensions.Padding.half))
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