package co.touchlab.notepad.utils

import android.app.Application
import android.os.Handler
import android.os.Looper
import co.touchlab.multiplatform.architecture.db.sqlite.AndroidNativeOpenHelperFactory
import co.touchlab.multiplatform.architecture.db.sqlite.NativeOpenHelperFactory
import java.net.URL
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference

actual fun sleepThread(millis: Long) {}
actual fun <T> goFreeze(a: T): T = a

actual fun currentTimeMillis(): Long = System.currentTimeMillis()

actual fun <B> backgroundTask(backJob: () -> B, mainJob: (B) -> Unit) {
    AndroidAppContext.backgroundTask(backJob, mainJob)
}

actual fun backgroundTask(backJob:()->Unit){
    AndroidAppContext.backgroundTask(backJob)
}

actual fun initContext(): NativeOpenHelperFactory = AndroidNativeOpenHelperFactory(AndroidAppContext.app)

object AndroidAppContext{
    lateinit var app: Application

    val executor = Executors.newSingleThreadExecutor()
    val handler = Handler(Looper.getMainLooper())

    fun <B> backgroundTask(backJob: () -> B, mainJob: (B) -> Unit) {
        executor.execute {
            val aref = AtomicReference<B>()
            try {
                aref.set(backJob())
                handler.post {
                    mainJob(aref.get())
                }
            }catch (t:Throwable){
                t.printStackTrace()
            }

        }
    }

    fun backgroundTask(backJob: () -> Unit) {
        executor.execute {
            try {
                backJob()
            }catch (t:Throwable){
                t.printStackTrace()
            }
        }
    }
}

actual fun simpleGet(url: String): String = URL(url).readText()

actual fun logException(t: Throwable) {
    t.printStackTrace()
}