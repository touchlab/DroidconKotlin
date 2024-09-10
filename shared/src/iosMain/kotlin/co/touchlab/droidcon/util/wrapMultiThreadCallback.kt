package co.touchlab.droidcon.util

import co.touchlab.stately.freeze
import kotlinx.coroutines.CompletableDeferred

// Closures crash because of multi-thread execution, these methods prevent that.
suspend fun <T> wrapMultiThreadCallback(call: (callback: (T) -> Unit) -> Unit): T {
    val completable = CompletableDeferred<T>()
    val closure: (T) -> Unit = { completable.complete(it) }
    call(closure)
    return completable.await()
}

suspend fun <T1, T2> wrapMultiThreadCallback(call: (callback: (T1, T2) -> Unit) -> Unit): Pair<T1, T2> {
    val completable = CompletableDeferred<Pair<T1, T2>>()
    val closure: (T1, T2) -> Unit = { t1, t2 -> completable.complete(t1 to t2) }
    call(closure)
    return completable.await()
}
