package co.touchlab.droidcon.android.ui.settings

import android.util.Patterns
import androidx.annotation.DrawableRes
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import co.touchlab.droidcon.R
import co.touchlab.droidcon.android.ui.theme.Dimensions
import co.touchlab.droidcon.android.ui.theme.Hyperlink
import co.touchlab.droidcon.android.ui.theme.Toolbar
import co.touchlab.droidcon.android.ui.theme.WebLinkText
import co.touchlab.droidcon.application.repository.AboutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class AboutInfo(val title: String, val detail: String, val webLinks: List<WebLink>, @DrawableRes val imageRes: Int?)
data class WebLink(val range: IntRange, val link: String)

val infoList: List<AboutInfo> = listOf(
    AboutInfo(
        title = "About Touchlab",
        detail = "This is some long text about Touchlab.\n\nhttps://touchlab.com",
        webLinks = listOf(WebLink(IntRange(40, 59), "https://touchlab.com")),
        imageRes = R.drawable.about_touchlab,
    ),
    AboutInfo(
        title = "Droidcon App",
        detail = "This is some long text about Droidcon App.",
        webLinks = emptyList(),
        imageRes = R.drawable.about_kotlin,
    ),
    AboutInfo(
        title = "Droidcon",
        detail = "This is some long text about Droidcon.",
        webLinks = emptyList(),
        imageRes = R.drawable.about_droidcon,
    ),
)

class AboutViewModel: ViewModel(), KoinComponent {

    // AboutItemVM
    val items = MutableStateFlow<List<AboutInfo>>(emptyList())

    private val aboutRepository by inject<AboutRepository>()

    private val scope = viewModelScope
    private val urlRegex = Patterns.WEB_URL.toRegex()

    init {
        scope.launch {
            aboutRepository.getAboutItems()
                .map {
                    it.detail
                    val links = urlRegex.findAll(it.detail).map { WebLink(it.range, it.value) }
                    AboutInfo(it.title, it.detail, links, null)
                }
        }
    }
}

@Composable
fun About(navController: NavHostController) {
    Scaffold(topBar = { Toolbar(titleRes = R.string.settings_about_title, navController = navController, showBackButton = true) }) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            infoList.forEach { aboutInfo ->
                item { Section(aboutInfo = aboutInfo) }
            }
        }
    }
}

@Composable
private fun Section(aboutInfo: AboutInfo) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Icon(
            modifier = Modifier.padding(horizontal = Dimensions.Padding.double, vertical = Dimensions.Padding.default),
            painter = painterResource(id = R.drawable.menu_info),
            contentDescription = aboutInfo.title,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = aboutInfo.title,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(
                    top = Dimensions.Padding.default,
                    bottom = Dimensions.Padding.default,
                    end = Dimensions.Padding.default,
                ),
            )

            WebLinkText(
                text = aboutInfo.detail,
                links = aboutInfo.webLink?.let { listOf(Hyperlink(it.range, it.link)) } ?: emptyList(),
                modifier = Modifier.padding(end = Dimensions.Padding.default),
            )
            if (aboutInfo.imageRes != null) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = Dimensions.Padding.double,
                            top = Dimensions.Padding.default,
                            bottom = Dimensions.Padding.default),
                    painter = painterResource(id = aboutInfo.imageRes),
                    contentDescription = aboutInfo.title,
                    contentScale = ContentScale.FillWidth,
                )
            }
        }
    }
}