package co.touchlab.sessionize.api

import co.touchlab.droidcon.db.MyPastSession

interface FeedbackApi {

    enum class FeedBackError{
        NoSessions
    }
    fun generateFeedbackDialog(session: MyPastSession)

    fun onError(error:FeedBackError)

}