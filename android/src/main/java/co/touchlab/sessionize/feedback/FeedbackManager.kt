package co.touchlab.sessionize.feedback

import android.content.Context
import android.content.Intent
import androidx.fragment.app.FragmentManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import co.touchlab.droidcon.db.MyPastSession
import co.touchlab.sessionize.FeedbackModel
import co.touchlab.sessionize.SoftExceptionCallback
import co.touchlab.sessionize.api.FeedbackApi
import co.touchlab.sessionize.platform.NotificationsModel
import org.koin.core.component.KoinComponent

class FeedbackManager(
    private val feedbackModel:FeedbackModel,
    private val notificationsModel: NotificationsModel,
    private val softExceptionCallback: SoftExceptionCallback,
    private val context: Context
    ) : FeedbackApi, KoinComponent {

    private var fragmentManager:FragmentManager? = null
    private var feedbackDialog:FeedbackDialog? = null

    fun setFragmentManager(fragmentManager: FragmentManager){
        this.fragmentManager = fragmentManager
    }

    fun showFeedbackForPastSessions(){
        feedbackModel.showFeedbackForPastSessions(this)
    }

    fun close(){
        this.fragmentManager = null
        try {
            feedbackDialog?.dismiss()
        } catch (e: Exception) {
            softExceptionCallback(e, "Failed closing FeedbackManager")
        }
        feedbackDialog = null
    }

    fun disableFeedback(){
        notificationsModel.feedbackEnabled = false
        LocalBroadcastManager.getInstance(context).sendBroadcast(Intent(FeedbackDisabledNotificationName))
    }

    override fun generateFeedbackDialog(session: MyPastSession){
        feedbackDialog = FeedbackDialog.newInstance(sessionId = session.id, sessionTitle = session.title, feedbackManager = this)
        try {
            fragmentManager?.let {
                feedbackDialog?.showNow(it, "FeedbackDialog")
            }
        } catch (e: Exception) {
            softExceptionCallback(e, "Failed generating feedback dialog. Probably closing context.")
        }
    }

    fun finishedFeedback(sessionId:String, rating:Int, comment: String) {
        feedbackModel.finishedFeedback(sessionId,rating,comment)
    }

    override fun onError(error: FeedbackApi.FeedBackError) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object{
        const val FeedbackDisabledNotificationName = "FeedbackDisabled"
    }
}