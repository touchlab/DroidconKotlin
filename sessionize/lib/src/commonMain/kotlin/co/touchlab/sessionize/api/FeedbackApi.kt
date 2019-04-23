package co.touchlab.sessionize.api

import co.touchlab.droidcon.db.MyPastSessions

interface FeedbackApi {

    enum class FeedBackError{
        NoSessions
    }
    fun generateFeedbackDialog(session: MyPastSessions)

    fun onError(error:FeedBackError)

}