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

internal actual fun <B> backgroundTask(backJob: () -> B, mainJob: (B) -> Unit) {
    AndroidAppContext.backgroundTask(backJob, mainJob)
}

private val btfHandler = Handler(Looper.getMainLooper())

internal actual fun <B> backToFront(b: () -> B, job: (B) -> Unit) {
    btfHandler.post { job(b()) }
}

internal actual val mainThread: Boolean
    get() = Looper.getMainLooper() === Looper.myLooper()

object AndroidAppContext {
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

actual fun settingsFactory(): Settings.Factory = PlatformSettings.Factory(AndroidAppContext.app)

actual fun createUuid(): String = UUID.randomUUID().toString()

val notificationPublisher: BroadcastReceiver = NotificationPublisher()

actual fun createLocalNotification(title:String, message:String, timeInMS:Long, notificationId: Int) {

    var notificationTime = timeInMS - tenMinutesInMS
    if(notificationTime < co.touchlab.sessionize.platform.Date(Date()).toLongMillis()){
        notificationTime = co.touchlab.sessionize.platform.Date(Date()).toLongMillis()
    }

    // Building Notification
    val channelId = AndroidAppContext.app.getString(R.string.notification_channel_id)
    var builder = NotificationCompat.Builder(AndroidAppContext.app, channelId)
            .setSmallIcon(R.drawable.notification_tile_bg)
            .setContentTitle(title)
            .setContentText(message)
            .setWhen(notificationTime)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)


    // Building Intent wrapper
    val intent = Intent().also { intent ->
        intent.action = AndroidAppContext.app.getString(R.string.notification_action)
        intent.putExtra(NotificationPublisher.NOTIFICATION_ID, notificationId)
        intent.putExtra(NotificationPublisher.NOTIFICATION, builder.build())
    }
    val pendingIntent = PendingIntent.getBroadcast(AndroidAppContext.app,notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT)

    // Scheduling Intent
    val alarmManager = AndroidAppContext.app.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTime, pendingIntent)

}

class NotificationPublisher : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val notification = intent.getParcelableExtra<Notification>(NOTIFICATION)
        val notificationId = intent.getIntExtra(NOTIFICATION_ID, 0)

        with(NotificationManagerCompat.from(AndroidAppContext.app)) {
            // notificationId is a unique int for each notification that you must define
            this.notify(notificationId, notification)
        }
    }

    companion object {
        var NOTIFICATION_ID = "notification_id"
        var NOTIFICATION = "notification"
    }
}

actual fun cancelLocalNotification(notificationId: Int){
    with(NotificationManagerCompat.from(AndroidAppContext.app)) {
        this.cancel(notificationId)
    }
}

actual fun initializeNotifications(){
    val filter = IntentFilter(AndroidAppContext.app.getString(R.string.notification_action))
    AndroidAppContext.app.registerReceiver(notificationPublisher, filter)

    createNotificationChannel()
}

actual fun deinitializeNotifications(){
    AndroidAppContext.app.unregisterReceiver(notificationPublisher)
}

private fun createNotificationChannel() {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = AndroidAppContext.app.getString(R.string.notification_channel_name)
        val descriptionText = AndroidAppContext.app.getString(R.string.notification_channel_description)
        val channelId = AndroidAppContext.app.getString(R.string.notification_channel_id)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }

        // Register the channel with the system
        val notificationManager: NotificationManager = AndroidAppContext.app.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


        if(!notificationManager.notificationChannels.contains(channel)) {
            notificationManager.createNotificationChannel(channel)
        }
    }
}