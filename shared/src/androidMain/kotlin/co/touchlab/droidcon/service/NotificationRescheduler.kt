package co.touchlab.droidcon.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import co.touchlab.droidcon.application.service.NotificationSchedulingService
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NotificationRescheduler: BroadcastReceiver(), KoinComponent {
    private val notificationSchedulingService by inject<NotificationSchedulingService>()

    override fun onReceive(context: Context?, intent: Intent?) {
        val backgroundIntent = goAsync()
        MainScope().launch {
            notificationSchedulingService.rescheduleAll()
            backgroundIntent.finish()
        }
    }
}