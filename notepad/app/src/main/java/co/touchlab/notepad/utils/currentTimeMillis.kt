package co.touchlab.notepad.utils

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import co.touchlab.multiplatform.architecture.db.sqlite.AndroidNativeOpenHelperFactory
import co.touchlab.multiplatform.architecture.db.sqlite.NativeOpenHelperFactory
import com.russhwolf.settings.PlatformSettings
import com.russhwolf.settings.Settings
import java.net.URL
import java.util.concurrent.ExecutorService
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

actual fun networkBackgroundTask(backJob: () -> Unit) {
    AndroidAppContext.networkBackgroundTask(backJob)
}

actual fun initContext(): NativeOpenHelperFactory = AndroidNativeOpenHelperFactory(AndroidAppContext.app)

object AndroidAppContext{
    lateinit var app: Application

    val executor = Executors.newSingleThreadExecutor()
    val networkExecutor = Executors.newSingleThreadExecutor()
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
        backgroundTaskRun(backJob, executor)
    }

    fun networkBackgroundTask(backJob: () -> Unit) {
        backgroundTaskRun(backJob, networkExecutor)
    }

    private fun backgroundTaskRun(backJob: () -> Unit, executor: ExecutorService){
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

actual fun settingsFactory(): Settings.Factory  = PlatformSettings.Factory(AndroidAppContext.app)

actual fun logEvent(name: String, vararg params: String) {
    Log.i("ANALYTICS", "$name: ${params.joinToString()}")
}