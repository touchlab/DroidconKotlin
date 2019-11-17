package co.touchlab.sessionize

import co.touchlab.droidcon.db.MyPastSession
import co.touchlab.sessionize.api.FeedbackApi
import co.touchlab.sessionize.api.NetworkRepo
import co.touchlab.sessionize.db.SessionizeDbHelper
import co.touchlab.sessionize.platform.NotificationsModel.cancelFeedbackNotifications
import co.touchlab.sessionize.platform.NotificationsModel.feedbackEnabled
import co.touchlab.sessionize.platform.currentTimeMillis
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeedbackModel : BaseModel(ServiceRegistry.coroutinesDispatcher) {
    private var feedbackListener: FeedbackApi? = null

    fun showFeedbackForPastSessions(listener: FeedbackApi){
        if(feedbackEnabled()) {
            feedbackListener = listener
            requestNextFeedback()
        }
    }

    fun requestNextFeedback(){
        mainScope.launch {
            loadPastSessions()?.firstOrNull { it.endsAt.toLongMillis() < currentTimeMillis() }
                    ?.let { feedbackListener?.generateFeedbackDialog(it) }
                    ?: feedbackListener?.onError(FeedbackApi.FeedBackError.NoSessions)
        }
    }

    private suspend fun loadPastSessions():List<MyPastSession>? = withContext(ServiceRegistry.backgroundDispatcher) {
        if(feedbackEnabled()) {
            SessionizeDbHelper.sessionQueries.myPastSession().executeAsList()
        } else
            null
    }

    fun finishedFeedback(sessionId:String, rating:Int, comment: String) {
        mainScope.launch {
            updateFeedback(sessionId, rating, comment)
            cancelFeedbackNotifications()
            requestNextFeedback()
            NetworkRepo.sendFeedback()
        }
    }

    private suspend fun updateFeedback(sessionId:String, rating:Int, comment: String) = withContext(ServiceRegistry.backgroundDispatcher){
        SessionizeDbHelper.updateFeedback(rating.toLong(), comment, sessionId)
    }
}
