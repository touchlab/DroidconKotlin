package co.touchlab.sessionize.api

import co.touchlab.droidcon.db.MySessions

interface FeedbackApi {

    enum class FeedBackError{
        NoSessions
    }
    fun generateFeedbackDialog(session: MySessions)

    fun onError(error:FeedBackError)

}