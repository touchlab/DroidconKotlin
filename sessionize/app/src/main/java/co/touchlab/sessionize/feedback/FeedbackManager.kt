package co.touchlab.sessionize.feedback

import androidx.fragment.app.FragmentManager
import co.touchlab.droidcon.db.MySessions
import co.touchlab.sessionize.FeedbackModel
import co.touchlab.sessionize.api.FeedbackApi

class FeedbackManager : FeedbackApi {


    private var fragmentManager:FragmentManager? = null
    private var feedbackModel:FeedbackModel = FeedbackModel()


    fun setFragmentManager(fragmentManager: FragmentManager){
        this.fragmentManager = fragmentManager
    }

    fun showFeedbackForPastSessions(){
        feedbackModel.showFeedbackForPastSessions(this)
    }

    override fun generateFeedbackDialog(session: MySessions){
        val feedbackDialog = FeedbackDialog()
        feedbackDialog.showNow(fragmentManager, "FeedbackDialog")
        feedbackDialog.setSessionInfo(session.id, session.title)
        feedbackDialog.setFeedbackManager(this)
    }

    fun finishedFeedback(sessionId:String, rating:Int, comment: String) {
        feedbackModel.finishedFeedback(sessionId,rating,comment)
    }

    override fun onError(error: FeedbackApi.FeedBackError) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}