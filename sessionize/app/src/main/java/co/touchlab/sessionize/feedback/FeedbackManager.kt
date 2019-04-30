package co.touchlab.sessionize.feedback

import androidx.fragment.app.FragmentManager
import co.touchlab.droidcon.db.MyPastSession
import co.touchlab.sessionize.FeedbackModel
import co.touchlab.sessionize.api.FeedbackApi

class FeedbackManager : FeedbackApi {


    private var fragmentManager:FragmentManager? = null
    private var feedbackModel:FeedbackModel = FeedbackModel()
    private var feedbackDialog:FeedbackDialog? = null

    fun setFragmentManager(fragmentManager: FragmentManager){
        this.fragmentManager = fragmentManager
    }

    fun showFeedbackForPastSessions(){
        feedbackModel.showFeedbackForPastSessions(this)
    }

    fun close(){
        this.fragmentManager = null
        feedbackDialog?.dismiss()
        feedbackDialog = null
    }

    fun disableFeedback(){
        //feedbackEnabled = false
    }

    override fun generateFeedbackDialog(session: MyPastSession){
        feedbackDialog = FeedbackDialog.newInstance(sessionId = session.id, sessionTitle = session.title, feedbackManager = this)
        feedbackDialog?.showNow(fragmentManager, "FeedbackDialog")
    }

    fun finishedFeedback(sessionId:String, rating:Int, comment: String) {
        feedbackModel.finishedFeedback(sessionId,rating,comment)
    }

    override fun onError(error: FeedbackApi.FeedBackError) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}