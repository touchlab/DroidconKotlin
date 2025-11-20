package co.touchlab.droidcon.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.flow.StateFlow

/**
 * Observe a view model property as it changes to update the view.
 * Uses StateFlow's collectAsState for Compose integration.
 */
@Composable
internal fun <T> StateFlow<T>.observeAsState(): State<T> = collectAsState()
