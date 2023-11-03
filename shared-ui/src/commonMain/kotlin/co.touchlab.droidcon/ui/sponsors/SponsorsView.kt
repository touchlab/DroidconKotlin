package co.touchlab.droidcon.ui.sponsors

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import co.touchlab.droidcon.ui.icons.DateRange
import co.touchlab.droidcon.ui.theme.Dimensions
import co.touchlab.droidcon.ui.util.RemoteImage
import co.touchlab.droidcon.ui.util.observeAsState
import co.touchlab.droidcon.util.NavigationStack
import co.touchlab.droidcon.viewmodel.sponsor.SponsorGroupViewModel
import co.touchlab.droidcon.viewmodel.sponsor.SponsorListViewModel
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SponsorsView(viewModel: SponsorListViewModel) {
    NavigationStack(
        key = viewModel,
        links = {
            NavigationLink(viewModel.observePresentedSponsorDetail) {
                SponsorDetailView(viewModel = it)
            }
        }
    ) {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = { Text("Sponsors") },
                    scrollBehavior = scrollBehavior
                )
            },
        ) { paddingValues ->
            val uriHandler = LocalUriHandler.current

            val sponsorGroups by viewModel.observeSponsorGroups.observeAsState()
            Column(modifier = Modifier.padding(top = paddingValues.calculateTopPadding())) {
                if (sponsorGroups.isEmpty()) {
                    EmptyView()
                } else {
                    LazyColumn(contentPadding = PaddingValues(vertical = Dimensions.Padding.quarter)) {
                        items(sponsorGroups) { sponsorGroup ->
                            SponsorGroupView(sponsorGroup)
                        }
                    }
                }
            }

            val presentedUrl by viewModel.observePresentedUrl.observeAsState()
            presentedUrl?.let {
                uriHandler.openUri(it.string)
                viewModel.presentedUrl = null
            }
        }
    }
}

@Composable
private fun SponsorGroupView(sponsorGroup: SponsorGroupViewModel) {
    Card(
        modifier = Modifier.padding(vertical = Dimensions.Padding.quarter, horizontal = Dimensions.Padding.half)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = sponsorGroup.title,
                modifier = Modifier.padding(
                    start = Dimensions.Padding.default,
                    end = Dimensions.Padding.default,
                    top = Dimensions.Padding.default,
                    bottom = Dimensions.Padding.quarter,
                ),
                style = MaterialTheme.typography.headlineLarge,
            )
            val columnCount = if (sponsorGroup.isProminent) 3 else 4

            val sponsors by sponsorGroup.observeSponsors.observeAsState()

            repeat(sponsors.size / columnCount + if (sponsors.size % columnCount == 0) 0 else 1) { rowIndex ->
                Row(modifier = Modifier.padding(horizontal = Dimensions.Padding.half)) {
                    val startIndex = rowIndex * columnCount
                    val endIndex = min(startIndex + columnCount, sponsors.size)
                    sponsors.subList(startIndex, endIndex).forEach { sponsor ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .padding(Dimensions.Padding.quarter)
                                .clip(CircleShape)
                                .background(Color.White)
                                .clickable {
                                    sponsor.selected()
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            val imageUrl = sponsor.validImageUrl
                            if (imageUrl != null) {
                                RemoteImage(
                                    imageUrl = imageUrl,
                                    contentDescription = sponsor.name,
                                )
                            } else {
                                Text(
                                    text = sponsor.name,
                                    modifier = Modifier.padding(Dimensions.Padding.half),
                                    textAlign = TextAlign.Center,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 3,
                                )
                            }
                        }
                    }
                    repeat(columnCount - endIndex + startIndex) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
            Spacer(modifier = Modifier.height(Dimensions.Padding.default))
        }
    }
}

@Composable
private fun EmptyView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .padding(Dimensions.Padding.default),
            tint = MaterialTheme.colorScheme.secondary,
        )

        Text(
            text = "Sponsors could not be loaded.",
            modifier = Modifier.padding(Dimensions.Padding.default),
            textAlign = TextAlign.Center,
        )
    }
}
