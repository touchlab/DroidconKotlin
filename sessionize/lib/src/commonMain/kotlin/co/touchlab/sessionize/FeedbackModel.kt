package co.touchlab.sessionize

import co.touchlab.droidcon.db.MySessions
import co.touchlab.sessionize.api.FeedbackApi
import co.touchlab.sessionize.platform.NotificationFeedbackTag
import co.touchlab.sessionize.platform.backgroundTask
import co.touchlab.sessionize.platform.cancelLocalNotification
import co.touchlab.sessionize.platform.currentTimeMillis

class FeedbackModel {

    private var sessionIdx:Int = 0
    private var sessions: List<MySessions>? = null

    private var feedbackListener: FeedbackApi? = null


    fun showFeedbackForPastSessions(listener: FeedbackApi){
        feedbackListener = listener
        backgroundTask({
            AppContext.sessionQueries.mySessions().executeAsList().filter {
                it.feedbackRating == null && it.endsAt.toLongMillis() < currentTimeMillis()
            }
        },{
            if(it.isNotEmpty()) {
                sessionIdx = 0
                this.sessions = sessions
                this.sessions?.let { session ->
                    feedbackListener?.generateFeedbackDialog(session[sessionIdx])
                }
            }else{
                feedbackListener?.onError(FeedbackApi.FeedBackError.NoSessions)
            }
        })
    }


    fun finishedFeedback(sessionId:String, rating:Int, comment: String) {
        backgroundTask({
            AppContext.dbHelper.updateFeedback(rating.toLong(),comment,sessionId)
        },{
            cancelLocalNotification(sessionId.hashCode(), NotificationFeedbackTag)

            getNextSessionFromList()?.let {
                feedbackListener?.generateFeedbackDialog(it)
            }
        })
    }

    private fun getNextSessionFromList(): MySessions?{
        sessionIdx++
        sessions?.let {
            if(sessionIdx < it.count()) {
                return it[sessionIdx]
            }
        }
        return null
    }

}
