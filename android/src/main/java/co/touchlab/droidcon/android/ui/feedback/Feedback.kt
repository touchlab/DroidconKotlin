package co.touchlab.droidcon.android.ui.feedback

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import co.touchlab.droidcon.R
import co.touchlab.droidcon.android.ui.theme.Colors
import co.touchlab.droidcon.android.ui.theme.Dimensions

sealed class Reaction(@StringRes val descriptionRes: Int, @DrawableRes val imageRes: Int) {
    object Bad: Reaction(R.string.feedback_reaction_bad_description, R.drawable.baseline_sentiment_very_dissatisfied_24)
    object Normal: Reaction(R.string.feedback_reaction_normal_description, R.drawable.baseline_sentiment_satisfied_24)
    object Good: Reaction(R.string.feedback_reaction_good_description, R.drawable.baseline_sentiment_satisfied_alt_24)
}

@Composable
fun Feedback() {
    Dialog(onDismissRequest = { /*TODO*/ }) {
        Card {
            Column(modifier = Modifier.padding(Dimensions.Padding.default)) {
                Text(
                    text = stringResource(id = R.string.feedback_title, "Droidcon 2021"),
                    color = Colors.darkGrey,
                    style = MaterialTheme.typography.h6,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    var selected by remember { mutableStateOf<Reaction?>(null) }

                    listOf(Reaction.Bad, Reaction.Normal, Reaction.Good).forEach { reaction ->
                        Icon(
                            painter = painterResource(id = reaction.imageRes),
                            contentDescription = stringResource(id = reaction.descriptionRes),
                            modifier = Modifier
                                .size(80.dp)
                                .padding(Dimensions.Padding.default)
                                .clip(CircleShape)
                                .clickable { selected = reaction },
                            tint = if (selected == reaction) Colors.teal else Colors.grey,
                        )
                    }
                }

                var opinion by remember { mutableStateOf("") }
                OutlinedTextField(
                    value = opinion,
                    onValueChange = { opinion = it },
                    placeholder = {
                        Text(text = stringResource(id = R.string.feedback_opinion_placeholder), style = MaterialTheme.typography.body1)
                    },
                    textStyle = MaterialTheme.typography.body1,
                    modifier = Modifier
                        .height(160.dp)
                        .fillMaxWidth(),
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End,
                ) {
                    TextButton(onClick = { /*TODO*/ }) {
                        Text(text = stringResource(id = R.string.feedback_submit).uppercase())
                    }
                    TextButton(onClick = { /*TODO*/ }) {
                        Text(text = stringResource(id = R.string.feedback_close_and_disable).uppercase())
                    }
                    TextButton(onClick = { /*TODO*/ }) {
                        Text(text = stringResource(id = R.string.feedback_skip).uppercase())
                    }
                }
            }
        }
    }
}