package co.touchlab.sessionize.db

import android.os.Handler
import android.os.Looper

private val btfHandler = Handler(Looper.getMainLooper()
)
internal actual fun <B> backToFront(b:()->B, job: (B) -> Unit) {
    btfHandler.post { job(b()) }
}

internal actual val mainThread: Boolean
    get() = Looper.getMainLooper() === Looper.myLooper()