package co.touchlab.sessionize.api

interface SessionizeApi {
    suspend fun getSpeakersJson(): String

    suspend fun getSessionsJson(): String

    suspend fun getSponsorSessionJson(): String

    suspend fun recordRsvp(methodName: String, sessionId: String): Boolean

    suspend fun sendFeedback(sessionId: String, rating: Int, comment: String?): Boolean
}