package co.touchlab.sessionize.db

import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.staticCFunction
import platform.Foundation.NSThread
import platform.darwin.dispatch_async_f
import platform.darwin.dispatch_get_main_queue
import kotlin.native.concurrent.DetachedObjectGraph
import kotlin.native.concurrent.attach
import kotlin.native.concurrent.freeze

internal actual fun <B> backToFront(b: ()->B, job: (B) -> Unit) {
    dispatch_async_f(dispatch_get_main_queue(), DetachedObjectGraph {
        JobAndThing(job.freeze(), b())
    }.asCPointer(), staticCFunction { it: COpaquePointer? ->
        initRuntimeIfNeeded()
        val result = DetachedObjectGraph<Any>(it).attach() as JobAndThing<B>
        result.job(result.thing)
    })
}

internal data class JobAndThing<B>(val job: (B) -> Unit, val thing:B)

internal actual val mainThread: Boolean
    get() = NSThread.isMainThread