package co.touchlab.droidcon.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import co.touchlab.droidcon.ui.icons.Info
import co.touchlab.droidcon.ui.theme.Dimensions
import co.touchlab.droidcon.ui.util.LocalImage
import co.touchlab.droidcon.ui.util.WebLinkText
import co.touchlab.droidcon.ui.util.observeAsState
import co.touchlab.droidcon.viewmodel.settings.AboutItemViewModel
import co.touchlab.droidcon.viewmodel.settings.AboutViewModel

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

            LocalImage(
                imageResourceName = viewModel.icon,
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
