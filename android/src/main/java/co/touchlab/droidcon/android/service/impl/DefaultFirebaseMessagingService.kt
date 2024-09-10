package co.touchlab.droidcon.android.service.impl

import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import co.touchlab.droidcon.application.service.Notification
import co.touchlab.droidcon.service.AndroidNotificationService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.component.inject

class DefaultFirebaseMessagingService: FirebaseMessagingService() {
    private val notificationService: AndroidNotificationService by inject()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        if (message.data.isNotEmpty() && message.data[Notification.Keys.notificationType] == Notification.Values.refreshDataType) {
            MainScope().launch {
                notificationService.handleNotification(
                    Notification.Remote.RefreshData
                )
            }
        }

        // If we have notification, we're running in foreground and should show it ourselves.
        val originalNotification = message.notification ?: return
        val notification = NotificationCompat.Builder(this, message.notification?.channelId ?: "")
            .setContentTitle(originalNotification.title)
            .setContentText(originalNotification.body)
            .apply {
                originalNotification.channelId?.let {
                    setChannelId(it)
                }
            }
            .build()

        val notificationManager = getSystemService<NotificationManager>()
        notificationManager?.notify(0, notification)
    }
}
