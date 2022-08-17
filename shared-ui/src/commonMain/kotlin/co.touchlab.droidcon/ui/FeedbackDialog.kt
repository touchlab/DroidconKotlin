package co.touchlab.droidcon.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import co.touchlab.droidcon.ui.icons.SentimentNeutral
import co.touchlab.droidcon.ui.icons.SentimentVeryDissatisfied
import co.touchlab.droidcon.ui.icons.SentimentVerySatisfied
import co.touchlab.droidcon.ui.theme.Dimensions
import co.touchlab.droidcon.ui.util.Dialog
import co.touchlab.droidcon.ui.util.observeAsState
import co.touchlab.droidcon.viewmodel.FeedbackDialogViewModel

@Composable
internal fun FeedbackDialog(feedback: FeedbackDialogViewModel) {
    Dialog(dismiss = feedback::skipTapped) {
        Card(modifier = Modifier.padding(Dimensions.Padding.double), backgroundColor = MaterialTheme.colors.background) {
            Column(
                modifier = Modifier
                    .padding(Dimensions.Padding.default)
                    .verticalScroll(rememberScrollState()),
            ) {
                Text(
                    text = "What did you think of ${feedback.sessionTitle}",
                    color = MaterialTheme.colors.onBackground,
                    style = MaterialTheme.typography.subtitle1,
                    overflow = TextOverflow.Ellipsis,
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    val selected by feedback.observeRating.observeAsState()

                    FeedbackDialogViewModel.Rating.values().forEach { rating ->
                        val image = when (rating) {
                            FeedbackDialogViewModel.Rating.Dissatisfied -> Icons.Default.SentimentVeryDissatisfied
                            FeedbackDialogViewModel.Rating.Normal -> Icons.Default.SentimentNeutral
                            FeedbackDialogViewModel.Rating.Satisfied -> Icons.Default.SentimentVerySatisfied
                        }
                        Icon(
                            imageVector = image,
                            modifier = Modifier
                                .size(80.dp)
                                .padding(Dimensions.Padding.default)
                                .clip(CircleShape)
                                .clickable { feedback.rating = rating },
                            contentDescription = rating.name,
                            tint = if (selected == rating) MaterialTheme.colors.primary else Color.Gray,
                        )
                    }
                }

                val comment by feedback.observeComment.observeAsState()
                OutlinedTextField(
                    value = comment,
                    onValueChange = { feedback.comment = it },
                    placeholder = {
                        Text(text = "(Optional) Suggest improvement", style = MaterialTheme.typography.body1)
                    },
                    textStyle = MaterialTheme.typography.body1,
                    modifier = Modifier
                        .height(160.dp)
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        cursorColor = MaterialTheme.colors.primary,
                        focusedBorderColor = MaterialTheme.colors.primary,
                    ),
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End,
                ) {
                    val isSubmitDisabled by feedback.observeIsSubmitDisabled.observeAsState()
                    TextButton(onClick = feedback::submitTapped, enabled = !isSubmitDisabled) {
                        Text(
                            text = "SUBMIT",
                            color = if (isSubmitDisabled) MaterialTheme.colors.onSurface else MaterialTheme.colors.primary,
                        )
                    }
                    TextButton(onClick = feedback::closeAndDisableTapped) {
                        Text(text = "CLOSE AND DISABLE FEEDBACK", color = MaterialTheme.colors.primary)
                    }
                    TextButton(onClick = feedback::skipTapped) {
                        Text(text = "SKIP FEEDBACK", color = MaterialTheme.colors.primary)
                    }
                }
            }
        }
    }
}
