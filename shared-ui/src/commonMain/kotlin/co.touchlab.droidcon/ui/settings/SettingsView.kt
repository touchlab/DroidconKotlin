package co.touchlab.droidcon.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import co.touchlab.droidcon.ui.theme.Dimensions
import co.touchlab.droidcon.ui.util.LocalImage
import co.touchlab.droidcon.ui.util.observeAsState
import co.touchlab.droidcon.viewmodel.settings.SettingsViewModel
import org.brightify.hyperdrive.multiplatformx.property.MutableObservableProperty

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsView(
    viewModel: SettingsViewModel,
) {
    val isAuthenticated by viewModel.observeIsAuthenticated.observeAsState()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                scrollBehavior = scrollBehavior
            )
        },
    ) { paddingValues ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(top = paddingValues.calculateTopPadding())
                .fillMaxHeight()
                .verticalScroll(scrollState)
        ) {
            IconTextSwitchRow(
                text = "Enable feedback",
                image = Icons.Default.MailOutline,
                checked = viewModel.observeIsFeedbackEnabled,
            )

            Divider()

            IconTextSwitchRow(
                text = "Enable reminders",
                image = Icons.Default.Notifications,
                checked = viewModel.observeIsRemindersEnabled,
            )

            Divider()

            Button(
                onClick = {
                    if (isAuthenticated) viewModel.signOut() else viewModel.signIn()
                },
                contentPadding = PaddingValues(),
            ) {
                if (isAuthenticated) {
                    Text("Sign Out", modifier = Modifier.padding(horizontal = 20.dp))
                } else {
                    LocalImage(
                        imageResourceName = "continue_with_google_rd",
                        modifier = Modifier
                    )
                }
            }

            Divider()

            PlatformSpecificSettingsView(viewModel = viewModel)

            AboutView(viewModel.about)
        }
    }
}

@Composable
internal fun IconTextSwitchRow(
    text: String,
    image: ImageVector,
    checked: MutableObservableProperty<Boolean>
) {
    val isChecked by checked.observeAsState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { checked.value = !checked.value },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.padding(Dimensions.Padding.default),
            imageVector = image,
            contentDescription = text,
        )
        Text(
            modifier = Modifier.weight(1f),
            text = text,
        )
        Switch(
            modifier = Modifier.padding(vertical = Dimensions.Padding.half, horizontal = 24.dp),
            checked = isChecked,
            onCheckedChange = { checked.value = it },
        )
    }
}
