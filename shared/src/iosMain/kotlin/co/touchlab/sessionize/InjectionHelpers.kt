package co.touchlab.sessionize

import co.touchlab.sessionize.platform.NotificationsModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

//The way we inject stuff needs a refactor, but for today I'm going to do quick and dirty

object NotificationHelper :KoinComponent{
    val notificationModel: NotificationsModel
        get() = get()
}

object FeedbackHelper: KoinComponent{
    val feedbackModel:FeedbackModel
        get() = get()
}