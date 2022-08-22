package co.touchlab.droidcon.viewmodel

import co.touchlab.droidcon.decompose.interfaceLock
import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.util.DcDispatchers
import co.touchlab.kermit.Logger
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.reduce

class FeedbackDialogComponent(
    componentContext: ComponentContext,
    dispatchers: DcDispatchers,
    session: Session,
    private val log: Logger,
    private val submit: suspend (Session.Feedback) -> Unit,
    private val closeAndDisable: (suspend () -> Unit)?,
    private val skip: suspend () -> Unit,
): ComponentContext by componentContext {

    private val instanceLock = interfaceLock(dispatchers.main)

    private val _model =
        MutableValue(
            Model(
                sessionTitle = session.title,
                rating = session.feedback?.rating?.let(::feedbackRatingToRating),
                comment = session.feedback?.comment ?: "",
                isSubmitDisabled = session.feedback != null,
                showCloseAndDisableOption = closeAndDisable != null,
            )
        )

    val model: Value<Model> get() = _model

    fun setRating(rating: Rating) {
        _model.reduce { it.copy(rating = rating) }
    }

    fun setComment(comment: String) {
        _model.reduce { it.copy(comment = comment) }
    }

    fun submitTapped() = instanceLock.runExclusively {
        val model = _model.value
        model.rating?.let {
            submit(Session.Feedback(it.entityValue, model.comment, false))
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

    data class Model(
        val sessionTitle: String,
        val rating: Rating?,
        val comment: String,
        val isSubmitDisabled: Boolean,
        val showCloseAndDisableOption: Boolean,
    )

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
        private val dispatchers: DcDispatchers,
        private val log: Logger,
    ) {

        fun create(
            componentContext: ComponentContext,
            session: Session,
            submit: suspend (Session.Feedback) -> Unit,
            closeAndDisable: (suspend () -> Unit)?,
            skip: suspend () -> Unit,
        ) = FeedbackDialogComponent(componentContext, dispatchers, session, log, submit, closeAndDisable, skip)
    }
}
