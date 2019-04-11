package co.touchlab.sessionize.feedback

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.feedback_view.view.*
import android.view.inputmethod.InputMethodManager

class FeedbackCommentView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    fun setFocus(){
        commentEditText.requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(commentEditText, InputMethodManager.SHOW_IMPLICIT)

    }
    fun getComment():String {
        return commentEditText.text.toString()
    }
}