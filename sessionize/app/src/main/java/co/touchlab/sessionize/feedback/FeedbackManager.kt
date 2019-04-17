package co.touchlab.sessionize.feedback

import androidx.fragment.app.FragmentManager
import co.touchlab.droidcon.db.MySessions
import co.touchlab.sessionize.AppContext
import co.touchlab.sessionize.AppContext.SessionListener
import co.touchlab.sessionize.platform.NotificationFeedbackTag
import co.touchlab.sessionize.platform.cancelLocalNotification

class FeedbackManager : SessionListener{


    private var fragmentManager:FragmentManager? = null

    private var sessionIdx:Int = 0
    private var sessions: List<MySessions>? = null

    fun setFragmentManager(fragmentManager: FragmentManager){
        this.fragmentManager = fragmentManager
    }

    fun showFeedbackForPastSessions(){
        AppContext.requestMySessionsForFeedback(this)
    }

    private fun generateFeedbackDialog(session: MySessions){
        val feedbackDialog = FeedbackDialog()
        feedbackDialog.showNow(fragmentManager, "FeedbackDialog")
        feedbackDialog.setSessionInfo(session.id, session.title)
        feedbackDialog.setFeedbackManager(this)
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

    fun finishedFeedback(sessionId:String, rating:Int, comment: String){
        AppContext.setSessionFeedback(sessionId,rating,comment, this)
    }

    override fun onMySessionsRetrieved(sessions: List<MySessions>) {
        if(sessions.isNotEmpty()) {
            sessionIdx = 0
            this.sessions = sessions
            this.sessions?.let {
                generateFeedbackDialog(it[sessionIdx])
            }
        }
    }

    override fun onFeedbackSaved(sessionId: String) {
        cancelLocalNotification(sessionId.hashCode(), NotificationFeedbackTag)

        getNextSessionFromList()?.let {
            generateFeedbackDialog(it)
        }
    }
}