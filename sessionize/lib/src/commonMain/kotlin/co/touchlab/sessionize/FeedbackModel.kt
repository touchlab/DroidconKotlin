package co.touchlab.sessionize

import co.touchlab.sessionize.api.FeedbackApi
import co.touchlab.sessionize.api.notificationFeedbackTag
import co.touchlab.sessionize.platform.backgroundTask

class FeedbackModel {
    private var feedbackListener: FeedbackApi? = null

    fun showFeedbackForPastSessions(listener: FeedbackApi){
        feedbackListener = listener
        requestNextFeedback()
    }

    fun requestNextFeedback(){
        backgroundTask({
            AppContext.sessionQueries.myPastSession().executeAsOneOrNull()
        },{

            it?.let {pastSession ->
                feedbackListener?.generateFeedbackDialog(pastSession)
            }?: run {
                feedbackListener?.onError(FeedbackApi.FeedBackError.NoSessions)
            }
        })
    }


    fun finishedFeedback(sessionId:String, rating:Int, comment: String) {
        backgroundTask({
            AppContext.dbHelper.updateFeedback(rating.toLong(),comment,sessionId)
        },{
            ServiceRegistry.notificationsApi.cancelLocalNotification(sessionId.hashCode(), notificationFeedbackTag)

            requestNextFeedback()
        })
    }
}
