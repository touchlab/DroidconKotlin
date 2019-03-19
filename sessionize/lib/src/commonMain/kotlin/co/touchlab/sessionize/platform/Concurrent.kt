package co.touchlab.sessionize.platform

interface Concurrent {
    fun <B> backgroundTask(backJob: () -> B, mainJob: (B) -> Unit)
}