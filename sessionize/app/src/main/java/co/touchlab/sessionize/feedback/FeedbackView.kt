package co.touchlab.sessionize.feedback

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import co.touchlab.sessionize.R
import co.touchlab.sessionize.databinding.FeedbackViewBinding

class FeedbackView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding by lazy {
        FeedbackViewBinding.inflate(LayoutInflater.from(context), this, true)
    }

    private var feedbackViewListener: FeedbackViewListener? = null

    fun setFeedbackViewListener(fbListener: FeedbackViewListener) {
        feedbackViewListener = fbListener
    }

    fun createButtonListeners() {
        binding.run {
            setDoneButtonEnabled(false)

            goodButton.setOnClickListener {
                resetRatingButtons()
                submitButton.isEnabled = true
                feedbackViewListener?.ratingSelected(FeedbackRating.Good)
                goodButton.background.colorFilter = BlendModeColorFilterCompat
                        .createBlendModeColorFilterCompat(Color.BLUE, BlendModeCompat.SRC_IN)
                setDoneButtonEnabled(true)
            }
            okButton.setOnClickListener {
                resetRatingButtons()
                submitButton.isEnabled = true
                feedbackViewListener?.ratingSelected(FeedbackRating.Ok)
                okButton.background.colorFilter = BlendModeColorFilterCompat
                        .createBlendModeColorFilterCompat(Color.BLUE, BlendModeCompat.SRC_IN)
                setDoneButtonEnabled(true)
            }
            badButton.setOnClickListener {
                resetRatingButtons()
                submitButton.isEnabled = true
                feedbackViewListener?.ratingSelected(FeedbackRating.Bad)
                badButton.background.colorFilter = BlendModeColorFilterCompat
                        .createBlendModeColorFilterCompat(Color.BLUE, BlendModeCompat.SRC_IN)
                setDoneButtonEnabled(true)
            }

            submitButton.isEnabled = false
            submitButton.setOnClickListener {
                feedbackViewListener?.submitPressed(commentEditText.text.toString())
            }
            closeButton.setOnClickListener {
                feedbackViewListener?.closePressed()
            }
            skipButton.setOnClickListener {
                feedbackViewListener?.ratingSelected(FeedbackRating.None)
                feedbackViewListener?.submitPressed("")

            }
            resetRatingButtons()
        }
    }

    fun setSessionTitle(sessionTitle: String) {
        findViewById<TextView>(R.id.titleTextView)?.text = "What did you think of \"${sessionTitle}\"?"
    }

    private fun resetRatingButtons() {
        binding.run {
            goodButton.background.colorFilter = BlendModeColorFilterCompat
                    .createBlendModeColorFilterCompat(Color.GRAY, BlendModeCompat.SRC_IN)
            okButton.background.colorFilter = BlendModeColorFilterCompat
                    .createBlendModeColorFilterCompat(Color.GRAY, BlendModeCompat.SRC_IN)
            badButton.background.colorFilter = BlendModeColorFilterCompat
                    .createBlendModeColorFilterCompat(Color.GRAY, BlendModeCompat.SRC_IN)
        }
    }

    private fun setDoneButtonEnabled(enabled: Boolean) {
        binding.run {
            submitButton.isEnabled = enabled
            if (enabled) {
                submitButton.background = resources.getDrawable(R.drawable.rounded_blue_button, null)
            } else {
                submitButton.background = resources.getDrawable(R.drawable.rounded_gray_button, null)
            }
        }
    }

    interface FeedbackViewListener {
        fun ratingSelected(rating: FeedbackRating)
        fun submitPressed(comment: String?)
        fun closePressed()
    }
}