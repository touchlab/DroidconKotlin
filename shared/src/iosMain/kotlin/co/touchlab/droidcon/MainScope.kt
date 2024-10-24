package co.touchlab.droidcon

import co.touchlab.droidcon.util.printThrowable
import co.touchlab.kermit.Logger
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class MainScope(private val mainContext: CoroutineContext, private val log: Logger) : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = mainContext + job + exceptionHandler

    internal val job = SupervisorJob()
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        printThrowable(throwable)
        showError(throwable)
    }

    // TODO: Some way of exposing this to the caller without trapping a reference and freezing it.
    private fun showError(t: Throwable) {
        log.e(throwable = t) { "Error in MainScope" }
    }

    fun onDestroy() {
        job.cancel()
    }
}
