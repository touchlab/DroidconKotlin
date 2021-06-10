package co.touchlab.sessionize.api

import co.touchlab.sessionize.jsondata.Days
import co.touchlab.sessionize.jsondata.Speaker
import co.touchlab.sessionize.jsondata.SponsorSessionGroup

interface SessionizeApi {
    suspend fun getSpeakers(): List<Speaker>

    suspend fun getSessions(): List<Days>

    suspend fun getSponsorSession(): List<SponsorSessionGroup>

    suspend fun recordRsvp(methodName: String, sessionId: String): Boolean

    suspend fun sendFeedback(sessionId: String, rating: Int, comment: String?): Boolean
}