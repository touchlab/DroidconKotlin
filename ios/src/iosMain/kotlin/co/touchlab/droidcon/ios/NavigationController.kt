package co.touchlab.droidcon.ios

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import co.touchlab.droidcon.ios.ui.observeAsState
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

internal class NavigationController: BaseViewModel() {

    private val stack = mutableListOf<NavigationStackItem>()
    private var stackTracking: MutableList<NavigationStackItem> by published(stack, equalityPolicy = neverEqualPolicy())
    private val observeStack by observe(::stackTracking)
    private var activeChild: NavigationController? = null

    companion object {

        val root = NavigationController()
    }

    sealed class NavigationStackItem {
        class BackPressHandler(val onBackPressed: BackPressHandlerScope.() -> Unit): NavigationStackItem() {

            override fun toString(): String {
                return "BackPress@${hashCode().toUInt().toString(16)}"
            }
        }

        class Push<T: Any>(val item: MutableObservableProperty<T?>, val content: @Composable (T) -> Unit): NavigationStackItem() {

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
            is NavigationStackItem.Push<*> -> if (top.item.value != null) {
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
    fun PushedStack(itemModifier: Modifier = Modifier) {
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
    private fun <T: Any> PushedStackItem(item: NavigationStackItem.Push<T>, itemModifier: Modifier) {
        println("$item")
        val itemValue by item.item.observeAsState()

        itemValue?.let {
            Surface {
                item.content(it)
            }
        }
    }

    @Composable
    fun <T: Any> Pushed(item: MutableObservableProperty<T?>, content: @Composable (T) -> Unit) {
        val refTracking = remember {
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
    fun HandleBackPressEffect(onBackPressed: BackPressHandlerScope.() -> Unit) {
        val refTracking = remember {
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

private class ReferenceTracking(private val onDispose: () -> Unit): RememberObserver {

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

    fun <T: Any> NavigationLink(item: MutableObservableProperty<T?>, content: @Composable (T) -> Unit)
}

@Composable
internal fun NavigationStack(vararg keys: Any?, links: NavigationStackScope.() -> Unit, content: @Composable () -> Unit) {

    val activeLinkComposables by remember(keys) {
        val constructedLinks = mutableListOf<ObservableProperty<(@Composable () -> Unit)?>>()
        val scope = object: NavigationStackScope {
            override fun <T: Any> NavigationLink(
                item: MutableObservableProperty<T?>,
                content: @Composable (T) -> Unit,
            ) {
                constructedLinks.add(
                    item.map {
                        it?.let { value ->
                            @Composable {
                                BackPressHandler {
                                    item.value = null
                                }
                                content(value)
                            }
                        }
                    }
                )
            }
        }
        scope.links()

        combine(constructedLinks)
            .map { linkComposables ->
                linkComposables.mapIndexedNotNull { index, value ->
                    value?.let { IndexedValue(index, it) }
                }
            }
    }.observeAsState()

    SubcomposeLayout(measurePolicy = { constraints ->
        val layoutWidth = constraints.maxWidth
        val layoutHeight = constraints.maxHeight

        val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)

        layout(layoutWidth, layoutHeight) {
            val contentMeasurable = subcompose(-1, content)

            val linkMeasurables = activeLinkComposables.fastMap {
                subcompose(it.index, it.value)
            }

            val activeMeasurables = linkMeasurables.lastOrNull() ?: contentMeasurable

            activeMeasurables.fastForEach {
                it.measure(looseConstraints).place(x = 0, y = 0)
            }
        }
    })
}
