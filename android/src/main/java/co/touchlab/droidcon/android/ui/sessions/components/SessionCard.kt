package co.touchlab.droidcon.android.ui.sessions.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.touchlab.droidcon.android.ui.theme.Colors

@Composable
fun SessionCard(
    backgroundColor: Color,
    border: BorderStroke?,
    content: @Composable () -> Unit,
) = Card(
    modifier = Modifier.fillMaxWidth(),
    backgroundColor = backgroundColor,
    border = border,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        content()
    }
}

@Preview
@Composable
fun SessionCardPreview() {
    SessionCard(
        backgroundColor = Colors.lightGrey220,
        border = BorderStroke(1.dp, MaterialTheme.colors.surface),
        content = {
            SessionView(
                title = "Creating Compound Views with Compose",
                speakers = "Kyle R.",
                onClick = { /* action*/ },
                enabled = true,
                badgeColor = Colors.skyBlue,
            )
        }
    )
}
