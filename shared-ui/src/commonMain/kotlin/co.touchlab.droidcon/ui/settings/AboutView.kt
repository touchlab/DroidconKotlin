package co.touchlab.droidcon.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
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

            val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
            val sizingModifier = when {
                windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND) ->
                    Modifier.sizeIn(maxHeight = 150.dp)
                else -> Modifier.fillMaxWidth()
            }

            LocalImage(
                imageResourceName = viewModel.icon,
                modifier = sizingModifier
                    .padding(
                        end = Dimensions.Padding.double,
                        top = Dimensions.Padding.default,
                        bottom = Dimensions.Padding.default,
                    ),
                contentScale = ContentScale.Fit
            )
        }
    }
}
