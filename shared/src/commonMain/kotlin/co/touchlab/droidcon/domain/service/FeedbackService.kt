package co.touchlab.droidcon.domain.service

import co.touchlab.droidcon.domain.entity.Session

interface FeedbackService {
    suspend fun next(): Session?

    suspend fun submit(session: Session, feedback: Session.Feedback)

    suspend fun skip(session: Session)
}
