package co.touchlab.droidcon.ui.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Aod
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import co.touchlab.droidcon.Constants
import co.touchlab.droidcon.viewmodel.settings.SettingsViewModel
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

@Composable
internal actual fun PlatformSpecificSettingsView(viewModel: SettingsViewModel) {
    IconTextSwitchRow(
        text = "Use compose for iOS",
        image = Icons.Default.Aod,
        checked = viewModel.observeUseCompose,
    )

    HorizontalDivider()
}

@Composable
internal actual fun PlatformSwitchApp() {
    Button(
        onClick = {
            openSisterApp()
        }
    ) {
        Text("Open ${Constants.SisterApp.name}")
    }
}

fun openSisterApp() {
    val url = NSURL(string = Constants.SisterApp.iosUrlString)
    if (UIApplication.sharedApplication.canOpenURL(url)) {
        UIApplication.sharedApplication.openURL(url)
    } else {
        val appStoreUrl = NSURL(string = Constants.SisterApp.iosAppStoreUrlString)
        UIApplication.sharedApplication.openURL(appStoreUrl)
    }
}
