package co.touchlab.droidcon.ui

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SentimentNeutral
import androidx.compose.material.icons.filled.SentimentVeryDissatisfied
import androidx.compose.material.icons.filled.SentimentVerySatisfied
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import co.touchlab.droidcon.ui.theme.Dimensions
import co.touchlab.droidcon.ui.util.Dialog
import co.touchlab.droidcon.ui.util.observeAsState
import co.touchlab.droidcon.viewmodel.FeedbackDialogViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FeedbackDialog(feedback: FeedbackDialogViewModel) {
    Dialog(dismiss = feedback::skipTapped) {
        Card(
            modifier = Modifier.padding(Dimensions.Padding.double),
        ) {
            Column(
                modifier = Modifier
                    .padding(Dimensions.Padding.default)
                    .verticalScroll(rememberScrollState()),
            ) {
                Text(
                    text = "What did you think of ${feedback.sessionTitle}",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleMedium,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    val selected by feedback.observeRating.observeAsState()

                    FeedbackDialogViewModel.Rating.entries.forEach { rating ->
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
                            tint = if (selected == rating) MaterialTheme.colorScheme.primary else Color.Gray,
                        )
                    }
                }

                val comment by feedback.observeComment.observeAsState()
                OutlinedTextField(
                    value = comment,
                    onValueChange = { feedback.comment = it },
                    placeholder = {
                        Text(
                            text = "(Optional) Suggest improvement",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    },
                    textStyle = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .height(160.dp)
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
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
                            color = if (isSubmitDisabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.primary,
                        )
                    }
                    TextButton(onClick = feedback::closeAndDisableTapped) {
                        Text(
                            text = "CLOSE AND DISABLE FEEDBACK",
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                    TextButton(onClick = feedback::skipTapped) {
                        Text(text = "SKIP FEEDBACK", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}
