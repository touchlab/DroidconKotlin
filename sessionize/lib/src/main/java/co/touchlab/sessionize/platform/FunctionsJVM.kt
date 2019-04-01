package co.touchlab.sessionize.platform

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.russhwolf.settings.PlatformSettings
import com.russhwolf.settings.Settings
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.content.BroadcastReceiver
import android.app.Notification
import android.content.IntentFilter
import app.sessionize.touchlab.lib.R
import java.util.Date


actual fun currentTimeMillis(): Long = System.currentTimeMillis()

internal actual fun <B> backgroundTaskPlatform(backJob: () -> B, mainJob: (B) -> Unit) {
    AndroidAppContext.backgroundTask(backJob, mainJob)
}

private val btfHandler:Handler? = try{Handler(Looper.getMainLooper())}catch (e:Throwable){null}

internal actual fun <B> backToFront(b: () -> B, job: (B) -> Unit) {
    val h = btfHandler
    if(h == null){
        job(b())
    }else{
        h.post { job(b()) }
    }
}

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

actual fun logException(t: Throwable) {
    t.printStackTrace()
}

actual fun createUuid(): String = UUID.randomUUID().toString()
