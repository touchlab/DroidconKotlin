package co.touchlab.droidcon.ios.ui.session

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
import co.touchlab.droidcon.ios.ui.theme.Colors
import co.touchlab.droidcon.ios.ui.theme.Dimensions
import co.touchlab.droidcon.ios.ui.util.observeAsState
import co.touchlab.droidcon.ios.viewmodel.session.SessionBlockViewModel

@OptIn(ExperimentalMaterialApi::class)
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
                        modifier = Modifier.padding(Dimensions.Padding.default),
                        badge = { Badge(backgroundColor = badgeColor, modifier = Modifier.size(Dimensions.Padding.half)) { } }
                    ) { }

                    val backgroundColor =
                        if (isInPast) MaterialTheme.colors.surface else MaterialTheme.colors.background
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
