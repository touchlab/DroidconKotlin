package co.touchlab.sessionize.platform

object MainConcurrent:Concurrent{
    override fun <B> backgroundTask(backJob: () -> B, mainJob: (B) -> Unit) {
        assertMainThread()
        backgroundTaskPlatform(backJob, mainJob)
    }
}