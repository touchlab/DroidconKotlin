package co.touchlab.sessionize

import co.touchlab.droidcon.db.MyPastSessions
import co.touchlab.droidcon.db.MySessions
import co.touchlab.sessionize.api.FeedbackApi
import co.touchlab.sessionize.platform.NotificationFeedbackTag
import co.touchlab.sessionize.platform.backgroundTask
import co.touchlab.sessionize.platform.cancelLocalNotification
import co.touchlab.sessionize.platform.currentTimeMillis

class FeedbackModel {

    private var sessionIdx:Int = 0
    private var sessions: List<MyPastSessions>? = null

    private var feedbackListener: FeedbackApi? = null

    fun showFeedbackForPastSessions(listener: FeedbackApi){
        feedbackListener = listener
        backgroundTask({
            AppContext.sessionQueries.myPastSessions().executeAsList()
        },{
            if(it.isNotEmpty()) {
                sessionIdx = 0
                this.sessions = it
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

    private fun getNextSessionFromList(): MyPastSessions?{
        sessionIdx++
        sessions?.let {
            if(sessionIdx < it.count()) {
                return it[sessionIdx]
            }
        }
        return null
    }

}
