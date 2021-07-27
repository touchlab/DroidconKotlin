package co.touchlab.sessionize

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.koin.core.component.KoinComponent
import kotlin.coroutines.CoroutineContext

open class BaseModel : KoinComponent {
    internal val mainScope = MainScope(Dispatchers.Main)

    open fun onDestroy() {
        mainScope.job.cancel()
    }
}

internal class MainScope(private val mainContext: CoroutineContext) : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = mainContext + job + exceptionHandler

    internal val job = Job()
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        showError(throwable)
    }

    fun showError(t: Throwable) {
        t.printStackTrace()
    }
}