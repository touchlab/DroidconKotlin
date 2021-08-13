package co.touchlab.droidcon.ios.viewmodel

import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.gateway.SessionGateway
import org.brightify.hyperdrive.multiplatformx.BaseViewModel
import org.brightify.hyperdrive.multiplatformx.property.map

class FeedbackDialogViewModel(
    private val sessionGateway: SessionGateway,
    private val session: Session,
): BaseViewModel() {
    val sessionTitle = session.title
    var rating: Rating? by published(null)
    private val observeRating by observe(::rating)
    var comment by published("")

    val isSubmitDisabled by observeRating.map { it == null }

    fun submit() = instanceLock.runExclusively{
        val rating = rating ?: return@runExclusively

        sessionGateway.setFeedback(
            session,
            Session.Feedback(rating.entityValue, comment, false)
        )

        // TODO: Go to next.
    }

    fun closeAndDisable() {
        // TODO: Close and disable further feedback dialogs.
    }

    fun skip() {
        // TODO: Go to next.
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
        fun create(session: Session) = FeedbackDialogViewModel(sessionGateway, session)
    }
}