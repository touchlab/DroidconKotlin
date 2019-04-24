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
import co.touchlab.sessionize.R
import kotlinx.android.synthetic.main.feedback_view.*
import kotlinx.android.synthetic.main.feedback_view.view.*

enum class FeedbackRating(val value: Int) {
    None(0),
    Good(1),
    Ok(2),
    Bad(3)
}

class FeedbackDialog : DialogFragment(),FeedbackInteractionInterface{


    lateinit var sessionId: String
    lateinit var sessionTitle: String
    private var feedbackManager: FeedbackManager? = null

    private var ratingView:FeedbackRatingView? = null
    private var commentView:FeedbackCommentView? = null

    private var doneButton:Button? = null

    private val animationTime = 400L

    private var rating:FeedbackRating = FeedbackRating.None
    private var comments:String = ""


    companion object {
        private const val sessionIdName = "sessionId"
        private const val sessionTitleName = "sessionTitle"

        fun newInstance(sessionId: String, sessionTitle: String, feedbackManager: FeedbackManager) : FeedbackDialog{
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



            val feedbackView = createFeedbackView(requireActivity().layoutInflater)

            val builder = AlertDialog.Builder(it)
            builder.setView(feedbackView)
                    .setNegativeButton("Close and Disable Feedback", DialogInterface.OnClickListener { dialog, _ ->
                        feedbackManager?.disableFeedback()
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
            doneButton = fbView.doneButton
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
        ratingView = feedbackView.ratingView
        ratingView?.createButtonListeners()
        ratingView?.setFeedbackInteractionListener(this)
        ratingView?.createCommentButtonListener(View.OnClickListener {
            showCommentView()
        })
        sessionTitle.let {
            ratingView?.setSessionTitle(it)
        }

    }

    private fun initCommentView(feedbackView:View){
        commentView = feedbackView.commentView
        commentView?.createButtonListeners()
        commentView?.setFeedbackInteractionListener(this)
        commentView?.visibility = View.INVISIBLE
    }

    fun setFeedbackManager(fbManager: FeedbackManager){
        this.feedbackManager = fbManager
    }

    private fun finishAndClose(){
        commentView?.getComment()?.let {
            comments = it
        }
        feedbackManager?.finishedFeedback(sessionId,rating.value,comments)
        dismiss()
    }

    private fun showCommentView(){
        commentView?.visibility = View.VISIBLE
        ratingView?.isEnabled = false
        commentView?.isEnabled = true
        animateOut(ratingView!!,false)
        animateIn(commentView!!,true)
    }

    private fun animateIn(v:View,fromRight: Boolean) {

        val xDelta = if(fromRight) v.width.toFloat() else -v.width.toFloat()-100
        val animate = TranslateAnimation( xDelta,0.0f,
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
                v.x = 0F
            }

        })
    }

    private fun animateOut(v:View, toRight: Boolean) {

        val xDelta = if(toRight) v.width.toFloat() else -v.width.toFloat()-100
        val animate = TranslateAnimation( 0.0f,xDelta,
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
                v.x = if(toRight) v.width.toFloat() else -v.width.toFloat()-100
                v.visibility = View.INVISIBLE
                if(v != commentView) {
                    commentView?.setFocus()
                }else{
                    commentView?.hideFocus()
                }

            }

        })
    }

    override fun feedbackSelected(rating:FeedbackRating){
        doneButton?.isEnabled = true
        additionalButton?.isEnabled = true
        this.rating = rating
    }

    override fun showFeedbackView(){
        ratingView?.visibility = View.VISIBLE
        ratingView?.isEnabled = true
        commentView?.isEnabled = false
        animateOut(commentView!!,true)
        animateIn(ratingView!!,false)
    }

}

interface FeedbackInteractionInterface {
    fun feedbackSelected(rating:FeedbackRating)
    fun showFeedbackView()

}