package co.touchlab.droidcon.ui.util

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.touchlab.droidcon.util.NavigationController
import kotlin.math.min

@Composable
internal actual fun NavigationBackPressWrapper(content: @Composable () -> Unit) {
    val triggerBackPressDragDistance = 40f

    Box(contentAlignment = Alignment.CenterStart) {
        content()

        var dragDistance by remember { mutableStateOf(0f) }
        val state = rememberDraggableState {
            dragDistance = min(dragDistance + it, triggerBackPressDragDistance * 2)
        }
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(20.dp)
                .draggable(
                    state = state,
                    orientation = Orientation.Horizontal,
                    onDragStopped = {
                        if (dragDistance > triggerBackPressDragDistance) {
                            NavigationController.root.handleBackPress()
                            dragDistance = 0f
                        }
                    },
                ),
        )

        AnimatedVisibility(
            visible = dragDistance > triggerBackPressDragDistance,
            enter = slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(durationMillis = 100)),
            exit = slideOutHorizontally(targetOffsetX = { -it }),
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}
