package co.touchlab.droidcon.android.ui.sessions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.touchlab.droidcon.android.ui.sessions.components.SessionCard
import co.touchlab.droidcon.android.ui.sessions.components.SessionView
import co.touchlab.droidcon.android.ui.theme.Colors
import co.touchlab.droidcon.android.ui.theme.Dimensions
import co.touchlab.droidcon.android.viewModel.sessions.SessionViewModel
import co.touchlab.droidcon.android.viewModel.sessions.SessionsBlockViewModel
import co.touchlab.droidcon.ui.theme.Colors

@Composable
fun SessionBlock(
    sessionsBlock: SessionsBlockViewModel,
    attendingOnly: Boolean,
    sessionTapped: (SessionViewModel) -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = sessionsBlock.time,
            modifier = Modifier
                .width(100.dp)
                .padding(Dimensions.Padding.half),
            textAlign = TextAlign.End,
        )

        val hasEnded by sessionsBlock.hasEnded.collectAsState(initial = false)
        val backgroundColor = if (hasEnded) MaterialTheme.colors.surface else MaterialTheme.colors.background
        val border = if (MaterialTheme.colors.isLight || hasEnded) null else BorderStroke(1.dp, MaterialTheme.colors.surface)

        SessionCard(
            backgroundColor = backgroundColor,
            border = border,
        ) {
            sessionsBlock.sessions.forEach { session ->
                SessionView(
                    title = session.title,
                    speakers = session.speakers,
                    onClick = { sessionTapped(session) },
                    enabled = !session.isServiceSession,
                    badgeColor = session.badgeColor(
                        attendingOnly = attendingOnly,
                        hasEnded = hasEnded
                    )
                )
                Divider()
            }
        }
    }
}

private fun SessionViewModel.badgeColor(attendingOnly: Boolean, hasEnded: Boolean) = when {
    attendingOnly || !isAttending -> Color.Transparent
    hasEnded -> Colors.grey
    isColliding -> Colors.orange
    else -> Colors.skyBlue
}
