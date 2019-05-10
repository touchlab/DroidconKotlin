package co.touchlab.sessionize

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import app.sessionize.touchlab.lib.R
import co.touchlab.sessionize.api.NotificationsApi
import co.touchlab.sessionize.api.notificationReminderTag
import co.touchlab.sessionize.platform.AndroidAppContext
import co.touchlab.sessionize.platform.NotificationsModel.setNotificationsEnabled
import co.touchlab.sessionize.platform.currentTimeMillis
import java.util.concurrent.TimeUnit


private const val keyTitle = "title"
private const val keyMessage = "message"
private const val keyTimeinMs = "timeinMS"
private const val keyNotificationId = "notification_id"
private const val keyNotificationTag = "notification_tag"

class NotificationsApiImpl : NotificationsApi {

    override fun createLocalNotification(title:String, message:String, timeInMS:Long, notificationId: Int, notificationTag: String) {

        val data = Data.Builder()
        data.put(keyTitle,title)
        data.put(keyMessage, message)
        data.put(keyTimeinMs, timeInMS)
        data.put(keyNotificationId,notificationId)
        data.put(keyNotificationTag, notificationTag)

        val delay = timeInMS - currentTimeMillis()
        val notificationWorkRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInputData(data.build())
                .addTag(notificationTag + notificationId.toString())
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build()

        WorkManager.getInstance().cancelAllWorkByTag(notificationTag + notificationId.toString())
        WorkManager.getInstance().enqueue(notificationWorkRequest)


        if(notificationTag == notificationReminderTag){
            createDismissalWorker(notificationId,notificationTag, delay)
        }

        print("Local $notificationTag Notification Created at $timeInMS: $title - $message \n")
    }


    private fun createDismissalWorker(notificationId: Int, notificationTag: String,timeInMS: Long){
        val data = Data.Builder()
        data.put(keyNotificationId,notificationId)
        data.put(keyNotificationTag, notificationTag)

        val delay = timeInMS + (Durations.TEN_MINS_MILLIS * 2)

        val notificationWorkRequest = OneTimeWorkRequestBuilder<NotificationDismissalWorker>()
                .setInputData(data.build())
                .addTag(notificationTag + notificationId.toString())
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build()

        WorkManager.getInstance().enqueue(notificationWorkRequest)

    }

    override fun cancelLocalNotification(notificationId: Int, notificationTag: String) {
        with(NotificationManagerCompat.from(AndroidAppContext.app)) {
            this.cancel(notificationTag,notificationId)
            print("Cancelling Local $notificationTag Notification")

        }
    }


    // General Notification Code

    override fun initializeNotifications(onSuccess: (Boolean) -> Unit)
    {
        createNotificationChannel()
        setNotificationsEnabled(true)
        onSuccess(true)
    }

    override fun deinitializeNotifications() {
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

    class NotificationWorker(appContext: Context, workerParams: WorkerParameters)
        : Worker(appContext, workerParams) {

        override fun doWork(): Result {

            val title = inputData.getString(keyTitle)
            val message = inputData.getString(keyMessage)
            val timeInMS = inputData.getLong(keyTimeinMs,-1)
            val notificationId = inputData.getInt(keyNotificationId, -1)
            val notificationTag = inputData.getString(keyNotificationTag)

            if(title.isNullOrBlank() || message.isNullOrBlank() || notificationTag.isNullOrBlank()){
                return Result.failure()
            }
            if(timeInMS == -1L || notificationId == -1){
                return Result.failure()
            }


            val channelId = AndroidAppContext.app.getString(R.string.notification_channel_id)
            val builder = NotificationCompat.Builder(AndroidAppContext.app, channelId)
                    .setSmallIcon(R.drawable.baseline_insert_invitation_24)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setWhen(timeInMS)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setCategory(NotificationCompat.CATEGORY_REMINDER)


            with(NotificationManagerCompat.from(AndroidAppContext.app)) {
                this.notify(notificationTag, notificationId, builder.build())
            }
            return Result.success()
        }
    }

    class NotificationDismissalWorker(appContext: Context, workerParams: WorkerParameters)
        : Worker(appContext, workerParams) {

        override fun doWork(): Result {
            val notificationId = inputData.getInt(keyNotificationId, -1)
            val notificationTag = inputData.getString(keyNotificationTag)

            if(notificationTag.isNullOrBlank() || notificationId == -1){
                return Result.failure()
            }

            with(NotificationManagerCompat.from(AndroidAppContext.app)) {
                this.cancel(notificationTag, notificationId)
            }
            return Result.success()
        }
    }

}
