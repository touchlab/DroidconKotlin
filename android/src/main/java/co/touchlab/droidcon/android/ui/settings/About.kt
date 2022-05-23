package co.touchlab.droidcon.android.ui.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import co.touchlab.droidcon.R
import co.touchlab.droidcon.android.ui.theme.Dimensions
import co.touchlab.droidcon.android.ui.theme.Toolbar
import co.touchlab.droidcon.android.ui.theme.WebLinkText
import co.touchlab.droidcon.android.viewModel.settings.AboutItemViewModel
import co.touchlab.droidcon.android.viewModel.settings.AboutViewModel

@Composable
fun About() {
    val about: AboutViewModel = viewModel()
    val items by about.items.collectAsState()
    items.forEach { aboutItem ->
        item { AboutSection(aboutItem = aboutItem) }
    }
}

@Composable
fun AboutSection(aboutItem: AboutItemViewModel) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Icon(
            modifier = Modifier.padding(horizontal = Dimensions.Padding.double, vertical = Dimensions.Padding.default),
            painter = painterResource(id = R.drawable.menu_info),
            contentDescription = aboutItem.title,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = aboutItem.title,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(
                    top = Dimensions.Padding.default,
                    bottom = Dimensions.Padding.default,
                    end = Dimensions.Padding.default,
                ),
            )

            WebLinkText(
                text = aboutItem.detail,
                links = aboutItem.webLinks,
                modifier = Modifier.padding(end = Dimensions.Padding.default),
            )

            val context = LocalContext.current
            val imageRes = context.resources.getIdentifier(aboutItem.image.name, "drawable", context.packageName).takeIf { it != 0 }
            if (imageRes != null) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = Dimensions.Padding.double,
                            top = Dimensions.Padding.default,
                            bottom = Dimensions.Padding.default),
                    painter = painterResource(id = imageRes),
                    contentDescription = aboutItem.title,
                    contentScale = ContentScale.FillWidth,
                )
            }
        }
    }
}