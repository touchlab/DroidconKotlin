package co.touchlab.sessionize.feedback

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.TranslateAnimation
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import co.touchlab.droidcon.db.SessionWithRoom
import co.touchlab.sessionize.R


enum class FeedbackRating(val value: Int) {
    Good(0),
    Ok(1),
    Bad(2)
}

class FeedbackDialog : DialogFragment(){

    private var feedbackView:View? = null
    private var selectionView:View? = null
    private var commentView:View? = null
    private var titleTextView:TextView? = null
    private var commentEditText:EditText? = null

    enum class SubviewType(val value:Int) {
        Rating(0),
        Comment(1)
    }
    var subviewIdx:SubviewType = SubviewType.Rating

    private val animationTime = 400L

    var rating:FeedbackRating? = null
    var comments:String? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            feedbackView = inflater.inflate(R.layout.feedback_view, null)


            feedbackView?.let { fbView ->
                titleTextView = fbView.findViewById(R.id.titleTextView)

                selectionView = fbView.findViewById(R.id.selectionView)
                selectionView?.let { selView ->
                    selView.findViewById<ImageButton>(R.id.goodButton)?.let { goodButton ->
                        goodButton.setOnClickListener {
                            feedbackSelected(FeedbackRating.Good)
                        }
                    }
                    selView.findViewById<ImageButton>(R.id.okButton)?.let { okButton ->
                        okButton.setOnClickListener {
                            feedbackSelected(FeedbackRating.Ok)
                        }
                    }
                    selView.findViewById<ImageButton>(R.id.badButton)?.let { badButton ->
                        badButton.setOnClickListener {
                            feedbackSelected(FeedbackRating.Bad)
                        }
                    }
                }

                commentView = fbView.findViewById(R.id.commentView)
                commentView?.let { commView ->
                    commView.findViewById<Button>(R.id.doneButton)?.setOnClickListener {
                        commentEntered(commentEditText!!.text.toString())
                    }
                    commentEditText = commView.findViewById(R.id.commentEditText)
                }
            }

            commentView?.visibility = View.INVISIBLE

            builder.setView(feedbackView)
                    .setNegativeButton("cancel", DialogInterface.OnClickListener { dialog, _ ->
                        dialog.dismiss()
                    })

            val dialog = builder.create()
            dialog.setOnShowListener {
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.GRAY)
            }
            return dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun setSession(session: SessionWithRoom){
        titleTextView?.text = "What did you think of ${session.title} ?"
    }

    private fun feedbackSelected(rating:FeedbackRating){
        this.rating = rating
        finishedViewsFeedback()
    }

    private fun commentEntered(comment: String) {
        this.comments = comment
        finishedViewsFeedback()
    }

    private fun finishedViewsFeedback() {
        when (subviewIdx) {
            SubviewType.Rating -> {
                subviewIdx = SubviewType.Comment
                showCommentView()
            }
            SubviewType.Comment -> finishAndClose()
        }
    }

    private fun finishAndClose(){
        
        dismiss()
    }

    private fun showCommentView(){
        commentView?.visibility = View.VISIBLE
        animateOut(selectionView!!)
        animateIn(commentView!!)

    }

    private fun animateIn(v:View) {

        var animate = TranslateAnimation( v.width.toFloat(),0.0f,
                0.0f,0.0f)
        animate.duration = animationTime
        animate.fillAfter = true
        v.startAnimation(animate)
    }

    private fun animateOut(v:View) {

        var animate = TranslateAnimation( 0.0f,-v.width.toFloat()-100,
            0.0f,0.0f)
        animate.duration = animationTime
        animate.fillAfter = true
        v.startAnimation(animate)
    }

}