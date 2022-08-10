package co.touchlab.droidcon.ios.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Badge
import androidx.compose.material.BadgedBox
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.touchlab.droidcon.ios.viewmodel.session.SessionBlockViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun SessionBlockView(sessionsBlock: SessionBlockViewModel) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopStart) {
        Text(
            text = sessionsBlock.time,
            modifier = Modifier
                .width(100.dp)
                .padding(8.dp),
            textAlign = TextAlign.End,
        )

        Column(modifier = Modifier.padding(start = 72.dp)) {
            sessionsBlock.sessions.forEach { session ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val isInPast by session.observeIsInPast.observeAsState()
                    val badgeColor = when {
                        !session.isAttending -> Color.Transparent
                        isInPast -> Color.Gray
                        session.isInConflict -> Colors.orange
                        else -> Colors.skyBlue
                    }
                    BadgedBox(
                        modifier = Modifier.padding(16.dp),
                        badge = { Badge(backgroundColor = badgeColor, modifier = Modifier.size(8.dp)) { } }
                    ) { }

                    val backgroundColor =
                        if (isInPast) Color.hsl(hue = 0f, saturation = 0f, lightness = 0.9f) else MaterialTheme.colors.background
                    val isClickable = !session.isServiceSession
                    Card(
                        modifier = Modifier.weight(1f),
                        backgroundColor = backgroundColor,
                        onClick = {
                            session.selected()
                        },
                        elevation = 2.dp,
                        enabled = isClickable,
                        border = null,
                    ) {
                        Column {
                            Text(text = session.title, modifier = Modifier.padding(8.dp), fontWeight = FontWeight.Bold)
                            Text(
                                text = session.speakers,
                                modifier = Modifier.padding(
                                    start = 8.dp,
                                    end = 8.dp,
                                    bottom = 8.dp,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }
}
