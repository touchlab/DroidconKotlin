package co.touchlab.sessionize.feedback

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import co.touchlab.sessionize.R


enum class FeedbackRating(val value: Int) {
    None(0),
    Good(1),
    Ok(2),
    Bad(3)
}

class FeedbackDialog : DialogFragment(), FeedbackView.FeedbackViewListener {

    lateinit var sessionId: String
    lateinit var sessionTitle: String
    private var feedbackManager: FeedbackManager? = null
    private var feedbackView: FeedbackView? = null

    private var rating: FeedbackRating = FeedbackRating.None
    private var comments: String = ""


    companion object {
        private const val sessionIdName = "sessionId"
        private const val sessionTitleName = "sessionTitle"

        fun newInstance(sessionId: String, sessionTitle: String, feedbackManager: FeedbackManager): FeedbackDialog {
            val bundle = Bundle()
            bundle.putString(sessionIdName, sessionId)
            bundle.putString(sessionTitleName, sessionTitle)
            val feedbackDialog = FeedbackDialog()
            feedbackDialog.arguments = bundle
            feedbackDialog.setFeedbackManager(feedbackManager)
            return feedbackDialog
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            arguments?.let { bundle ->
                sessionId = bundle[sessionIdName] as String
                sessionTitle = bundle[sessionTitleName] as String
            }

            feedbackView = createFeedbackView(it.layoutInflater)

            val builder = AlertDialog.Builder(it)
            builder.setView(feedbackView)
            val dialog = builder.create()
            dialog.setOnShowListener {
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.GRAY)
            }
            return dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun createFeedbackView(inflater: LayoutInflater): FeedbackView {
        val view = inflater.inflate(R.layout.feedback_view, null) as FeedbackView
        view.setFeedbackViewListener(this)
        view.createButtonListeners()
        sessionTitle.let {
            view.setSessionTitle(it)
        }
        return view
    }

    fun setFeedbackManager(fbManager: FeedbackManager) {
        this.feedbackManager = fbManager
    }

    private fun finishAndClose() {
        feedbackManager?.finishedFeedback(sessionId, rating.value, comments)
        dismiss()
    }

    override fun ratingSelected(rating: FeedbackRating) {
        this.rating = rating
    }

    override fun submitPressed(comment: String?) {
        this.comments = comment ?: ""
        finishAndClose()
    }

    override fun closePressed() {
        feedbackManager?.disableFeedback()
        dismiss()
    }
}
