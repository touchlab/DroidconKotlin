package co.touchlab.droidcon.ui.settings

import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import co.touchlab.droidcon.ui.icons.Aod
import co.touchlab.droidcon.viewmodel.settings.SettingsComponent
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState

@Composable
internal actual fun PlatformSpecificSettingsView(component: SettingsComponent) {
    val model by component.model.subscribeAsState()

    IconTextSwitchRow(
        text = "Use compose for iOS",
        image = Icons.Default.Aod,
        isChecked = model.useComposeForIos,
        onCheckedChange = component::setUseComposeForIos,
    )

    Divider()
}
