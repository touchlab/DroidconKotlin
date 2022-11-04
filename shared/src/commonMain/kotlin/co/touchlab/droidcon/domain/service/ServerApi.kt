package co.touchlab.droidcon.domain.service

import co.touchlab.droidcon.domain.entity.Session

interface ServerApi {
    suspend fun setRsvp(sessionId: Session.Id, isAttending: Boolean): Boolean

    suspend fun setFeedback(sessionId: Session.Id, rating: Int, comment: String): Boolean
}
