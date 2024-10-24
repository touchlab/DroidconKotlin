package co.touchlab.droidcon.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import co.touchlab.droidcon.Constants
import co.touchlab.droidcon.viewmodel.settings.SettingsViewModel

@Composable
internal actual fun PlatformSpecificSettingsView(viewModel: SettingsViewModel) {
    // Add settings specific for Android here.
}

@Composable
internal actual fun PlatformSwitchApp() {
    val context = LocalContext.current
    Button(
        onClick = {
            val intent = context.packageManager.getLaunchIntentForPackage(Constants.SisterApp.androidPackageName)
            if (intent != null) {
                context.startActivity(intent)
            } else {
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=${Constants.SisterApp.androidPackageName}")
                    )
                )
            }
        }
    ) {
        Text("Open ${Constants.SisterApp.name}")
    }
}
