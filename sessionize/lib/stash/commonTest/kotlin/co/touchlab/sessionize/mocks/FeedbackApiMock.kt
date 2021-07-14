package co.touchlab.sessionize.mocks

import co.touchlab.droidcon.db.MyPastSession
import co.touchlab.sessionize.FeedbackModel
import co.touchlab.sessionize.api.FeedbackApi

class FeedbackApiMock : FeedbackApi {

    var generatingFeedbackDialog: Boolean = false
    var feedbackError: FeedbackApi.FeedBackError? = null


    private var feedbackModel: FeedbackModel = FeedbackModel()

    fun getFeedbackModel(): FeedbackModel {
        return feedbackModel
    }

    override fun generateFeedbackDialog(session: MyPastSession) {
        generatingFeedbackDialog = true
        feedbackModel.finishedFeedback("1234", 1, "This is a comment")
    }

    override fun onError(error: FeedbackApi.FeedBackError) {
        feedbackError = error
    }
}