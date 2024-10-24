package co.touchlab.droidcon.util

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints
import co.touchlab.droidcon.ui.util.NavigationBackPressWrapper
import co.touchlab.droidcon.ui.util.observeAsState
import org.brightify.hyperdrive.multiplatformx.BaseViewModel
import org.brightify.hyperdrive.multiplatformx.CancellationToken
import org.brightify.hyperdrive.multiplatformx.property.MutableObservableProperty
import org.brightify.hyperdrive.multiplatformx.property.ObservableProperty
import org.brightify.hyperdrive.multiplatformx.property.combine
import org.brightify.hyperdrive.multiplatformx.property.map
import org.brightify.hyperdrive.multiplatformx.property.neverEqualPolicy

private val LocalNavigationController = staticCompositionLocalOf {
    NavigationController.root
}

private val LocalNavigationViewDimensions = staticCompositionLocalOf<NavigationViewDimensions> {
    error("NavigationView hasn't been used.")
}

class NavigationController : BaseViewModel() {

    private val stack = mutableListOf<NavigationStackItem>()
    private var stackTracking: MutableList<NavigationStackItem> by published(stack, equalityPolicy = neverEqualPolicy())
    private val observeStack by observe(::stackTracking)
    private var activeChild: NavigationController? = null

    companion object {

        val root = NavigationController()
    }

    internal sealed class NavigationStackItem {
        class BackPressHandler(val onBackPressed: BackPressHandlerScope.() -> Unit) : NavigationStackItem() {

            override fun toString(): String {
                return "BackPress@${hashCode().toUInt().toString(16)}"
            }
        }

        class Push<T : Any>(val item: MutableObservableProperty<T?>, val content: @Composable (T) -> Unit) : NavigationStackItem() {

            override fun toString(): String {
                return "Push(${item.value}@${item.hashCode().toUInt().toString(16)})@${hashCode().toUInt().toString(16)}"
            }
        }
    }

    class BackPressHandlerScope {

        var isSkipped = false
            private set

        fun skip() {
            isSkipped = true
        }
    }

    fun handleBackPress(): Boolean {
        val activeChild = activeChild

        return if (activeChild != null && activeChild.handleBackPress()) {
            true
        } else {
            pop()
        }
    }

    private fun pop(defer: Int = 0): Boolean {
        val currentIndex = stack.count() - 1 - defer
        return when (val top = stack.getOrNull(currentIndex)) {
            is NavigationStackItem.BackPressHandler -> {
                val scope = BackPressHandlerScope()
                top.onBackPressed(scope)
                if (scope.isSkipped) {
                    pop(defer + 1)
                } else {
                    true
                }
            }

            is NavigationStackItem.Push<*> ->
                if (top.item.value != null) {
                    stack.removeAt(currentIndex)
                    top.item.value = null
                    true
                } else {
                    pop(defer + 1)
                }

            null -> false
        }
    }

    fun branch(child: NavigationController): CancellationToken {
        activeChild = child
        return CancellationToken {
            if (activeChild === child) {
                activeChild = null
            }
        }
    }

    @Composable
    internal fun PushedStack(itemModifier: Modifier = Modifier) {
        val currentStack by observeStack.observeAsState()

        var i = 0
        while (i < currentStack.count()) {
            when (val item = currentStack[i++]) {
                is NavigationStackItem.BackPressHandler -> continue
                is NavigationStackItem.Push<*> -> PushedStackItem(item, itemModifier)
            }
        }
    }

    @Composable
    private fun <T : Any> PushedStackItem(item: NavigationStackItem.Push<T>, itemModifier: Modifier) {
        println("$item")
        val itemValue by item.item.observeAsState()

        itemValue?.let {
            Surface(modifier = itemModifier) {
                item.content(it)
            }
        }
    }

    @Composable
    internal fun <T : Any> Pushed(item: MutableObservableProperty<T?>, content: @Composable (T) -> Unit) {
        remember {
            val stackItem = NavigationStackItem.Push(item, content).also {
                notifyingStackChange {
                    stack.add(it)
                }
            }
            ReferenceTracking {
                notifyingStackChange {
                    stack.remove(stackItem)
                }
            }
        }
    }

    @Composable
    internal fun HandleBackPressEffect(onBackPressed: BackPressHandlerScope.() -> Unit) {
        remember {
            val stackItem = NavigationStackItem.BackPressHandler(onBackPressed).also {
                stack.add(it)
            }
            ReferenceTracking {
                stack.remove(stackItem)
            }
        }
    }

    private inline fun <T> notifyingStackChange(block: () -> T): T {
        val result = block()
        observeStack.value = stack
        return result
    }
}

internal data class NavigationViewDimensions(
    val constraints: Constraints,
)

@Composable
internal fun rememberNavigationController(): NavigationController = remember {
    NavigationController()
}

private class ReferenceTracking(private val onDispose: () -> Unit) : RememberObserver {

    private var refCount: Int = 0

    override fun onAbandoned() {
        onDispose()
    }

    override fun onForgotten() {
        refCount -= 1
        if (refCount <= 0) {
            onDispose()
        }
    }

    override fun onRemembered() {
        refCount += 1
    }
}

@Composable
internal fun BackPressHandler(onBackPressed: NavigationController.BackPressHandlerScope.() -> Unit) {
    val navigationController = LocalNavigationController.current
    navigationController.HandleBackPressEffect(onBackPressed)
}

internal interface NavigationStackScope {

    fun <T : Any> NavigationLink(item: MutableObservableProperty<T?>, content: @Composable (T) -> Unit)
}

internal class NavigationLinkWrapper<T : Any>(
    val index: Int,
    private val value: T?,
    private val reset: () -> Unit,
    private val content: @Composable (T) -> Unit,
) {

    val body: (@Composable () -> Unit)?
        get() = value?.let { value ->
            @Composable {
                BackPressHandler {
                    reset()
                }
                NavigationBackPressWrapper {
                    content(value)
                }
            }
        }

    override fun equals(other: Any?): Boolean {
        return (other as? NavigationLinkWrapper<*>)?.let { it.index == index && it.value == value } ?: false
    }

    override fun hashCode(): Int {
        return listOfNotNull(index, value).hashCode()
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
internal fun NavigationStack(key: Any?, links: NavigationStackScope.() -> Unit, content: @Composable () -> Unit) {
    val activeLinkComposables by remember(key) {
        val constructedLinks = mutableListOf<ObservableProperty<NavigationLinkWrapper<*>>>()
        val scope = object : NavigationStackScope {
            override fun <T : Any> NavigationLink(
                item: MutableObservableProperty<T?>,
                content: @Composable (T) -> Unit,
            ) {
                constructedLinks.add(
                    item.map {
                        NavigationLinkWrapper(index = constructedLinks.size, value = it, reset = { item.value = null }, content)
                    },
                )
            }
        }
        scope.links()

        combine(constructedLinks)
    }.observeAsState()

    AnimatedContent(
        targetState = activeLinkComposables,
        transitionSpec = {
            if (initialState.indexOfLast { it.body != null } < targetState.indexOfLast { it.body != null }) {
                slideInHorizontally(initialOffsetX = { it }) with slideOutHorizontally(targetOffsetX = { -it })
            } else {
                slideInHorizontally(initialOffsetX = { -it }) with slideOutHorizontally(targetOffsetX = { it })
            }
        },
        contentAlignment = Alignment.BottomCenter,
    ) { activeComposables ->
        SubcomposeLayout(
            measurePolicy = { constraints ->
                val layoutWidth = constraints.maxWidth
                val layoutHeight = constraints.maxHeight

                val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)

                layout(layoutWidth, layoutHeight) {
                    val contentMeasurable = subcompose(-1, content)

                    val linkMeasurables = activeComposables.mapNotNull { wrapper ->
                        wrapper.body?.let { subcompose(wrapper.index, it) }
                    }

                    val activeMeasurables = linkMeasurables.lastOrNull() ?: contentMeasurable

                    activeMeasurables.forEach {
                        it.measure(looseConstraints).place(x = 0, y = 0)
                    }
                }
            },
        )
    }
}
