package co.touchlab.sessionize.feedback

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.feedback_view.view.*

class FeedbackRatingView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : FeedbackSubview(context,attrs,defStyleAttr) {

    override fun createButtonListeners(){
        super.createButtonListeners()

        additionalButton.isEnabled = false
        additionalButton.setTextColor(Color.LTGRAY)

        goodButton.setOnClickListener {
            resetRatingButtons()
            goodButton.background.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN)
            feedbackInteractionInterface?.feedbackSelected(FeedbackRating.Good)
            activateCommentButton()
        }
        okButton.setOnClickListener {
            resetRatingButtons()
            okButton.background.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN)
            feedbackInteractionInterface?.feedbackSelected(FeedbackRating.Ok)
            activateCommentButton()
        }
        badButton.setOnClickListener {
            resetRatingButtons()
            badButton.background.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN)
            feedbackInteractionInterface?.feedbackSelected(FeedbackRating.Bad)
            activateCommentButton()
        }
        resetRatingButtons()
    }

    fun createCommentButtonListener(listener:View.OnClickListener){
        additionalButton.setOnClickListener(listener)
    }

    fun setSessionTitle(sessionTitle: String){
        titleTextView?.text = "What did you think of \"${sessionTitle}\"?"
    }

    private fun resetRatingButtons(){
        goodButton.background.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN)
        okButton.background.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN)
        badButton.background.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN)
    }

    private fun activateCommentButton(){
        additionalButton.isEnabled = true
        additionalButton.setTextColor(Color.GRAY)
    }

}