package co.touchlab.droidcon.ui.sponsors

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
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

@Composable
internal fun SponsorsView(viewModel: SponsorListViewModel) {
    NavigationStack(key = viewModel, links = {
        NavigationLink(viewModel.observePresentedSponsorDetail) {
            SponsorDetailView(viewModel = it)
        }
    }) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Sponsors") },
                    elevation = 0.dp,
                    modifier = Modifier.shadow(AppBarDefaults.TopAppBarElevation),
                    backgroundColor = MaterialTheme.colors.primary,
                )
            },
        ) {
            val uriHandler = LocalUriHandler.current

            val sponsorGroups by viewModel.observeSponsorGroups.observeAsState()
            Column {
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
    Surface(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.padding(vertical = Dimensions.Padding.quarter, horizontal = Dimensions.Padding.half),
        color = MaterialTheme.colors.background,
        elevation = 2.dp,
        border = if (MaterialTheme.colors.isLight) null else BorderStroke(1.dp, MaterialTheme.colors.surface),
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
                style = MaterialTheme.typography.h4,
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
                                .shadow(2.dp, CircleShape)
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
            tint = MaterialTheme.colors.secondary,
        )

        Text(
            text = "Sponsors could not be loaded.",
            modifier = Modifier.padding(Dimensions.Padding.default),
            textAlign = TextAlign.Center,
        )
    }
}
