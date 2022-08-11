package co.touchlab.droidcon.ios.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import co.touchlab.droidcon.composite.Url
import co.touchlab.droidcon.ios.NavigationController
import co.touchlab.droidcon.ios.NavigationStack
import co.touchlab.droidcon.ios.viewmodel.session.SpeakerListItemViewModel
import co.touchlab.droidcon.ios.viewmodel.sponsor.SponsorDetailViewModel
import com.seiko.imageloader.ImageLoaderBuilder
import com.seiko.imageloader.LocalImageLoader
import com.seiko.imageloader.rememberAsyncImagePainter

@Composable
internal fun SponsorDetailView(viewModel: SponsorDetailViewModel) {
    NavigationStack(links = {
        NavigationLink(viewModel.observePresentedSpeakerDetail) {
            SpeakerDetailView(viewModel = it)
        }
    }) {
        Scaffold(topBar = {
            TopAppBar(
                title = { Text("Sponsor") },
                navigationIcon = {
                    IconButton(onClick = { NavigationController.root.handleBackPress() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                }
            )
        }) { paddingValues ->
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(scrollState),
            ) {
                HeaderView(name = viewModel.name, groupTitle = viewModel.groupName, imageUrl = viewModel.imageUrl)

                viewModel.abstract?.let {
                    DescriptionView(description = it)
                }

                val representatives by viewModel.observeRepresentatives.observeAsState()
                representatives.forEach {
                    RepresentativeInfoView(profile = it)
                }
            }
        }
    }
}

@Composable
private fun HeaderView(name: String, groupTitle: String, imageUrl: Url?) {
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 160.dp)
            .background(color = MaterialTheme.colors.primary),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.h5,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = Color.White,
                modifier = Modifier.padding(
                    start = 32.dp,
                    top = 16.dp,
                ),
            )
            Text(
                text = groupTitle,
                color = Color.White,
                modifier = Modifier.padding(
                    start = 32.dp,
                    bottom = 16.dp,
                ),
            )
        }

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
                        .width(120.dp)
                        .padding(horizontal = 16.dp)
                        .clip(CircleShape)
                        .aspectRatio(1f),
                )
            }
        } else {
            Image(
                imageVector = Icons.Default.Person,
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(120.dp)
                    .padding(horizontal = 16.dp)
                    .clip(CircleShape)
                    .aspectRatio(1f),
            )
        }
    }
}

@Composable
private fun DescriptionView(description: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = Icons.Default.Description,
            contentDescription = null,
            modifier = Modifier
                .padding(8.dp)
                .width(64.dp),
        )
        Text(
            text = description,
            modifier = Modifier.padding(
                end = 16.dp,
                top = 8.dp,
                bottom = 8.dp,
            ),
        )
    }
}

@Composable
private fun RepresentativeInfoView(profile: SpeakerListItemViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { profile.selected() },
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            val imageUrl = profile.avatarUrl
            if (imageUrl == null) {
                Image(
                    imageVector = Icons.Default.Person,
                    contentDescription = profile.info,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(80.dp)
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                        .clip(CircleShape)
                        .aspectRatio(1f)
                        .background(MaterialTheme.colors.primary),
                )
            } else {
                CompositionLocalProvider(
                    LocalImageLoader provides ImageLoaderBuilder().build(),
                ) {
                    val resource = rememberAsyncImagePainter(
                        url = imageUrl.string,
                        imageLoader = LocalImageLoader.current,
                    )

                    Image(
                        painter = resource,
                        contentDescription = profile.info,
                        modifier = Modifier
                            .width(80.dp)
                            .padding(start = 16.dp, end = 16.dp, top = 8.dp)
                            .clip(CircleShape)
                            .aspectRatio(1f)
                            .background(MaterialTheme.colors.primary),
                    )
                }
            }

            Text(
                text = profile.info,
                color = Color.Gray,
                modifier = Modifier.padding(
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 8.dp,
                ),
            )
        }
        Text(
            text = profile.bio ?: "",
            modifier = Modifier.padding(
                start = 80.dp,
                end = 16.dp,
                bottom = 8.dp,
            ),
        )
    }
}
