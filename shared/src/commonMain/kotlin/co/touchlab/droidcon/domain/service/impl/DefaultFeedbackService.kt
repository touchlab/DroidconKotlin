package co.touchlab.droidcon.domain.service.impl

import co.touchlab.droidcon.domain.entity.Session
import co.touchlab.droidcon.domain.gateway.SessionGateway
import co.touchlab.droidcon.domain.service.FeedbackService
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.set
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSettingsApi::class)
class DefaultFeedbackService(
    private val sessionGateway: SessionGateway,
    private val settings: ObservableSettings,
    private val json: Json,
    private val clock: Clock,
): FeedbackService {
    companion object {
        private const val COMPLETED_SESSION_FEEDBACKS_KEY = "COMPLETED_SESSION_FEEDBACKS"
    }

    private var completedSessionIds: Set<String> = settings.getStringOrNull(COMPLETED_SESSION_FEEDBACKS_KEY)?.let {
        json.decodeFromString(it)
    } ?: emptySet()

    private var sessionsToReview: List<Session> = emptyList()

    private val initializationCompletable = CompletableDeferred<Unit>()

    init {
        MainScope().launch {
            sessionGateway.observeAgenda()
                .collect { sessions ->
                    sessionsToReview = sessions
                        .map { it.session }
                        .filter { it.endsAt < clock.now() }
                        .filterNot { completedSessionIds.contains(it.id.value) }
                    initializationCompletable.complete(Unit)
                }
        }
    }

    override suspend fun next(): Session? {
        initializationCompletable.await()
        return sessionsToReview.firstOrNull { !completedSessionIds.contains(it.id.value) }
    }

    override suspend fun submit(session: Session, feedback: Session.Feedback) {
        sessionGateway.setFeedback(session, feedback)
        completedSessionIds += session.id.value
        saveCompletedSessions()
    }

    override suspend fun skip(session: Session) {
        completedSessionIds += session.id.value
        saveCompletedSessions()
    }

    private fun saveCompletedSessions() {
        settings[COMPLETED_SESSION_FEEDBACKS_KEY] = json.encodeToString(completedSessionIds)
    }
}