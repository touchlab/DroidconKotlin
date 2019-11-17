package co.touchlab.sessionize

import co.touchlab.sessionize.platform.logException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

open class BaseModel(
        private val mainContext: CoroutineContext
) {
    internal val mainScope = MainScope(mainContext)

    open fun onDestroy() {
        mainScope.job.cancel()
    }
}

internal class MainScope(private val mainContext: CoroutineContext): CoroutineScope{
    override val coroutineContext: CoroutineContext
        get() = mainContext + job + exceptionHandler

    internal val job = Job()
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        showError(throwable)
    }

    fun showError(t: Throwable) {
        logException(t)
    }
}