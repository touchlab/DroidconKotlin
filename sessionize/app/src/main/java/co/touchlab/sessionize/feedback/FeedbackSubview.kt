package co.touchlab.sessionize.feedback

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout

open class FeedbackSubview @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    var feedbackInteractionInterface:FeedbackInteractionInterface? = null

    open fun createButtonListeners() {
    }
    fun setFeedbackInteractionListener(listener:FeedbackInteractionInterface){
        feedbackInteractionInterface = listener
    }
}