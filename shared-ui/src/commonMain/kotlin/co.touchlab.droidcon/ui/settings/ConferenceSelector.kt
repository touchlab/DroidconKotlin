package co.touchlab.droidcon.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.touchlab.droidcon.domain.entity.Conference
import co.touchlab.droidcon.ui.theme.Dimensions
import androidx.compose.runtime.collectAsState
import co.touchlab.droidcon.viewmodel.settings.SettingsViewModel

@Composable
internal fun ConferenceSelectorRow(viewModel: SettingsViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val conferences by viewModel.allConferences.collectAsState<List<Conference>>()
    val selectedConference by viewModel.selectedConference.collectAsState<Conference?>()

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                modifier = Modifier.padding(Dimensions.Padding.default),
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Conference",
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 12.dp),
            ) {
                Text(text = "Conference")
                selectedConference?.let {
                    Text(
                        text = it.name + " (Selected)",
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Select",
                modifier = Modifier.padding(end = 16.dp),
            )
        }

        ConferenceDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            conferences = conferences,
            selectedConference = selectedConference,
            onConferenceSelected = { conference ->
                viewModel.selectConference(conference.id)
                expanded = false
            },
        )

        HorizontalDivider()
    }
}

@Composable
private fun ConferenceDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    conferences: List<Conference>,
    selectedConference: Conference?,
    onConferenceSelected: (Conference) -> Unit,
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = Modifier.fillMaxWidth(0.9f),
    ) {
        Text(
            text = "Select Conference",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )

        HorizontalDivider()

        conferences.forEach { conference ->
            val isSelected = conference.id == selectedConference?.id
            DropdownMenuItem(
                text = {
                    Text(
                        text = if (isSelected) {
                            "${conference.name} (Selected)"
                        } else {
                            conference.name
                        },
                    )
                },
                onClick = {
                    // Explicitly call the selection function to ensure it runs
                    onConferenceSelected(conference)
                },
                leadingIcon = {
                    RadioButton(
                        selected = isSelected,
                        onClick = {
                            // Handle click on the radio button separately to ensure it runs
                            onConferenceSelected(conference)
                        },
                    )
                },
            )
        }
    }
}
