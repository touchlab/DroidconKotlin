package co.touchlab.droidcon.android.ui.sessions.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BadgeBox
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import co.touchlab.droidcon.android.ui.theme.Colors
import co.touchlab.droidcon.android.ui.theme.Dimensions

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SessionView(
    title: String,
    speakers: String,
    onClick: () -> Unit,
    enabled: Boolean,
    badgeColor: Color,
) = Row(verticalAlignment = Alignment.CenterVertically) {
    BadgeBox(
        modifier = Modifier.padding(Dimensions.Padding.default),
        backgroundColor = badgeColor,
    ) { }
    Column(
        modifier = Modifier.clickable(
            enabled = enabled,
            onClick = onClick
        )
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(Dimensions.Padding.half),
        )
        Text(
            text = speakers,
            modifier = Modifier.padding(
                start = Dimensions.Padding.half,
                end = Dimensions.Padding.half,
                bottom = Dimensions.Padding.half,
            ),
        )
    }
}

@Preview
@Composable
fun SessionViewPreview() {
    SessionView(
        title = "Utilizing Composable Previews",
        speakers = "Joshua R.",
        onClick = { /* action */ },
        enabled = true,
        badgeColor = Colors.skyBlue,
    )
}
