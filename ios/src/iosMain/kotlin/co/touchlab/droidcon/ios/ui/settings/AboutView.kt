package co.touchlab.droidcon.ios.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import co.touchlab.droidcon.ios.ui.theme.Dimensions
import co.touchlab.droidcon.ios.ui.util.UIKitImage
import co.touchlab.droidcon.ios.ui.util.WebLinkText
import co.touchlab.droidcon.ios.ui.util.observeAsState
import co.touchlab.droidcon.ios.viewmodel.settings.AboutItemViewModel
import co.touchlab.droidcon.ios.viewmodel.settings.AboutViewModel

@Composable
internal fun AboutView(viewModel: AboutViewModel) {
    val items by viewModel.observeItemViewModels.observeAsState()
    items.forEach { aboutItem ->
        AboutItemView(aboutItem)
    }
}

@Composable
private fun AboutItemView(viewModel: AboutItemViewModel) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Icon(
            modifier = Modifier.padding(Dimensions.Padding.default),
            imageVector = Icons.Default.Info,
            contentDescription = viewModel.title,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = viewModel.title,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(
                    top = Dimensions.Padding.default,
                    bottom = Dimensions.Padding.default,
                    end = Dimensions.Padding.default,
                ),
            )

            WebLinkText(
                text = viewModel.detail,
                links = viewModel.webLinks,
                modifier = Modifier.padding(end = Dimensions.Padding.default),
            )

            UIKitImage(
                imageName = viewModel.icon,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        end = Dimensions.Padding.double,
                        top = Dimensions.Padding.default,
                        bottom = Dimensions.Padding.default,
                    ),
            )
        }
    }
}
