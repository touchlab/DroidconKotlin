package co.touchlab.droidcon.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import co.touchlab.droidcon.application.service.NotificationSchedulingService
import co.touchlab.droidcon.domain.gateway.SessionGateway
import co.touchlab.droidcon.domain.service.DateTimeService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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