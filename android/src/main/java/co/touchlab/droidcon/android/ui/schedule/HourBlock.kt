package co.touchlab.droidcon.android.ui.schedule

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import co.touchlab.droidcon.android.ui.theme.Colors
import co.touchlab.droidcon.android.ui.theme.Dimensions

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HourBlock(hourBlock: HourBlock) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = hourBlock.time,
            modifier = Modifier
                .weight(1f)
                .padding(Dimensions.Padding.half),
            textAlign = TextAlign.End,
        )
        Card(
            backgroundColor = Colors.lightGrey220,
            modifier = Modifier.weight(3f),
            onClick = { /* TODO: Open HourBlock detail. */ },
        ) {
            Text(text = hourBlock.description, modifier = Modifier.padding(Dimensions.Padding.half))
        }
    }
}