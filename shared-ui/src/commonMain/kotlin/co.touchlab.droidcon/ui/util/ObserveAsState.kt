package co.touchlab.droidcon.ui.util

import androidx.compose.runtime.*
import org.brightify.hyperdrive.multiplatformx.ManageableViewModel
import org.brightify.hyperdrive.multiplatformx.ObservableObject
import org.brightify.hyperdrive.multiplatformx.property.ObservableProperty

/**
 * Observe a view model as its properties change to update the view.
 *
 * Equivalent to [ObservableProperty.observeAsState] for observing all changes in a view model.
 */
@Composable
internal fun <T: ManageableViewModel> T.observeAsState(): State<T> {
    val result = remember(this) { mutableStateOf(this, neverEqualPolicy()) }
    val listener = remember(this) {
        object: ObservableObject.ChangeTracking.Listener {
            override fun onObjectDidChange() {
                result.value = this@observeAsState
            }
        }
    }
    DisposableEffect(this) {
        val token = changeTracking.addListener(listener)
        result.value = this@observeAsState

        onDispose {
            token.cancel()
        }
    }
    return result
}

/**
 * Observe a view model property as it changes to update the view.
 *
 * Equivalent to [collectAsState] for [ObservableProperty].
 */
@Composable
internal fun <T> ObservableProperty<T>.observeAsState(): State<T> {
    val result = remember(this) { mutableStateOf(value, neverEqualPolicy()) }
    val listener = remember(this) {
        object: ObservableProperty.Listener<T> {
            override fun valueDidChange(oldValue: T, newValue: T) {
                result.value = newValue
            }
        }
    }
    DisposableEffect(this) {
        val token = addListener(listener)
        result.value = value

        onDispose {
            token.cancel()
        }
    }
    return result
}
