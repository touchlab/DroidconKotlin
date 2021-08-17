package co.touchlab.droidcon.android.viewModel.feedback

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.droidcon.R
import co.touchlab.droidcon.domain.entity.Session
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class FeedbackViewModel(
    val session: MutableStateFlow<Session>,
    private val submitFeedback: suspend (Session, Session.Feedback) -> Unit,
    private val closeAndDisableFeedback: suspend () -> Unit,
    private val skipFeedback: suspend (Session) -> Unit,
): ViewModel() {

    val title: Flow<String> = session.map { it.title }
    val reactions: List<Reaction> = listOf(Reaction.Bad, Reaction.Normal, Reaction.Good)
    val comment: MutableStateFlow<String> = MutableStateFlow("")
    val selectedReaction: MutableStateFlow<Reaction?> = MutableStateFlow(null)

    val isSubmitDisabled: Flow<Boolean> = selectedReaction.map { it == null }

    fun submit() {
        viewModelScope.launch {
            selectedReaction.value?.let {
                submitFeedback(session.value, Session.Feedback(it.toFeedbackRating(), comment.value, false))
            }
        }
    }

    fun closeAndDisable() {
        viewModelScope.launch {
            closeAndDisableFeedback()
        }
    }

    fun skip() {
        viewModelScope.launch {
            skipFeedback(session.value)
        }
    }

    private fun Reaction.toFeedbackRating(): Int =
        when (this) {
            Reaction.Bad -> Session.Feedback.Rating.DISSATISFIED
            Reaction.Normal -> Session.Feedback.Rating.NORMAL
            Reaction.Good -> Session.Feedback.Rating.SATISFIED
        }

    sealed class Reaction(@StringRes val descriptionRes: Int, @DrawableRes val imageRes: Int) {
        object Bad: Reaction(R.string.feedback_reaction_bad_description, R.drawable.baseline_sentiment_very_dissatisfied_24)
        object Normal: Reaction(R.string.feedback_reaction_normal_description, R.drawable.baseline_sentiment_satisfied_24)
        object Good: Reaction(R.string.feedback_reaction_good_description, R.drawable.baseline_sentiment_satisfied_alt_24)
    }
}