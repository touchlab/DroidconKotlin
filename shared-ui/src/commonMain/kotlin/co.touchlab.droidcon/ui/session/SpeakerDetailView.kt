package co.touchlab.droidcon.ui.session

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import co.touchlab.droidcon.composite.Url
import co.touchlab.droidcon.dto.WebLink
import co.touchlab.droidcon.ui.theme.Dimensions
import co.touchlab.droidcon.ui.util.DcAsyncImage
import co.touchlab.droidcon.ui.util.LocalImage
import co.touchlab.droidcon.ui.util.WebLinkText
import co.touchlab.droidcon.util.NavigationController
import co.touchlab.droidcon.viewmodel.session.SpeakerDetailViewModel

private const val LOG_TAG = "SpeakerDetailView"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SpeakerDetailView(viewModel: SpeakerDetailViewModel) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("Speaker") },
                navigationIcon = {
                    IconButton(onClick = { NavigationController.root.handleBackPress() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier.verticalScroll(scrollState).padding(paddingValues),
        ) {
            HeaderView(viewModel.name, viewModel.position ?: "", viewModel.avatarUrl)

            viewModel.socials.website?.let {
                SocialView(WebLink.fromUrl(it), Icons.Default.Language)
            }
            viewModel.socials.twitter?.let {
                SocialView(WebLink.fromUrl(it), "twitter")
            }
            viewModel.socials.linkedIn?.let {
                SocialView(WebLink.fromUrl(it), "linkedin")
            }

            HorizontalDivider()

            viewModel.bio?.let {
                BioView(it, viewModel.bioWebLinks)
            }
        }
    }
}

@Composable
private fun HeaderView(name: String, tagLine: String, imageUrl: Url?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (imageUrl != null) {
            DcAsyncImage(
                logTag = LOG_TAG,
                model = imageUrl.string,
                contentDescription = name,
                modifier = Modifier
                    .width(100.dp)
                    .padding(Dimensions.Padding.default)
                    .clip(CircleShape)
                    .aspectRatio(1f),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.headlineSmall,
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

@Composable
private fun SocialView(url: WebLink, iconName: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        LocalImage(
            imageResourceName = iconName,
            contentDescription = null,
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
private fun SocialView(url: WebLink, icon: ImageVector) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            modifier = Modifier
                .padding(Dimensions.Padding.default)
                .size(28.dp),
            contentDescription = null,
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
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = Dimensions.Padding.half),
        verticalAlignment = Alignment.Top,
    ) {
        Icon(
            imageVector = Icons.Default.Description,
            contentDescription = null,
            modifier = Modifier
                .padding(Dimensions.Padding.default)
                .size(28.dp),
            tint = MaterialTheme.colorScheme.onSurface,
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
