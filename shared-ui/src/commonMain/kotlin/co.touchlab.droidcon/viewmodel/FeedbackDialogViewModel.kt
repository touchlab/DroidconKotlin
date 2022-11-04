package co.touchlab.droidcon.viewmodel

import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.gateway.SessionGateway
import co.touchlab.kermit.Logger
import org.brightify.hyperdrive.multiplatformx.BaseViewModel
import org.brightify.hyperdrive.multiplatformx.property.map

class FeedbackDialogViewModel(
    private val sessionGateway: SessionGateway,
    private val session: Session,
    private val log: Logger,
    private val submit: suspend (Session.Feedback) -> Unit,
    private val closeAndDisable: (suspend () -> Unit)?,
    private val skip: suspend () -> Unit,
) : BaseViewModel() {

    val sessionTitle = session.title
    var rating: Rating? by published(session.feedback?.rating?.let(::feedbackRatingToRating))
    val observeRating by observe(::rating)
    var comment by published(session.feedback?.comment ?: "")
    val observeComment by observe(::comment)

    val isSubmitDisabled by observeRating.map { it == null }
    val observeIsSubmitDisabled by observe(::isSubmitDisabled)

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

    private fun feedbackRatingToRating(rating: Int): Rating? =
        when (rating) {
            Session.Feedback.Rating.DISSATISFIED -> Rating.Dissatisfied
            Session.Feedback.Rating.NORMAL -> Rating.Normal
            Session.Feedback.Rating.SATISFIED -> Rating.Satisfied
            else -> {
                log.w("Unknown feedback rating $rating.")
                null
            }
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
        private val log: Logger,
    ) {

        fun create(
            session: Session,
            submit: suspend (Session.Feedback) -> Unit,
            closeAndDisable: (suspend () -> Unit)?,
            skip: suspend () -> Unit,
        ) = FeedbackDialogViewModel(sessionGateway, session, log, submit, closeAndDisable, skip)
    }
}
