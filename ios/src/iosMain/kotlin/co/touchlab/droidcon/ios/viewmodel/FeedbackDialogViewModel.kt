package co.touchlab.droidcon.ios.viewmodel

import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.gateway.SessionGateway
import org.brightify.hyperdrive.multiplatformx.BaseViewModel
import org.brightify.hyperdrive.multiplatformx.property.map

class FeedbackDialogViewModel(
    private val sessionGateway: SessionGateway,
    private val session: Session,
    private val submit: suspend (Session.Feedback) -> Unit,
    private val closeAndDisable: (suspend () -> Unit)?,
    private val skip: suspend () -> Unit,
): BaseViewModel() {

    val sessionTitle = session.title
    var rating: Rating? by published(session.feedback?.rating?.let(::feedbackRatingToRating))
    private val observeRating by observe(::rating)
    var comment by published(session.feedback?.comment ?: "")

    val isSubmitDisabled by observeRating.map { it == null }

    val showCloseAndDisableOption: Boolean = closeAndDisable != null

    fun submitTapped() = instanceLock.runExclusively {
        rating?.let {
            submit(Session.Feedback(it.entityValue, comment, false))
        }
    }

    fun closeAndDisableTapped() = instanceLock.runExclusively {
        closeAndDisable?.invoke()
    }

    fun skipTapped() = instanceLock.runExclusively(skip::invoke)

    private fun feedbackRatingToRating(rating: Int): Rating =
        when (rating) {
            Session.Feedback.Rating.DISSATISFIED -> Rating.Dissatisfied
            Session.Feedback.Rating.NORMAL -> Rating.Normal
            Session.Feedback.Rating.SATISFIED -> Rating.Satisfied
            else -> throw IllegalStateException("Unknown feedback rating $rating.")
        }

    enum class Rating {
        Dissatisfied, Normal, Satisfied;

        val entityValue: Int
            get() = when (this) {
                Dissatisfied -> Session.Feedback.Rating.DISSATISFIED
                Normal -> Session.Feedback.Rating.NORMAL
                Satisfied -> Session.Feedback.Rating.SATISFIED
            }
    }

    class Factory(
        private val sessionGateway: SessionGateway,
    ) {

        fun create(
            session: Session,
            submit: suspend (Session.Feedback) -> Unit,
            closeAndDisable: (suspend () -> Unit)?,
            skip: suspend () -> Unit,
        ) = FeedbackDialogViewModel(sessionGateway, session, submit, closeAndDisable, skip)
    }
}
