package co.touchlab.sessionize

import co.touchlab.sessionize.api.FeedbackApi
import co.touchlab.sessionize.db.SessionizeDbHelper
import co.touchlab.sessionize.platform.NotificationsModel.cancelFeedbackNotifications
import co.touchlab.sessionize.platform.NotificationsModel.feedbackEnabled
import co.touchlab.sessionize.platform.backgroundTask

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
                SessionizeDbHelper.sessionQueries.myPastSession().executeAsOneOrNull()
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
            SessionizeDbHelper.updateFeedback(rating.toLong(), comment, sessionId)
        },{
            cancelFeedbackNotifications()
            requestNextFeedback()
        })
    }
}
