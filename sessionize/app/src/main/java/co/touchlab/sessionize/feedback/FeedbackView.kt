package co.touchlab.sessionize.feedback

import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import co.touchlab.sessionize.R
import kotlinx.android.synthetic.main.feedback_view.view.*

class FeedbackView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var feedbackViewListener: FeedbackViewListener? = null

    fun setFeedbackViewListener(fbListener: FeedbackViewListener){
        feedbackViewListener = fbListener
    }
    fun createButtonListeners(){
        setDoneButtonEnabled(false)

        goodButton.setOnClickListener {
            resetRatingButtons()
            submitButton.isEnabled = true
            feedbackViewListener?.ratingSelected(FeedbackRating.Good)
            goodButton.background.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN)
            setDoneButtonEnabled(true)
        }
        okButton.setOnClickListener {
            resetRatingButtons()
            submitButton.isEnabled = true
            feedbackViewListener?.ratingSelected(FeedbackRating.Ok)
            okButton.background.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN)
            setDoneButtonEnabled(true)
        }
        badButton.setOnClickListener {
            resetRatingButtons()
            submitButton.isEnabled = true
            feedbackViewListener?.ratingSelected(FeedbackRating.Bad)
            badButton.background.setColorFilter(Color.BLUE, PorterDuff.Mode.SRC_IN)
            setDoneButtonEnabled(true)
        }

        submitButton.isEnabled = false
        submitButton.setOnClickListener {
            feedbackViewListener?.submitPressed(commentEditText.text.toString())
        }
        closeButton.setOnClickListener {
            feedbackViewListener?.closePressed()
        }
        resetRatingButtons()
    }

    fun setSessionTitle(sessionTitle: String){
        titleTextView?.text = "What did you think of \"${sessionTitle}\"?"
    }

    private fun resetRatingButtons(){
        goodButton.background.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN)
        okButton.background.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN)
        badButton.background.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN)
    }

    private fun setDoneButtonEnabled(enabled:Boolean){
        submitButton.isEnabled = enabled
        if(enabled){
            submitButton.background = resources.getDrawable(R.drawable.rounded_blue_button, null)
        }else{
            submitButton.background = resources.getDrawable(R.drawable.rounded_gray_button, null)
        }

    }

    interface FeedbackViewListener {
        fun ratingSelected(rating: FeedbackRating)
        fun submitPressed(comment: String?)
        fun closePressed()
    }
}