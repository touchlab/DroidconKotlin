package co.touchlab.droidcon.android.ui.settings

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.navigation.NavHostController
import co.touchlab.droidcon.R
import co.touchlab.droidcon.android.ui.theme.Colors
import co.touchlab.droidcon.android.ui.theme.Dimensions
import co.touchlab.droidcon.android.ui.theme.Toolbar

data class AboutInfo(val title: String, val detail: String, val webLink: WebLink?, @DrawableRes val imageRes: Int?)
data class WebLink(val range: IntRange, val link: String)

val infoList: List<AboutInfo> = listOf(
    AboutInfo(
        title = "About Touchlab",
        detail = "This is some long text about Touchlab.\n\nhttps://touchlab.com",
        webLink = WebLink(IntRange(40, 59), "https://touchlab.com"),
        imageRes = R.drawable.about_touchlab,
    ),
    AboutInfo(
        title = "Droidcon App",
        detail = "This is some long text about Droidcon App.",
        webLink = null,
        imageRes = R.drawable.about_kotlin,
    ),
    AboutInfo(
        title = "Droidcon",
        detail = "This is some long text about Droidcon.",
        webLink = null,
        imageRes = R.drawable.about_droidcon,
    ),
)

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

            val detail = buildAnnotatedString {
                val linkStyle = SpanStyle(
                    color = Colors.darkBlue,
                    textDecoration = TextDecoration.Underline,
                )
                val normalStyle = SpanStyle()

                aboutInfo.webLink?.let { webLink ->
                    withStyle(normalStyle) {
                        append(aboutInfo.detail.substring(0, webLink.range.first - 1))
                    }
                    withStyle(linkStyle) {
                        append(aboutInfo.detail.substring(webLink.range))
                        addStringAnnotation(
                            tag = "URL",
                            annotation = webLink.link,
                            start = webLink.range.first,
                            end = webLink.range.last,
                        )
                    }
                    withStyle(style = normalStyle) {
                        append(aboutInfo.detail.substring(webLink.range.last + 1))
                    }
                } ?: withStyle(normalStyle) {
                    append(aboutInfo.detail)
                }
            }

            var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
            val uriHandler = LocalUriHandler.current
            Text(
                text = detail,
                modifier = Modifier
                    .padding(end = Dimensions.Padding.default)
                    .pointerInput("key") {
                        detectTapGestures { offsetPosition ->
                            textLayoutResult?.let {
                                val position = it.getOffsetForPosition(offsetPosition)
                                detail
                                    .getStringAnnotations(position, position)
                                    .firstOrNull()
                                    ?.let { result ->
                                        uriHandler.openUri(result.item)
                                    }
                            }
                        }
                    },
                onTextLayout = { textLayoutResult = it },
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