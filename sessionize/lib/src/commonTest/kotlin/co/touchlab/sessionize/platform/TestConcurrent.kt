package co.touchlab.sessionize.platform

object TestConcurrent:Concurrent {
    override val allMainThread: Boolean = true

    override fun <B> backgroundTask(backJob: () -> B, mainJob: (B) -> Unit) {
        mainJob(backJob())
    }
}