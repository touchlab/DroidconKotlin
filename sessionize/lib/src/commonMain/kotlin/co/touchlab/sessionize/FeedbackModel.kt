package co.touchlab.sessionize

import co.touchlab.sessionize.api.FeedbackApi
import co.touchlab.sessionize.api.notificationFeedbackTag
import co.touchlab.sessionize.platform.backgroundTask
import co.touchlab.sessionize.platform.feedbackEnabled

class FeedbackModel {
    private var feedbackListener: FeedbackApi? = null

    fun showFeedbackForPastSessions(listener: FeedbackApi){
        if(feedbackEnabled()) {
            feedbackListener = listener
            requestNextFeedback()
        }
    }

    fun requestNextFeedback(){
        backgroundTask({
            if(feedbackEnabled()) {
                AppContext.sessionQueries.myPastSession().executeAsOneOrNull()
            }else null
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
