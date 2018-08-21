package co.touchlab.sessionize.platform

import kotlin.coroutines.experimental.*
import kotlinx.coroutines.experimental.*
import platform.darwin.*
import konan.worker.*

internal actual val MainDispatcher: CoroutineDispatcher = NsQueueDispatcher(dispatch_get_main_queue())
internal actual val ApplicationDispatcher: CoroutineDispatcher = MainDispatcher/*DefaultDispatcher*/

internal class NsQueueDispatcher(
        private val dispatchQueue: dispatch_queue_t
) : CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
        dispatch_async(dispatchQueue) {
            block.run()
        }
    }
}

/*
internal class WorkerDispatcher() : CoroutineDispatcher() {
    var worker: Worker? = null
    private val myWorker: Worker
        get() {
            if (worker == null)
                worker = startWorker()
            return worker!!
        }

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        myWorker.schedule(TransferMode.CHECKED,
                { block.freeze() }) {
            it.run()
        }
    }
}*/
