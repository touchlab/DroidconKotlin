package co.touchlab.droidcon.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun DcAsyncImage(logTag: String, url: String?, contentDescription: String?, modifier: Modifier = Modifier)

@Composable
expect fun InitImageLoader()
