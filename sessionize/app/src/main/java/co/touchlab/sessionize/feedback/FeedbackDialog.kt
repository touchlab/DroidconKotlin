package co.touchlab.sessionize.feedback

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import co.touchlab.sessionize.AppContext
import co.touchlab.sessionize.R
import kotlinx.android.synthetic.main.feedback_view.*


interface FeedbackDialogInterface{
    fun finishedFeedback(sessionId:String, rating:Int, comment: String)
}

enum class FeedbackRating(val value: Int) {
    None(0),
    Good(1),
    Ok(2),
    Bad(3)
}

class FeedbackDialog : DialogFragment(),FeedbackInteractionInterface{


    private var ratingView:FeedbackRatingView? = null
    private var commentView:FeedbackCommentView? = null

    private var doneButton:Button? = null

    private var feedbackInterface:FeedbackDialogInterface? = null

    private val animationTime = 400L

    private var sessionId:Int? = null
    private var sessionTitle:String? = null
    private var rating:FeedbackRating = FeedbackRating.None
    private var comments:String = ""


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {

            val feedbackView = createFeedbackView(requireActivity().layoutInflater)

            val builder = AlertDialog.Builder(it)
            builder.setView(feedbackView)
                    .setNegativeButton("Close and Disable Feedback", DialogInterface.OnClickListener { dialog, _ ->
                        AppContext.disableFeedback()
                        dialog.dismiss()
                    })

            val dialog = builder.create()
            dialog.setOnShowListener {
                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.GRAY)
            }
            return dialog
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun createFeedbackView(inflater: LayoutInflater):View{
        val view = inflater.inflate(R.layout.feedback_view, null)
        view?.let { fbView ->
            doneButton = fbView.findViewById(R.id.doneButton)
            doneButton?.setOnClickListener {
                finishAndClose()
            }
            doneButton?.isEnabled = false

            initRatingView(fbView)
            initCommentView(fbView)
        }
        return view
    }

    private fun initRatingView(feedbackView:View){
        ratingView = feedbackView.findViewById(R.id.ratingView)
        ratingView?.createButtonListeners()
        ratingView?.setFeedbackInteractionListener(this)
        ratingView?.createCommentButtonListener(View.OnClickListener {
            showCommentView()
        })
        sessionTitle?.let {
            ratingView?.setSessionTitle(it)

        }

    }

    private fun initCommentView(feedbackView:View){
        commentView = feedbackView.findViewById(R.id.commentView)
        commentView?.visibility = View.INVISIBLE
    }


    fun setFeedbackDialogInterface(feedbackDialogInterface: FeedbackDialogInterface){
        this.feedbackInterface = feedbackDialogInterface
    }

    fun setSessionInfo(sessionId: Int,sessionTitle:String){
        this.sessionId = sessionId
        this.sessionTitle = sessionTitle
        ratingView?.setSessionTitle(sessionTitle)
    }


    private fun finishAndClose(){
        commentView?.getComment()?.let {
            comments = it
        }
        feedbackInterface?.finishedFeedback(sessionId?.toString()!!,rating.value,comments)
        dismiss()
    }

    private fun showCommentView(){
        ratingView?.isEnabled = false
        commentView?.visibility = View.VISIBLE
        animateOut(ratingView!!)
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
        animate.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                v.x = -v.width.toFloat()-100
                commentView?.setFocus()

            }

        })
    }

    override fun feedbackSelected(rating:FeedbackRating){
        doneButton?.isEnabled = true
        additionalButton?.isEnabled = true
        this.rating = rating
    }

}

interface FeedbackInteractionInterface {
    fun feedbackSelected(rating:FeedbackRating)
}