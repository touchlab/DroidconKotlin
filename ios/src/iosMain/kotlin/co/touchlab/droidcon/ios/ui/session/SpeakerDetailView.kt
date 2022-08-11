package co.touchlab.droidcon.ios.ui.session

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import co.touchlab.droidcon.composite.Url
import co.touchlab.droidcon.dto.WebLink
import co.touchlab.droidcon.ios.NavigationController
import co.touchlab.droidcon.ios.ui.theme.Dimensions
import co.touchlab.droidcon.ios.ui.util.UIKitImage
import co.touchlab.droidcon.ios.ui.util.WebLinkText
import co.touchlab.droidcon.ios.viewmodel.session.SpeakerDetailViewModel
import com.seiko.imageloader.ImageLoaderBuilder
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.rememberAsyncImagePainter

@Composable
internal fun SpeakerDetailView(viewModel: SpeakerDetailViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Speaker") },
                navigationIcon = {
                    IconButton(onClick = { NavigationController.root.handleBackPress() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                }
            )
        },
    ) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier.verticalScroll(scrollState),
        ) {
            HeaderView(viewModel.name, viewModel.position ?: "", viewModel.avatarUrl)

            viewModel.socials.website?.let {
                SocialView(WebLink.fromUrl(it), "globe")
            }
            viewModel.socials.twitter?.let {
                SocialView(WebLink.fromUrl(it), "twitter")
            }
            viewModel.socials.linkedIn?.let {
                SocialView(WebLink.fromUrl(it), "linked-in")
            }

            Divider()

            viewModel.bio?.let {
                BioView(it, viewModel.bioWebLinks)
            }
        }
    }
}

@Composable
private fun HeaderView(name: String, tagLine: String, imageUrl: Url?) {
    Card(elevation = Dimensions.Padding.quarter,
        backgroundColor = Color.hsl(hue = 0f, saturation = 0f, lightness = 0.96f),
        shape = RectangleShape) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (imageUrl != null) {
                CompositionLocalProvider(
                    LocalImageLoader provides ImageLoaderBuilder().build(),
                ) {
                    val resource = rememberAsyncImagePainter(
                        url = imageUrl.string,
                        imageLoader = LocalImageLoader.current,
                    )

                    Image(
                        painter = resource,
                        contentDescription = name,
                        modifier = Modifier
                            .width(100.dp)
                            .padding(Dimensions.Padding.default)
                            .clip(CircleShape)
                            .aspectRatio(1f),
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.h5,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(
                        end = Dimensions.Padding.double,
                        top = Dimensions.Padding.default,
                    ),
                )
                Text(
                    text = tagLine,
                    modifier = Modifier.padding(
                        end = Dimensions.Padding.double,
                        bottom = Dimensions.Padding.default,
                    ),
                )
            }
        }
    }
}

@Composable
private fun SocialView(url: WebLink, iconName: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        UIKitImage(
            imageName = iconName,
            modifier = Modifier
                .padding(Dimensions.Padding.default)
                .size(28.dp),
        )
        WebLinkText(
            text = url.link,
            links = listOf(url),
            modifier = Modifier.padding(
                end = Dimensions.Padding.default,
                top = Dimensions.Padding.half,
                bottom = Dimensions.Padding.half,
            ),
        )
    }
}

@Composable
private fun BioView(bio: String, webLinks: List<WebLink>) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = Dimensions.Padding.half), verticalAlignment = Alignment.Top) {
        Image(
            imageVector = Icons.Default.Description,
            contentDescription = null,
            modifier = Modifier
                .padding(Dimensions.Padding.default)
                .size(28.dp),
        )
        WebLinkText(
            text = bio,
            links = webLinks,
            modifier = Modifier.padding(
                end = Dimensions.Padding.default,
                top = Dimensions.Padding.half,
                bottom = Dimensions.Padding.half,
            ),
        )
    }
}
