package co.touchlab.sessionize.platform

import android.app.Application
import android.os.Handler
import android.os.Looper
import co.touchlab.firebase.firestore.Query
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import java.net.URL
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference


actual fun currentTimeMillis(): Long = System.currentTimeMillis()

private val btfHandler:Handler? = try{Handler(Looper.getMainLooper())}catch (e:Throwable){null}

internal actual val mainThread: Boolean
    get() = Looper.getMainLooper() === Looper.myLooper()

object AndroidAppContext {
    lateinit var app: Application

    val executor = Executors.newSingleThreadExecutor()
    val networkExecutor = Executors.newSingleThreadExecutor()

    fun <B> backgroundTask(backJob: () -> B, mainJob: (B) -> Unit) {
        executor.execute {
            val aref = AtomicReference<B>()
            try {
                aref.set(backJob())
                val h = btfHandler
                if(h == null){
                    mainJob(aref.get())
                }else{
                    h.post {
                        mainJob(aref.get())
                    }
                }
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

    fun backgroundTask(backJob: () -> Unit) {
        backgroundTaskRun(backJob, executor)
    }

    fun networkBackgroundTask(backJob: () -> Unit) {
        backgroundTaskRun(backJob, networkExecutor)
    }

    private fun backgroundTaskRun(backJob: () -> Unit, executor: ExecutorService) {
        executor.execute {
            try {
                backJob()
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
    }

}

actual fun createUuid(): String = UUID.randomUUID().toString()
actual fun printThrowable(t: Throwable) {
    t.printStackTrace()
}

actual fun backgroundDispatcher(): CoroutineDispatcher = Dispatchers.IO

actual fun simpleGet(url: String): String = URL(url).readText()
actual fun networkDispatcher(): CoroutineDispatcher = newSingleThreadContext("network")