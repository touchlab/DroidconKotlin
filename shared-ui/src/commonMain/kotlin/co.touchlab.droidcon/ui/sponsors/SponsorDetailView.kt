package co.touchlab.droidcon.ui.sponsors

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import co.touchlab.droidcon.composite.Url
import co.touchlab.droidcon.ui.session.SpeakerDetailView
import co.touchlab.droidcon.ui.theme.Dimensions
import co.touchlab.droidcon.ui.util.DcAsyncImage
import androidx.compose.runtime.collectAsState
import co.touchlab.droidcon.util.NavigationController
import co.touchlab.droidcon.util.NavigationStack
import co.touchlab.droidcon.viewmodel.session.SpeakerDetailViewModel
import co.touchlab.droidcon.viewmodel.session.SpeakerListItemViewModel
import co.touchlab.droidcon.viewmodel.sponsor.SponsorDetailViewModel

private const val LOG_TAG = "SponsorDetailView"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SponsorDetailView(viewModel: SponsorDetailViewModel) {
    NavigationStack(
        key = viewModel,
        links = {
            navigationLink<SpeakerDetailViewModel>(
                viewModel.observePresentedSpeakerDetail,
                reset = { viewModel.presentedSpeakerDetail = null },
            ) {
                SpeakerDetailView(viewModel = it)
            }
        },
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Sponsor") },
                    navigationIcon = {
                        IconButton(onClick = { NavigationController.root.handleBackPress() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = "Back",
                            )
                        }
                    },
                )
            },
        ) { paddingValues ->
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(scrollState),
            ) {
                HeaderView(
                    name = viewModel.name,
                    groupTitle = viewModel.groupName,
                    imageUrl = viewModel.imageUrl,
                )

                viewModel.abstract?.let {
                    DescriptionView(description = it)
                }

                val representatives by viewModel.observeRepresentatives.collectAsState()
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
            .background(color = MaterialTheme.colorScheme.primary),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = Color.White,
                modifier = Modifier.padding(
                    start = Dimensions.Padding.double,
                    top = Dimensions.Padding.default,
                ),
            )
            Text(
                text = groupTitle,
                color = Color.White,
                modifier = Modifier.padding(
                    start = Dimensions.Padding.double,
                    bottom = Dimensions.Padding.default,
                ),
            )
        }

        if (imageUrl != null) {
            DcAsyncImage(
                logTag = LOG_TAG,
                model = imageUrl.string,
                modifier = Modifier
                    .width(120.dp)
                    .padding(horizontal = Dimensions.Padding.default)
                    .clip(CircleShape)
                    .aspectRatio(1f),
                contentDescription = name,
            )
        } else {
            Image(
                imageVector = Icons.Default.Person,
                contentDescription = name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(120.dp)
                    .padding(horizontal = Dimensions.Padding.default)
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
                .padding(Dimensions.Padding.half)
                .width(64.dp),
        )
        Text(
            text = description,
            modifier = Modifier.padding(
                end = Dimensions.Padding.default,
                top = Dimensions.Padding.half,
                bottom = Dimensions.Padding.half,
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
                        .padding(
                            start = Dimensions.Padding.default,
                            end = Dimensions.Padding.default,
                            top = Dimensions.Padding.half,
                        )
                        .clip(CircleShape)
                        .aspectRatio(1f)
                        .background(MaterialTheme.colorScheme.primary),
                )
            } else {
                DcAsyncImage(
                    logTag = LOG_TAG,
                    model = imageUrl.string,
                    contentDescription = profile.info,
                    modifier = Modifier
                        .width(80.dp)
                        .padding(
                            start = Dimensions.Padding.default,
                            end = Dimensions.Padding.default,
                            top = Dimensions.Padding.half,
                        )
                        .clip(CircleShape)
                        .aspectRatio(1f)
                        .background(MaterialTheme.colorScheme.primary),
                )
            }

            Text(
                text = profile.info,
                color = Color.Gray,
                modifier = Modifier.padding(
                    end = Dimensions.Padding.default,
                    top = Dimensions.Padding.half,
                    bottom = Dimensions.Padding.half,
                ),
            )
        }
        Text(
            text = profile.bio ?: "",
            modifier = Modifier.padding(
                start = 80.dp,
                end = Dimensions.Padding.default,
                bottom = Dimensions.Padding.half,
            ),
        )
    }
}
