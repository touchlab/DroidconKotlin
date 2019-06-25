package co.touchlab.sessionize

import co.touchlab.sessionize.api.FeedbackApi
import co.touchlab.sessionize.db.SessionizeDbHelper
import co.touchlab.sessionize.jsondata.Session
import co.touchlab.sessionize.platform.Date
import co.touchlab.sessionize.platform.NotificationsModel.cancelFeedbackNotificationsForSession
import co.touchlab.sessionize.platform.NotificationsModel.feedbackEnabled
import co.touchlab.sessionize.platform.backgroundTask
import co.touchlab.sessionize.platform.currentTimeMillis

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
                SessionizeDbHelper.sessionQueries.myPastSession().executeAsList()
            }else null
        },{ pastSessions ->
            pastSessions?.firstOrNull { it.endsAt.toLongMillis() < currentTimeMillis() }
                    ?.let { feedbackListener?.generateFeedbackDialog(it) }
                    ?: feedbackListener?.onError(FeedbackApi.FeedBackError.NoSessions)
        })
    }


    fun finishedFeedback(sessionId:String, rating:Int, comment: String) {
        backgroundTask({
            SessionizeDbHelper.updateFeedback(rating.toLong(), comment, sessionId)
        },{
            cancelFeedbackNotificationsForSession()
            requestNextFeedback()
        })
    }
}
