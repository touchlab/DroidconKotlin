package co.touchlab.sessionize.platform

import co.touchlab.sessionize.AppContext
import co.touchlab.sessionize.ServiceRegistry
import com.russhwolf.settings.set


fun notificationsEnabled() : Boolean{
    return ServiceRegistry.appSettings.getBoolean(AppContext.LOCAL_NOTIFICATIONS_ENABLED,true)
}

fun feedbackEnabled() : Boolean{
    return ServiceRegistry.appSettings.getBoolean(AppContext.FEEDBACK_ENABLED,true)
}

fun reminderNotificationsEnabled() : Boolean{
    return ServiceRegistry.appSettings.getBoolean(AppContext.LOCAL_NOTIFICATIONS_ENABLED,true) &&
            ServiceRegistry.appSettings.getBoolean(AppContext.REMINDERS_ENABLED,true)
}

fun setNotificationsEnabled(enabled: Boolean){
    ServiceRegistry.appSettings[AppContext.LOCAL_NOTIFICATIONS_ENABLED] = enabled
}

fun setFeedbackEnabled(enabled: Boolean){
    ServiceRegistry.appSettings[AppContext.FEEDBACK_ENABLED] = enabled
}