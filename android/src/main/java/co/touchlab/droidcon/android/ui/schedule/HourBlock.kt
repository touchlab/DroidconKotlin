package co.touchlab.droidcon.android.ui.schedule

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BadgeBox
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import co.touchlab.droidcon.android.ui.theme.Colors
import co.touchlab.droidcon.android.ui.theme.Dimensions

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HourBlock(hourBlock: HourBlock, hourBlockTapped: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth()) {
        val hasEnded = false
        Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
            Text(
                text = hourBlock.time,
                modifier = Modifier.padding(Dimensions.Padding.half),
                textAlign = TextAlign.End,
            )
            val isInAgenda = true
            if (isInAgenda) {
                val badgeColor = if (hasEnded) Colors.grey else Colors.skyBlue
                BadgeBox(
                    modifier = Modifier.padding(end = Dimensions.Padding.default, bottom = Dimensions.Padding.default),
                    backgroundColor = badgeColor,
                ) {}
            }
        }
        val backgroundColor = if (hasEnded) Colors.lightGrey222 else Color.White
        Card(
            backgroundColor = backgroundColor,
            modifier = Modifier.weight(3f),
            onClick = hourBlockTapped,
            elevation = 2.dp,
        ) {
            Text(text = hourBlock.description, modifier = Modifier.padding(Dimensions.Padding.half))
        }
    }
}