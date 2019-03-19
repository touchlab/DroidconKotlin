package co.touchlab.sessionize.platform

object TestConcurrent:Concurrent {
    override fun <B> backgroundTask(backJob: () -> B, mainJob: (B) -> Unit) {
        mainJob(backJob())
    }
}