package co.touchlab.droidcon.ui.settings

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import co.touchlab.droidcon.Constants
import co.touchlab.droidcon.viewmodel.settings.SettingsViewModel
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

@Composable
internal actual fun PlatformSpecificSettingsView(viewModel: SettingsViewModel) {
    // No platform-specific settings needed for iOS anymore
}

@Composable
internal actual fun PlatformSwitchApp() {
    Button(
        onClick = {
            openSisterApp()
        },
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
