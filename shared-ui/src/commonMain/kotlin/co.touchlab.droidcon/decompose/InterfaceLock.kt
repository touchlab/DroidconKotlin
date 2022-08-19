package co.touchlab.droidcon.decompose

import com.arkivanov.essenty.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

internal fun LifecycleOwner.interfaceLock(context: CoroutineContext): InterfaceLock =
    DefaultInterfaceLock(coroutineScope(context))

internal interface InterfaceLock {

    fun runExclusively(work: suspend () -> Unit)
}

private class DefaultInterfaceLock(
    private val scope: CoroutineScope,
): InterfaceLock {

    private var lock = false

    override fun runExclusively(work: suspend () -> Unit) {
        if (lock) {
            return
        }

        lock = true

        scope.launch {
            try {
                work()
            } finally {
                lock = false
            }
        }
    }
}
