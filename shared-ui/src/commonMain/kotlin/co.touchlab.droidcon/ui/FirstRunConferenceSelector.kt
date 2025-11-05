package co.touchlab.droidcon.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import co.touchlab.droidcon.domain.entity.Conference

@Composable
fun FirstRunConferenceSelector(
    conferences: List<Conference>,
    onConferenceSelected: (Conference) -> Unit,
    onDismiss: () -> Unit,
    selectedConference: Conference? = null,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Text(
                    text = "Welcome to Droidcon!",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 16.dp),
                )

                Text(
                    text = "Please select a conference to get started:",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp),
                )

                LazyColumn {
                    items(conferences) { conference ->
                        val isSelected = conference.id == selectedConference?.id
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // Explicitly call selection function
                                    onConferenceSelected(conference)
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    // Handle radio button click separately
                                    onConferenceSelected(conference)
                                },
                            )
                            Text(
                                text = if (isSelected) {
                                    "${conference.name} (Selected)"
                                } else {
                                    conference.name
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 8.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}
