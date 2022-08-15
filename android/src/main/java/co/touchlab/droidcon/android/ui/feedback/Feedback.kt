package co.touchlab.droidcon.android.ui.feedback

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
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import co.touchlab.droidcon.R
import co.touchlab.droidcon.android.ui.theme.Colors
import co.touchlab.droidcon.android.ui.theme.Dimensions
import co.touchlab.droidcon.android.viewModel.feedback.FeedbackViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Feedback(feedback: FeedbackViewModel) {
    // Dialog does not resize in response to changing views inside, it's a compose bug: https://issuetracker.google.com/issues/194911971
    // Worked around by adding usePlatformWidth = false, according to https://issuetracker.google.com/issues/221643630
    Dialog(
        onDismissRequest = feedback::skip,
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false, usePlatformDefaultWidth = false),
    ) {
        Card(modifier = Modifier.padding(horizontal = Dimensions.Padding.default)) {
            Column(
                modifier = Modifier.padding(Dimensions.Padding.default),
            ) {
                val title by feedback.title.collectAsState(initial = "")
                Text(
                    text = stringResource(id = R.string.feedback_title, title),
                    color = MaterialTheme.colors.onBackground,
                    style = MaterialTheme.typography.subtitle1,
                    overflow = TextOverflow.Ellipsis,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    val selected by feedback.selectedReaction.collectAsState()

                    feedback.reactions.forEach { reaction ->
                        Icon(
                            painter = painterResource(id = reaction.imageRes),
                            contentDescription = stringResource(id = reaction.descriptionRes),
                            modifier = Modifier
                                .size(80.dp)
                                .padding(Dimensions.Padding.default)
                                .clip(CircleShape)
                                .clickable { feedback.selectedReaction.value = reaction },
                            tint = if (selected == reaction) MaterialTheme.colors.secondary else Colors.grey,
                        )
                    }
                }

                val comment by feedback.comment.collectAsState()
                OutlinedTextField(
                    value = comment,
                    onValueChange = { feedback.comment.value = it },
                    placeholder = {
                        Text(text = stringResource(id = R.string.feedback_opinion_placeholder), style = MaterialTheme.typography.body1)
                    },
                    textStyle = MaterialTheme.typography.body1,
                    modifier = Modifier
                        .height(160.dp)
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        cursorColor = MaterialTheme.colors.secondary,
                        focusedBorderColor = MaterialTheme.colors.secondary,
                    ),
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End,
                ) {
                    val isSubmitDisabled by feedback.isSubmitDisabled.collectAsState(initial = true)
                    TextButton(onClick = feedback::submit, enabled = !isSubmitDisabled) {
                        Text(
                            text = stringResource(id = R.string.feedback_submit).uppercase(),
                            color = if (isSubmitDisabled) MaterialTheme.colors.onSurface else MaterialTheme.colors.secondary,
                        )
                    }
                    if (feedback.showCloseAndDisableOption) {
                        TextButton(onClick = feedback::closeAndDisable) {
                            Text(
                                text = stringResource(id = R.string.feedback_close_and_disable).uppercase(),
                                color = MaterialTheme.colors.secondary,
                            )
                        }
                    }
                    TextButton(onClick = feedback::skip) {
                        Text(text = stringResource(id = R.string.feedback_skip).uppercase(), color = MaterialTheme.colors.secondary)
                    }
                }
            }
        }
    }
}
